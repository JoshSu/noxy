package com.spinn3r.noxy.logging;

import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.HttpObject;

/**
 * A log listener that forwards its events to delegates.
 */
public class CompositeLogListener implements LogListener {

    private final ImmutableList<LogListener> delegates;

    public CompositeLogListener(ImmutableList<LogListener> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void onLogMessage(LogMessage logMessage) {
        for (LogListener delegate : delegates) {
            delegate.onLogMessage( logMessage );
        }
    }

    @Override
    public void onSecureLogMessage(SecureLogMessage secureLogMessage) {
        for (LogListener delegate : delegates) {
            delegate.onSecureLogMessage( secureLogMessage );
        }
    }

    @Override
    public void clientToProxyRequest(HttpObject httpObject) {
        for (LogListener delegate : delegates) {
            delegate.clientToProxyRequest( httpObject );
        }
    }

    @Override
    public void proxyToClientResponse(HttpObject httpObject) {
        for (LogListener delegate : delegates) {
            delegate.proxyToClientResponse( httpObject );
        }
    }

}
