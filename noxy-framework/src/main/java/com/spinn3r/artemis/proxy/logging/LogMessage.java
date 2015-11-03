package com.spinn3r.artemis.proxy.logging;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;

/**
 * Used so that we can trace which HTTP requests/responses have been executed.
 */
public class LogMessage {

    private final HttpMethod httpMethod;

    private final HttpVersion protocolVersion;

    private final String uri;

    private final HttpResponseStatus httpResponseStatus;

    private final long duration;

    private final String resolutionServerHostAndPort;

    private final InetSocketAddress resolvedRemoteAddress;

    public LogMessage(HttpMethod httpMethod, HttpVersion protocolVersion, String uri, HttpResponseStatus httpResponseStatus, long duration, String resolutionServerHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        this.httpMethod = httpMethod;
        this.protocolVersion = protocolVersion;
        this.uri = uri;
        this.httpResponseStatus = httpResponseStatus;
        this.duration = duration;
        this.resolutionServerHostAndPort = resolutionServerHostAndPort;
        this.resolvedRemoteAddress = resolvedRemoteAddress;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpVersion getProtocolVersion() {
        return protocolVersion;
    }

    public String getUri() {
        return uri;
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public long getDuration() {
        return duration;
    }

    public String getResolutionServerHostAndPort() {
        return resolutionServerHostAndPort;
    }

    public InetSocketAddress getResolvedRemoteAddress() {
        return resolvedRemoteAddress;
    }

    @Override
    public String toString() {
        return "LogMessage{" +
                 "httpMethod=" + httpMethod +
                 ", protocolVersion=" + protocolVersion +
                 ", uri='" + uri + '\'' +
                 ", httpResponseStatus=" + httpResponseStatus +
                 ", duration=" + duration +
                 ", resolutionServerHostAndPort='" + resolutionServerHostAndPort + '\'' +
                 ", resolvedRemoteAddress=" + resolvedRemoteAddress +
                 '}';
    }

}
