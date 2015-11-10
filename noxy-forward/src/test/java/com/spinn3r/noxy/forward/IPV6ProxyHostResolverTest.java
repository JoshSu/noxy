package com.spinn3r.noxy.forward;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class IPV6ProxyHostResolverTest {

    IPV6ProxyHostResolver hostResolver = new IPV6ProxyHostResolver();

    @Test
    public void testResolve() throws Exception {

        InetSocketAddress inetSocketAddress = hostResolver.resolve( "google.com", 80 );

        assertThat( inetSocketAddress.getAddress().toString(), containsString( ":" ) );

        System.out.printf( "%s\n", inetSocketAddress.getAddress() );

    }

    @Test
    public void testResolveWitNoAddresses() throws Exception {



    }

}