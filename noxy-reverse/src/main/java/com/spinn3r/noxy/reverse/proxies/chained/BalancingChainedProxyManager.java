package com.spinn3r.noxy.reverse.proxies.chained;

import com.spinn3r.log5j.Logger;
import com.spinn3r.noxy.reverse.init.Listener;
import com.spinn3r.noxy.reverse.meta.OnlineServerMetaIndexProvider;
import com.spinn3r.noxy.reverse.meta.ServerMeta;
import com.spinn3r.noxy.reverse.meta.ServerMetaIndex;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyManager;

import java.util.Queue;

/**
 *
 */
public class BalancingChainedProxyManager implements ChainedProxyManager {

    private static final Logger log = Logger.getLogger();

    private final Listener listener;

    private final OnlineServerMetaIndexProvider onlineServerMetaIndexProvider;

    public BalancingChainedProxyManager(Listener listener, OnlineServerMetaIndexProvider onlineServerMetaIndexProvider) {
        this.listener = listener;
        this.onlineServerMetaIndexProvider = onlineServerMetaIndexProvider;
    }

    @Override
    public void lookupChainedProxies(HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {

        ServerMetaIndex onlineProxyServers = onlineServerMetaIndexProvider.get();
        ServerMeta proxyServerMeta = onlineProxyServers.getBalancer().next();

        if ( proxyServerMeta != null ) {

            // TODO: might want to make this debug in the future.
            log.info( "Using proxy server (%,d available) for request: %s (%s)",
                      onlineProxyServers.getServers().size(), proxyServerMeta.getServer().getAddress(), proxyServerMeta.getServer().getName() );

            chainedProxies.add( new SimpleChainedProxy( proxyServerMeta.getInetSocketAddress() ) );

        } else {
            log.warn( "No online servers available for request on listener: %s (with %,d servers)",
                      listener.getName(), onlineProxyServers.getServers().size() );
        }

    }

}
