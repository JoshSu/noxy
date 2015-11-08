package com.spinn3r.noxy.reverse.init;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.AtomicReferenceProvider;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.noxy.logging.Log5jLogListener;
import com.spinn3r.noxy.logging.LoggingHttpFiltersSourceAdapterFactory;
import com.spinn3r.noxy.reverse.LoadBalancingReverseProxyHostResolver;
import com.spinn3r.noxy.reverse.checks.CheckDaemon;
import com.spinn3r.noxy.reverse.checks.CheckDaemonFactory;
import com.spinn3r.noxy.reverse.filters.DefaultHttpFiltersSourceAdapter;
import com.spinn3r.noxy.reverse.meta.*;
import com.spinn3r.artemis.util.net.HostPort;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

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

    private final ReverseProxyConfig reverseProxyConfig;

    private final LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory;

    private final List<ListenerMeta> listenerMetas = Lists.newCopyOnWriteArrayList();

    private final CheckDaemonFactory checkDaemonFactory;

    private final AtomicReferenceProvider<ListenerMetaIndex> listenerMetaIndexProvider = new AtomicReferenceProvider<>( null );

    @Inject
    ReverseProxyService(ReverseProxyConfig reverseProxyConfig, LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory, CheckDaemonFactory checkDaemonFactory) {
        this.reverseProxyConfig = reverseProxyConfig;
        this.loggingHttpFiltersSourceAdapterFactory = loggingHttpFiltersSourceAdapterFactory;
        this.checkDaemonFactory = checkDaemonFactory;
    }


    @Override
    public void init() {
        provider( ListenerMetaIndex.class, listenerMetaIndexProvider );
    }

    @Override
    public void start() throws Exception {

        for (Listener listener : reverseProxyConfig.getListeners()) {

            List<HostPort> serverAddresses = Lists.newArrayList();

            for ( Server server : listener.getServers() ) {
                serverAddresses.add( new HostPort( server.getAddress() ) );
            }

            ServerMetaIndex serverMetaIndex = ServerMetaIndex.fromServers( listener.getServers() );

            ServerMetaIndexProvider serverMetaIndexProvider = new ServerMetaIndexProvider();
            serverMetaIndexProvider.set( serverMetaIndex );

            // initially we don't have any hosts online
            OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = new OnlineServerMetaIndexProvider();

            HostResolver hostResolver = new LoadBalancingReverseProxyHostResolver( onlineServerMetaIndexProvider );

            HttpProxyServerBootstrap httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap();

            httpProxyServerBootstrap
              .withAddress( new HostPort( listener.getBinding().getAddress() ).toInetSocketAddress() )
              .withServerResolver( hostResolver )
              ;

            if ( listener.getName() != null ) {
                httpProxyServerBootstrap.withName( listener.getName() );
            }

            HttpFiltersSourceAdapter httpFiltersSourceAdapterDelegate = new HttpFiltersSourceAdapter();

            if ( listener.getLogging() ) {
                Log5jLogListener log5jLogListener = new Log5jLogListener();
                httpFiltersSourceAdapterDelegate = loggingHttpFiltersSourceAdapterFactory.create( log5jLogListener );
                info( "HTTP request log enabled for proxy requests." );
            }

            httpProxyServerBootstrap.withFiltersSource( new DefaultHttpFiltersSourceAdapter( httpFiltersSourceAdapterDelegate, onlineServerMetaIndexProvider ) );

            httpProxyServerBootstrap.withConnectTimeout( listener.getConnectTimeout() );

            HttpProxyServer httpProxyServer = httpProxyServerBootstrap.start();

            ExecutorService executorService = Executors.newCachedThreadPool();

            CheckDaemon checkDaemon = checkDaemonFactory.create( listener, serverMetaIndexProvider, onlineServerMetaIndexProvider );

            executorService.submit( checkDaemon );

            ListenerMeta listenerMeta = new ListenerMeta( listener, onlineServerMetaIndexProvider, checkDaemon, httpProxyServer, executorService );

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
