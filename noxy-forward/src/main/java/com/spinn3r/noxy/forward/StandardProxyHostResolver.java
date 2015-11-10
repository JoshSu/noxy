package com.spinn3r.noxy.forward;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class StandardProxyHostResolver extends BaseHostResolver {

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {

        InetAddress inetAddress = InetAddress.getByName( host );

        if ( inetAddress == null ) {
            return null;
        }

        return new InetSocketAddress( inetAddress, port );

    }

}
