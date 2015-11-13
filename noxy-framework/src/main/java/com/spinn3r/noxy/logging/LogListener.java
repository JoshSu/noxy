package com.spinn3r.noxy.logging;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * A listener that can listen to the requests being proxied.
 */
public interface LogListener {

    void onLogMessage( LogMessage logMessage );

    void onSecureLogMessage( SecureLogMessage secureLogMessage );

    void clientToProxyRequest(HttpObject httpObject);

    void proxyToClientResponse(HttpObject httpObject);

}
