package com.spinn3r.noxy.forward;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 *
 */
@Ignore
public class TestWithLotsOfServers {

    private InboundInetSocketAllocator inboundInetSocketAllocator = new InboundInetSocketAllocator();

    @Test
    public void testWithLotsOfAddresses() throws Exception {

        List<String> addresses = createAddresses( 64, 254 );

        Stopwatch stopwatch = Stopwatch.createStarted();

        InetSocketAddress inboundInetSockAddr = inboundInetSocketAllocator.allocate();
        InetSocketAddress outboundInetSockAddr = new InetSocketAddress( addresses.remove( 0 ), 0 );

        HttpProxyServer proto = create( DefaultHttpProxyServer.bootstrap(),
                                        inboundInetSockAddr,
                                        outboundInetSockAddr );

        int allocated = 1;

        for (String address : addresses) {

            inboundInetSockAddr = inboundInetSocketAllocator.allocate();
            outboundInetSockAddr = new InetSocketAddress( address, 0 );

            create( proto.clone(), inboundInetSockAddr, outboundInetSockAddr );
            System.out.printf( "Allocated %,d proxies\n", ++allocated );
        }

        System.out.printf( "Listening to %,d addresses in %s\n", addresses.size(), stopwatch.stop() );

        //Thread.sleep( Long.MAX_VALUE );

    }

    private HttpProxyServer create( HttpProxyServerBootstrap httpProxyServerBootstrap, InetSocketAddress inboundInetSockAddr, InetSocketAddress outboundInetSockAddr ) throws Exception {

        try {

            System.out.printf( "Creating bootstrap listening on %s and bound to %s\n", inboundInetSockAddr, outboundInetSockAddr );

            return httpProxyServerBootstrap
                     .withName( String.format( "%s:%s", inboundInetSockAddr.getHostName(), inboundInetSockAddr.getPort() ) )
                     .withAddress( inboundInetSockAddr )
                     .withNetworkInterface( outboundInetSockAddr )
                     .start();
        } catch (Exception e) {
            throw new Exception( "Failed to create proxy on: " + inboundInetSockAddr, e );
        }

    }


    private List<String> createAddresses( int nrNetworks, int nrHostsPerNetwork ) {

        List<String> result = Lists.newArrayList();

        for (int i = 0; i < nrNetworks; i++) {
            for (int j = 1; j < nrHostsPerNetwork; j++) {
                result.add( String.format( "10.1.%s.%s", i, j ) );
            }
        }

        return result;
    }

    /*
     ip address add 10.2.0.1/16 dev eth0
     ip address add 10.2.0.2/16 dev eth0
     ip address add 10.2.0.3/16 dev eth0
     ip address add 10.2.0.4/16 dev eth0
     ip address add 10.2.0.5/16 dev eth0
     ip address add 10.2.0.6/16 dev eth0
     ip address add 10.2.0.7/16 dev eth0
     ip address add 10.2.0.8/16 dev eth0
     ip address add 10.2.0.9/16 dev eth0

    */
    class InboundInetSocketAllocator {

        private String network = "10.2.0";

        private int startingPort = 8000;
        private int endPort = 8999;

        private int startingHostIP = 1;

        private int port = startingPort;

        private int hostIP = startingHostIP;

        public InetSocketAddress allocate() {
            String host = String.format( "%s.%s", network, hostIP );
            InetSocketAddress inetSocketAddress = new InetSocketAddress( host, port++ );

            if ( port >= endPort ) {
                rollover();
            }

            return inetSocketAddress;
        }

        public void rollover() {
            System.out.printf( "Rolling over for next address...\n" );

            hostIP++;
            port = startingPort;
        }

    }

}
