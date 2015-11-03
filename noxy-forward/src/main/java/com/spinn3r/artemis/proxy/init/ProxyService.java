package com.spinn3r.artemis.proxy.init;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.artemis.proxy.logging.Log5jLogListener;
import com.spinn3r.artemis.proxy.logging.LoggingHttpFiltersSourceAdapter;
import com.spinn3r.artemis.proxy.logging.LoggingHttpFiltersSourceAdapterFactory;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Config( path = "proxy.conf",
         required = true,
         implementation = ProxyConfig.class )
public class ProxyService extends BaseService {

    private final ProxyConfig proxyConfig;

    private final LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory;

    private final List<HttpProxyServer> httpProxyServers = Lists.newArrayList();

    @Inject
    ProxyService(ProxyConfig proxyConfig, LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory) {
        this.proxyConfig = proxyConfig;
        this.loggingHttpFiltersSourceAdapterFactory = loggingHttpFiltersSourceAdapterFactory;
    }

    @Override
    public void init() {
    }

    @Override
    public void start() throws Exception {

        List<ProxyServerDescriptor> servers = proxyConfig.getServers();

        if ( servers == null || servers.size() == 0 ) {
            warn( "No servers defined." );
            return;
        }

        HttpProxyServer proto = create( DefaultHttpProxyServer.bootstrap(), servers.get( 0 ) );
        httpProxyServers.add( proto );

        for (int i = 1; i < servers.size(); i++) {
            ProxyServerDescriptor proxyServerDescriptor = servers.get( i );
            httpProxyServers.add( create( proto.clone(), proxyServerDescriptor ) );
        }

    }

    private HttpProxyServer create( HttpProxyServerBootstrap httpProxyServerBootstrap, ProxyServerDescriptor proxyServerDescriptor ) {

        info( "Creating proxy server: %s", proxyServerDescriptor );

        InetSocketAddress address = new InetSocketAddress( proxyServerDescriptor.getInbound().getAddress(), proxyServerDescriptor.getInbound().getPort() );
        InetSocketAddress networkInterface = new InetSocketAddress( proxyServerDescriptor.getOutbound().getAddress(), proxyServerDescriptor.getOutbound().getPort() );

        String name = proxyServerDescriptor.getName();

        if ( name == null ) {
            name = proxyServerDescriptor.getInbound().getAddress() + ":" + proxyServerDescriptor.getInbound().getPort();
        }

        // TODO: use a custom HostResolver for ipv6 and then one for ipv4 depending
        // on which mode a connection is taking.

        httpProxyServerBootstrap
          .withName( name )
          .withAddress( address )
          .withNetworkInterface( networkInterface );

        if ( proxyConfig.getEnableRequestLogging() ) {
            Log5jLogListener log5jLogListener = new Log5jLogListener();
            LoggingHttpFiltersSourceAdapter loggingHttpFiltersSourceAdapter = loggingHttpFiltersSourceAdapterFactory.create( log5jLogListener );
            httpProxyServerBootstrap.withFiltersSource( loggingHttpFiltersSourceAdapter );
        }

        return httpProxyServerBootstrap.start();

    }

    @Override
    public void stop() throws Exception {

        List<HttpProxyServer> httpProxyServersReversed = Lists.newArrayList( httpProxyServers );
        Collections.reverse( httpProxyServersReversed );

        for (HttpProxyServer httpProxyServer : httpProxyServersReversed ) {
            httpProxyServer.stop();
        }

    }

}
