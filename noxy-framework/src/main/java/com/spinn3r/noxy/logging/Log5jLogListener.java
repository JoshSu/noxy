package com.spinn3r.noxy.logging;

import com.spinn3r.log5j.Logger;
import io.netty.handler.codec.http.HttpObject;

/**
 * A log listener that just logs messages to log5j (Spinn3r's internal async
 * logger).
 */
public class Log5jLogListener implements LogListener {

    private static final Logger log = Logger.getLogger();

    private final boolean trace;

    public Log5jLogListener() {
        this( false );
    }

    public Log5jLogListener(boolean trace) {
        this.trace = trace;
    }

    @Override
    public void onLogMessage(LogMessage logMessage) {
        log.info( "%s %s %s responseCode=%s, duration=%sms, resolutionHostAndPort=%s, resolvedRemoteAddress=%s",
                  logMessage.getHttpMethod(),
                  logMessage.getUri(),
                  logMessage.getProtocolVersion(),
                  logMessage.getHttpResponseStatus().code(),
                  logMessage.getDuration(),
                  logMessage.getResolutionServerHostAndPort(),
                  logMessage.getResolvedRemoteAddress() );
    }

    @Override
    public void onSecureLogMessage(SecureLogMessage secureLogMessage) {
        log.info( "%s %s %s resolutionHostAndPort=%s, resolvedRemoteAddress=%s",
                  secureLogMessage.getHttpMethod(),
                  secureLogMessage.getUri(),
                  secureLogMessage.getProtocolVersion(),
                  secureLogMessage.getResolutionServerHostAndPort(),
                  secureLogMessage.getResolvedRemoteAddress() );
    }

    @Override
    public void clientToProxyRequest(HttpObject httpObject) {

        if ( trace ) {
            log.info( "clientToProxyRequest (trace): \n%s", httpObject );
        }

    }

    @Override
    public void proxyToClientResponse(HttpObject httpObject) {

        if ( trace ) {
            log.info( "proxyToClientResponse (trace): \n%s", httpObject );
        }

    }

}
