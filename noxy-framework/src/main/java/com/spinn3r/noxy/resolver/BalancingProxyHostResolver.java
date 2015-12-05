package com.spinn3r.noxy.resolver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Uses the standard resolver BUT balances properly by randomizing.
 */
public class BalancingProxyHostResolver extends BaseHostResolver {

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {

        InetAddress[] inetAddresses = InetAddress.getAllByName( host );

        if ( inetAddresses == null || inetAddresses.length == 0 ) {
            return null;
        }

        InetAddress inetAddress = randomize( toList( inetAddresses ) );

        if ( inetAddress == null ) {
            return null;
        }

        return new InetSocketAddress( inetAddress, port );

    }

}
