package com.spinn3r.noxy.reverse.net;

import com.google.common.net.InetAddresses;
import com.spinn3r.artemis.util.net.HostPort;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class InetSocketAddresses {

    public static InetSocketAddress parse( String address ) throws UnknownHostException {

        HostPort hostPort = new HostPort( address );

        if (InetAddresses.isInetAddress( hostPort.getHostname() ) ) {
            InetAddress inetAddress = InetAddresses.forString( hostPort.getHostname() );
            return new InetSocketAddress( inetAddress, hostPort.getPort() );
        }

        InetAddress inetAddress = InetAddress.getByName( hostPort.getHostname() );
        return new InetSocketAddress( inetAddress, hostPort.getPort() );

    }

}
