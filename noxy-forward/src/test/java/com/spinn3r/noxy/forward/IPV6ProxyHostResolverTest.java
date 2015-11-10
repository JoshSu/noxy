package com.spinn3r.noxy.forward;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class IPV6ProxyHostResolverTest {

    @Test
    public void testResolve() throws Exception {

        IPV6ProxyHostResolver hostResolver = new IPV6ProxyHostResolver();

        InetSocketAddress inetSocketAddress = hostResolver.resolve( "google.com", 80 );

        assertThat( inetSocketAddress.getAddress().toString(), containsString( ":" ) );

        System.out.printf( "%s\n", inetSocketAddress.getAddress() );

    }

}