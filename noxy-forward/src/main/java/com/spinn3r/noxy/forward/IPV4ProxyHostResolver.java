package com.spinn3r.noxy.forward;

import java.net.*;

/**
 *
 */
public class IPV4ProxyHostResolver extends BaseHostResolver {

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {

        InetAddress inetAddress = randomize( filter( Inet4Address.getAllByName( host ), Inet4Address.class ) );

        return new InetSocketAddress( inetAddress, port );

    }

}
