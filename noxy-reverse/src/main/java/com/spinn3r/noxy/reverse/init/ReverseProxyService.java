package com.spinn3r.noxy.reverse.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.AtomicReferenceProvider;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.artemis.init.resource_mutexes.PortMutex;
import com.spinn3r.artemis.init.resource_mutexes.PortMutexes;
import com.spinn3r.log5j.Logger;
import com.spinn3r.noxy.Allocator;
import com.spinn3r.noxy.discovery.*;
import com.spinn3r.noxy.logging.CompositeLogListener;
import com.spinn3r.noxy.logging.Log5jLogListener;
import com.spinn3r.noxy.logging.LogListener;
import com.spinn3r.noxy.logging.LoggingHttpFiltersSourceAdapterFactory;
import com.spinn3r.noxy.logging.instrumented.MetricsLogListener;
import com.spinn3r.noxy.resolver.BalancingProxyHostResolver;
import com.spinn3r.noxy.reverse.LoadBalancingReverseProxyHostResolver;
import com.spinn3r.noxy.reverse.checks.CheckDaemon;
import com.spinn3r.noxy.reverse.checks.CheckDaemonFactory;
import com.spinn3r.noxy.reverse.filters.ReverseProxyHttpFiltersSourceAdapter;
import com.spinn3r.noxy.reverse.meta.*;
import com.spinn3r.artemis.util.net.HostPort;
import com.spinn3r.noxy.reverse.proxies.chained.BalancingChainedProxyManager;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.UnknownHostException;
import java.util.List;
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

    private final PortMutexes portMutexes;

    private final MetricsLogListener metricsLogListener;

    private final ListenerPorts listenerPorts = new ListenerPorts();

    private final AtomicReferenceProvider<ListenerPorts> listenerPortsProvider = new AtomicReferenceProvider<>( listenerPorts );

    @Inject
    ReverseProxyService(ReverseProxyConfig reverseProxyConfig, LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory, CheckDaemonFactory checkDaemonFactory, DiscoveryFactory discoveryFactory, PortMutexes portMutexes, MetricsLogListener metricsLogListener) {
        this.reverseProxyConfig = reverseProxyConfig;
        this.loggingHttpFiltersSourceAdapterFactory = loggingHttpFiltersSourceAdapterFactory;
        this.checkDaemonFactory = checkDaemonFactory;
        this.discoveryFactory = discoveryFactory;
        this.portMutexes = portMutexes;
        this.metricsLogListener = metricsLogListener;
    }

    @Override
    public void init() {
        provider( ListenerMetaIndex.class, listenerMetaIndexProvider );
        provider( ListenerPorts.class, listenerPortsProvider );

        if ( reverseProxyConfig.getAllocator().equals( Allocator.POOLED ) ) {

            // http://netty.io/4.0/xref/io/netty/buffer/ByteBufUtil.html
            //
            // we might also have to set:
            //
            // io.netty.threadLocalDirectBufferSize
            // io.netty.maxThreadLocalCharBufferSize

            info( "Enabling pooled allocator for netty" );
            // this is a non-ideal way to set the allocator but right now
            // littleproxy doesn't expose this setting directly.
            System.setProperty( "io.netty.allocator.type", "pooled" );
        }

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

                ChainedProxyManager chainedProxyManager
                  = new BalancingChainedProxyManager( listener, onlineServerMetaIndexProvider );

                httpProxyServerBootstrap.withChainProxyManager( chainedProxyManager );

            }

            HostPort hostPort = new HostPort( listener.getBinding().getAddress() );

            PortMutex portMutex = null;

            if ( hostPort.getPort() <= 0 ) {
                portMutex = portMutexes.acquire( 8082, 9081 );
                hostPort = new HostPort( hostPort.getHostname(), portMutex.getPort() );
            }

            listenerPorts.register( listener.getName(), hostPort.getPort() );

            httpProxyServerBootstrap
              .withAddress( hostPort.toInetSocketAddress() )
              .withServerResolver( hostResolver )
              ;

            if ( listener.getName() != null ) {
                httpProxyServerBootstrap.withName( listener.getName() );
            }

            HttpFiltersSourceAdapter httpFiltersSourceAdapter = new HttpFiltersSourceAdapter();

            List<LogListener> compositeLogListenerDelegates = Lists.newArrayList();

            if ( listener.getLogging() ) {
                info( "HTTP request logging enabled for proxy requests." );
                Log5jLogListener log5jLogListener = new Log5jLogListener( listener.getTracing() );
                compositeLogListenerDelegates.add( log5jLogListener );
            }

            if ( listener.getMetrics() ) {
                info( "HTTP request metrics enabled for proxy requests." );
                compositeLogListenerDelegates.add( metricsLogListener );
            }

            if ( compositeLogListenerDelegates.size() > 0 ) {
                CompositeLogListener compositeLogListener = new CompositeLogListener( ImmutableList.copyOf( compositeLogListenerDelegates ) );
                httpFiltersSourceAdapter = loggingHttpFiltersSourceAdapterFactory.create( compositeLogListener );
            }

            httpProxyServerBootstrap.withFiltersSource( new ReverseProxyHttpFiltersSourceAdapter( listener, httpFiltersSourceAdapter, onlineServerMetaIndexProvider ) );

            httpProxyServerBootstrap.withConnectTimeout( listener.getConnectTimeout() );

            info( "Starting listener %s on %s:%s", listener.getName(), hostPort.getHostname(), hostPort.getPort() );

            HttpProxyServer httpProxyServer = httpProxyServerBootstrap.start();

            ExecutorService executorService = Executors.newCachedThreadPool();

            CheckDaemon checkDaemon = checkDaemonFactory.create( listener, serverMetaIndexProvider, onlineServerMetaIndexProvider );

            executorService.submit( checkDaemon );

            ListenerMeta listenerMeta = new ListenerMeta( listener,
                                                          serverMetaIndexProvider,
                                                          onlineServerMetaIndexProvider,
                                                          checkDaemon,
                                                          httpProxyServer,
                                                          executorService,
                                                          portMutex );

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

            if( listenerMeta.getPortMutex() != null ) {
                listenerMeta.getPortMutex().close();
            }

        }

    }

}
