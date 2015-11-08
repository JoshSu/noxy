package com.spinn3r.noxy.forward;

import com.spinn3r.log5j.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

/**
 * Implements HTTP request logging so that we can see the requests that are
 * actually being used against the server.
 */
public class TracingHttpFiltersSourceAdapter extends HttpFiltersSourceAdapter {

    private static final Logger log = Logger.getLogger();

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest) {

        return new TracingHttpFiltersAdapter( originalRequest );

    }

    class TracingHttpFiltersAdapter extends HttpFiltersAdapter {

        public TracingHttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            super( originalRequest, ctx );
        }

        public TracingHttpFiltersAdapter(HttpRequest originalRequest) {
            super( originalRequest );
        }

        @Override
        public HttpResponse clientToProxyRequest(HttpObject httpObject) {
            System.out.printf( "\n%s\n", httpObject );

            return super.clientToProxyRequest( httpObject );
        }

        @Override
        public HttpObject proxyToClientResponse(HttpObject httpObject) {
            System.out.printf( "\n%s\n", httpObject );

            return super.proxyToClientResponse( httpObject );
        }
    }

}
