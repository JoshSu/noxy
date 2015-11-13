package com.spinn3r.noxy.logging;

import com.spinn3r.artemis.time.Clock;
import com.spinn3r.log5j.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import java.net.InetSocketAddress;

/**
 * Implements HTTP request logging so that we can see the requests that are
 * actually being used against the server.
 */
public class LoggingHttpFiltersSourceAdapter extends HttpFiltersSourceAdapter {

    private static final Logger log = Logger.getLogger();

    private final Clock clock;

    private final LogListener logListener;

    public LoggingHttpFiltersSourceAdapter(Clock clock, LogListener logListener) {
        this.clock = clock;
        this.logListener = logListener;
    }

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest) {
        return new LoggingHttpFiltersAdapter( originalRequest );
    }

    class LoggingHttpFiltersAdapter extends HttpFiltersAdapter {

        private HttpRequest httpRequest;
        private long httpRequestTimestamp;
        private String httpRequestUri;
        private String resolutionServerHostAndPort;
        private InetSocketAddress resolvedRemoteAddress;

        public LoggingHttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            super( originalRequest, ctx );
        }

        public LoggingHttpFiltersAdapter(HttpRequest originalRequest) {
            super( originalRequest );
        }

        @Override
        public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
            this.resolutionServerHostAndPort = serverHostAndPort;
            this.resolvedRemoteAddress = resolvedRemoteAddress;
        }

        @Override
        public HttpResponse clientToProxyRequest(HttpObject httpObject) {

            if ( httpObject instanceof HttpRequest ) {
                httpRequest = (HttpRequest)httpObject;
                httpRequestTimestamp = clock.currentTimeMillis();
                httpRequestUri = httpRequest.getUri();

                if ( httpRequest.getMethod().equals( HttpMethod.CONNECT ) ) {
                    SecureLogMessage secureLogMessage = new SecureLogMessage( httpRequest.getMethod(), httpRequest.getProtocolVersion(), httpRequestUri, resolutionServerHostAndPort, resolvedRemoteAddress );
                    logListener.onSecureLogMessage( secureLogMessage );
                }

            }

            logListener.clientToProxyRequest( httpObject );

            return super.clientToProxyRequest( httpObject );

        }

        @Override
        public HttpObject proxyToClientResponse(HttpObject httpObject) {

            if ( httpObject instanceof HttpResponse ) {

                HttpResponse httpResponse = (HttpResponse)httpObject;

                long duration = clock.currentTimeMillis() - httpRequestTimestamp;
                LogMessage logMessage = new LogMessage( httpRequest.getMethod(), httpRequest.getProtocolVersion(), httpRequestUri, httpResponse.getStatus(), duration, resolutionServerHostAndPort, resolvedRemoteAddress );
                logListener.onLogMessage( logMessage );

            }

            logListener.proxyToClientResponse( httpObject );

            return super.proxyToClientResponse( httpObject );

        }


    }

}
