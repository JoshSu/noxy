package com.spinn3r.noxy.reverse;

import com.spinn3r.noxy.reverse.meta.Balancer;
import com.spinn3r.noxy.reverse.meta.OnlineServerMetaIndexProvider;
import com.spinn3r.noxy.reverse.meta.ServerMeta;
import com.spinn3r.noxy.reverse.meta.ServerMetaIndex;
import com.spinn3r.noxy.reverse.net.InetSocketAddresses;
import org.littleshoot.proxy.HostResolver;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Implements a host resolver for mapping to backend hosts.  This is a simple
 * load balancing resolver.
 */
public class LoadBalancingReverseProxyHostResolver implements HostResolver {

    private final OnlineServerMetaIndexProvider onlineServerMetaIndexProvider;

    public LoadBalancingReverseProxyHostResolver(OnlineServerMetaIndexProvider onlineServerMetaIndexProvider) throws UnknownHostException {
        this.onlineServerMetaIndexProvider = onlineServerMetaIndexProvider;
    }

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {

        ServerMetaIndex serverMetaIndex = this.onlineServerMetaIndexProvider.get();
        Balancer<ServerMeta> balancer = serverMetaIndex.getBalancer();
        ServerMeta next = balancer.next();

        if ( next == null ) {
            // go to the local host in this situation so we generate a bad gateway
            return InetSocketAddresses.parse( "127.0.0.1:0" );
        }

        return next.getInetSocketAddress();

    }

}
