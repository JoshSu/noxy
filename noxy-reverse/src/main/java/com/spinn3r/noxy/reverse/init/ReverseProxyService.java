package com.spinn3r.noxy.reverse.init;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.AtomicReferenceProvider;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.log5j.Logger;
import com.spinn3r.noxy.discovery.*;
import com.spinn3r.noxy.logging.Log5jLogListener;
import com.spinn3r.noxy.logging.LoggingHttpFiltersSourceAdapterFactory;
import com.spinn3r.noxy.resolver.BalancingProxyHostResolver;
import com.spinn3r.noxy.reverse.LoadBalancingReverseProxyHostResolver;
import com.spinn3r.noxy.reverse.checks.CheckDaemon;
import com.spinn3r.noxy.reverse.checks.CheckDaemonFactory;
import com.spinn3r.noxy.reverse.filters.ReverseProxyHttpFiltersSourceAdapter;
import com.spinn3r.noxy.reverse.meta.*;
import com.spinn3r.artemis.util.net.HostPort;
import com.spinn3r.noxy.reverse.proxies.chained.SimpleChainedProxy;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
@Config( path = "reverse-proxy.conf",
         required = true,
         implementation = ReverseProxyConfig.class )
public class ReverseProxyService extends BaseService {

    private static final Logger log = Logger.getLogger();

    private final ReverseProxyConfig reverseProxyConfig;

    private final LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory;

    private final List<ListenerMeta> listenerMetas = Lists.newCopyOnWriteArrayList();

    private final CheckDaemonFactory checkDaemonFactory;

    private final AtomicReferenceProvider<ListenerMetaIndex> listenerMetaIndexProvider = new AtomicReferenceProvider<>( null );

    private final DiscoveryFactory discoveryFactory;

    @Inject
    ReverseProxyService(ReverseProxyConfig reverseProxyConfig, LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory, CheckDaemonFactory checkDaemonFactory, DiscoveryFactory discoveryFactory) {
        this.reverseProxyConfig = reverseProxyConfig;
        this.loggingHttpFiltersSourceAdapterFactory = loggingHttpFiltersSourceAdapterFactory;
        this.checkDaemonFactory = checkDaemonFactory;
        this.discoveryFactory = discoveryFactory;
    }


    @Override
    public void init() {
        provider( ListenerMetaIndex.class, listenerMetaIndexProvider );
    }

    @Override
    public void start() throws Exception {

        //TODO: in the future we can share the bootstrap and clone it.
        // this would improve performance and scalability.

        for (Listener listener : reverseProxyConfig.getListeners()) {

            Cluster cluster = listener.getCluster();

            ServerMetaIndex serverMetaIndex = new ServerMetaIndex();

            DiscoveryListener discoveryListener = new DiscoveryListener() {
                @Override
                public void onJoin(Endpoint endpoint) {

                    try {

                        Server server = Servers.apply( listener.getServerTemplate(), endpoint );
                        ServerMeta serverMeta = new ServerMeta( server );

                        serverMetaIndex.add( serverMeta );

                        log.info( "Added endpoint %s to cluster %s", endpoint, cluster );

                    } catch (UnknownHostException e) {
                        log.error( "Unable to add discovered server to balancer: " + endpoint, e );
                    }

                }

                @Override
                public void onLeave(Endpoint endpoint) {
                    serverMetaIndex.remove(  serverMetaIndex.key( endpoint ) );
                }

            };

            Discovery discovery = discoveryFactory.create( cluster, discoveryListener );

            ServerMetaIndexProvider serverMetaIndexProvider = new ServerMetaIndexProvider();
            serverMetaIndexProvider.set( serverMetaIndex );

            // initially we don't have any hosts online
            OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = new OnlineServerMetaIndexProvider();

            HostResolver hostResolver = new LoadBalancingReverseProxyHostResolver( onlineServerMetaIndexProvider );

            HttpProxyServerBootstrap httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap();

            if ( EndpointType.FORWARD_PROXY.equals( listener.getEndpointTypes() ) ) {

                // DO NOT use the load balancing resolver because we're using
                // changed proxies here.
                hostResolver = new BalancingProxyHostResolver();

                httpProxyServerBootstrap.withChainProxyManager( new ChainedProxyManager() {
                    @Override
                    public void lookupChainedProxies(HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {

                        ServerMetaIndex onlineProxyServers = onlineServerMetaIndexProvider.get();
                        ServerMeta proxyServerMeta = onlineProxyServers.getBalancer().next();

                        if ( proxyServerMeta != null ) {
                            chainedProxies.add( new SimpleChainedProxy( proxyServerMeta.getInetSocketAddress() ) );
                        }
                    }

                } );

            }

            httpProxyServerBootstrap
              .withAddress( new HostPort( listener.getBinding().getAddress() ).toInetSocketAddress() )
              .withServerResolver( hostResolver )
              ;

            if ( listener.getName() != null ) {
                httpProxyServerBootstrap.withName( listener.getName() );
            }

            HttpFiltersSourceAdapter httpFiltersSourceAdapterDelegate = new HttpFiltersSourceAdapter();

            if ( listener.getLogging() ) {
                Log5jLogListener log5jLogListener = new Log5jLogListener( listener.getTracing() );
                httpFiltersSourceAdapterDelegate = loggingHttpFiltersSourceAdapterFactory.create( log5jLogListener );
                info( "HTTP request log enabled for proxy requests." );
            }

            httpProxyServerBootstrap.withFiltersSource( new ReverseProxyHttpFiltersSourceAdapter( httpFiltersSourceAdapterDelegate, onlineServerMetaIndexProvider ) );

            httpProxyServerBootstrap.withConnectTimeout( listener.getConnectTimeout() );

            HttpProxyServer httpProxyServer = httpProxyServerBootstrap.start();

            ExecutorService executorService = Executors.newCachedThreadPool();

            CheckDaemon checkDaemon = checkDaemonFactory.create( listener, serverMetaIndexProvider, onlineServerMetaIndexProvider );

            executorService.submit( checkDaemon );

            ListenerMeta listenerMeta = new ListenerMeta( listener,
                                                          serverMetaIndexProvider,
                                                          onlineServerMetaIndexProvider,
                                                          checkDaemon,
                                                          httpProxyServer,
                                                          executorService );

            listenerMetas.add( listenerMeta );

        }

        listenerMetaIndexProvider.set( new ListenerMetaIndex( listenerMetas ) );

    }

    @Override
    public void stop() throws Exception {

        for (ListenerMeta listenerMeta : listenerMetas) {
            info( "Stopping listener named: %s", listenerMeta.getListener().getName() );
            listenerMeta.getHttpProxyServer().stop();
            listenerMeta.getCheckDaemon().markTerminated();
            listenerMeta.getExecutorService().shutdown();
        }

    }

}
