package com.spinn3r.noxy.logging;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;

/**
 * SSL proxy connect request which only has basic info since its encrypted.
 */
public class SecureLogMessage {

    private final HttpMethod httpMethod;

    private final HttpVersion protocolVersion;

    private final String uri;

    private final String resolutionServerHostAndPort;

    private final InetSocketAddress resolvedRemoteAddress;

    public SecureLogMessage(HttpMethod httpMethod, HttpVersion protocolVersion, String uri, String resolutionServerHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        this.httpMethod = httpMethod;
        this.protocolVersion = protocolVersion;
        this.uri = uri;
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

    public String getResolutionServerHostAndPort() {
        return resolutionServerHostAndPort;
    }

    public InetSocketAddress getResolvedRemoteAddress() {
        return resolvedRemoteAddress;
    }

    @Override
    public String toString() {
        return "SecureLogMessage{" +
                 "httpMethod=" + httpMethod +
                 ", protocolVersion=" + protocolVersion +
                 ", uri='" + uri + '\'' +
                 ", resolutionServerHostAndPort='" + resolutionServerHostAndPort + '\'' +
                 ", resolvedRemoteAddress=" + resolvedRemoteAddress +
                 '}';
    }
}
