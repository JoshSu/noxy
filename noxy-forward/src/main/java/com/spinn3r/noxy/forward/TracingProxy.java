package com.spinn3r.noxy.forward;

import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;

/**
 * Simple/prototype HTTP proxy for tracing requests..
 */
public class TracingProxy {

    public static void main(String[] args) {

        HttpProxyServerBootstrap httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap();

        InetSocketAddress address = new InetSocketAddress( "192.168.2.210", 1984 );
        InetSocketAddress network = new InetSocketAddress( "192.168.2.210", 0 );

        httpProxyServerBootstrap
                 .withName( "tracing-proxy" )
                 .withAddress( address )
                 .withNetworkInterface( network )
                 .withFiltersSource( new TracingHttpFiltersSourceAdapter() )
                 .start();

    }

}
