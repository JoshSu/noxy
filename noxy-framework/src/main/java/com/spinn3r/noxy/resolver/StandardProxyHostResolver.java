package com.spinn3r.noxy.resolver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * The standard proxy host resolver. The only problem with this one is that
 * it always fetches the first host an doesn't randomize them.
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
