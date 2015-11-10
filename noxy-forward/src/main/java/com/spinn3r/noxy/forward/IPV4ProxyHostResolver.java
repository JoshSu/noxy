package com.spinn3r.noxy.forward;

import org.littleshoot.proxy.HostResolver;

import java.net.*;

/**
 *
 */
public class IPV4ProxyHostResolver implements HostResolver {

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {

        InetAddress inetAddress = Inet4Address.getByName( host );

        return new InetSocketAddress( inetAddress, port );

    }

}
