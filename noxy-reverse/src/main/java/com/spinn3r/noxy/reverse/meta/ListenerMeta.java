package com.spinn3r.noxy.reverse.meta;

import com.spinn3r.artemis.init.resource_mutexes.PortMutex;
import com.spinn3r.noxy.reverse.checks.CheckDaemon;
import com.spinn3r.noxy.reverse.init.Listener;
import org.littleshoot.proxy.HttpProxyServer;

import java.util.concurrent.ExecutorService;

/**
 *
 */
public class ListenerMeta {

    private final Listener listener;

    private final ServerMetaIndexProvider serverMetaIndexProvider;

    private final OnlineServerMetaIndexProvider onlineServerMetaIndexProvider;

    private final CheckDaemon checkDaemon;

    private final HttpProxyServer httpProxyServer;

    private final ExecutorService executorService;

    private final PortMutex portMutex;

    public ListenerMeta(Listener listener,
                        ServerMetaIndexProvider serverMetaIndexProvider,
                        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider,
                        CheckDaemon checkDaemon,
                        HttpProxyServer httpProxyServer,
                        ExecutorService executorService,
                        PortMutex portMutex) {

        this.listener = listener;
        this.serverMetaIndexProvider = serverMetaIndexProvider;
        this.onlineServerMetaIndexProvider = onlineServerMetaIndexProvider;
        this.checkDaemon = checkDaemon;
        this.httpProxyServer = httpProxyServer;
        this.executorService = executorService;
        this.portMutex = portMutex;
    }

    public Listener getListener() {
        return listener;
    }

    public ServerMetaIndexProvider getServerMetaIndexProvider() {
        return serverMetaIndexProvider;
    }

    public OnlineServerMetaIndexProvider getOnlineServerMetaIndexProvider() {
        return onlineServerMetaIndexProvider;
    }

    public CheckDaemon getCheckDaemon() {
        return checkDaemon;
    }

    public HttpProxyServer getHttpProxyServer() {
        return httpProxyServer;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public PortMutex getPortMutex() {
        return portMutex;
    }

}
