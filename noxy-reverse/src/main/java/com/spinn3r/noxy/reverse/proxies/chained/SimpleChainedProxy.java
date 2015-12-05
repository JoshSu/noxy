package com.spinn3r.noxy.reverse.proxies.chained;

import org.littleshoot.proxy.ChainedProxyAdapter;

import java.net.InetSocketAddress;

/**
 *
 */
public class SimpleChainedProxy extends ChainedProxyAdapter {

    private final InetSocketAddress chainedProxyAddress;

    public SimpleChainedProxy(InetSocketAddress chainedProxyAddress) {
        this.chainedProxyAddress = chainedProxyAddress;
    }

    @Override
    public InetSocketAddress getChainedProxyAddress() {
        return chainedProxyAddress;
    }

}
