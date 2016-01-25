package com.spinn3r.noxy.forward.init;

import com.spinn3r.artemis.init.resource_mutexes.PortMutex;
import org.littleshoot.proxy.HttpProxyServer;

/**
 *
 */
public class ProxyServerMeta {

    private final HttpProxyServer httpProxyServer;

    private final PortMutex portMutex;

    public ProxyServerMeta(HttpProxyServer httpProxyServer, PortMutex portMutex) {
        this.httpProxyServer = httpProxyServer;
        this.portMutex = portMutex;
    }

    public HttpProxyServer getHttpProxyServer() {
        return httpProxyServer;
    }

    public PortMutex getPortMutex() {
        return portMutex;
    }

}
