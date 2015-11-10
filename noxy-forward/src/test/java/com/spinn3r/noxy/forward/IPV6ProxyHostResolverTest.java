package com.spinn3r.noxy.forward;

import com.google.common.collect.Lists;
import com.spinn3r.artemis.util.misc.Histograph;
import com.spinn3r.artemis.util.misc.HitIndex;
import org.junit.Before;
import org.junit.Test;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class IPV6ProxyHostResolverTest {

    IPV6ProxyHostResolver hostResolver;

    @Before
    public void setUp() throws Exception {

        hostResolver = new IPV6ProxyHostResolver();

        // give it an explicit random so tests are deterministic
        hostResolver.random = new Random( 0 );

    }

    @Test
    public void testResolve() throws Exception {

        InetSocketAddress inetSocketAddress = hostResolver.resolve( "google.com", 80 );

        assertThat( inetSocketAddress.getAddress().toString(), containsString( ":" ) );

        System.out.printf( "%s\n", inetSocketAddress.getAddress() );

    }

    @Test
    public void testFilterWithNoAddresses() throws Exception {

        InetAddress[] inetAddresses = new InetAddress[0];

        assertEquals( 0, hostResolver.filter( inetAddresses, Inet6Address.class ).size() );

    }

    @Test
    public void testFilterWithOneIPV4Addresses() throws Exception {

        InetAddress[] inetAddresses = new InetAddress[1];

        inetAddresses[0] = InetAddress.getByName( "127.0.0.1" );

        assertEquals( 0, hostResolver.filter( inetAddresses, Inet6Address.class ).size() );

    }

    @Test
    public void testFilterWithOneIPV6Addresses() throws Exception {

        InetAddress[] inetAddresses = new InetAddress[1];

        inetAddresses[0] = InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:200e" );

        assertEquals( 1, hostResolver.filter( inetAddresses, Inet6Address.class ).size() );

    }


    @Test
    public void testFilterWithMixedAddresses() throws Exception {

        InetAddress[] inetAddresses = new InetAddress[2];

        inetAddresses[0] = InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:200e" );
        inetAddresses[1] = InetAddress.getByName( "127.0.0.1" );

        assertEquals( 1, hostResolver.filter( inetAddresses, Inet6Address.class ).size() );

    }

    @Test
    public void testRandomizeWithNoAddresses() throws Exception {

        List<InetAddress> addresses = Lists.newArrayList();

        assertNull( hostResolver.randomize( addresses ) );

    }

    @Test
    public void testRandomizeWithOneAddress() throws Exception {

        List<InetAddress> addresses = Lists.newArrayList();

        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:200e" ) );

        assertNotNull( hostResolver.randomize( addresses ) );

    }

    @Test
    public void testRandomizeWithTwoAddresses() throws Exception {

        List<InetAddress> addresses = Lists.newArrayList();

        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:200e" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:200f" ) );

        assertEquals( "/2607:f8b0:4000:80b:0:0:0:200f", hostResolver.randomize( addresses ).toString() );
        assertEquals( "/2607:f8b0:4000:80b:0:0:0:200f", hostResolver.randomize( addresses ).toString() );
        assertEquals( "/2607:f8b0:4000:80b:0:0:0:200e", hostResolver.randomize( addresses ).toString() );
        assertEquals( "/2607:f8b0:4000:80b:0:0:0:200f", hostResolver.randomize( addresses ).toString() );
        assertEquals( "/2607:f8b0:4000:80b:0:0:0:200f", hostResolver.randomize( addresses ).toString() );

    }

    @Test
    public void testRandomizeDistribution() throws Exception {

        Histograph<String> histograph = new Histograph<>();

        List<InetAddress> addresses = Lists.newArrayList();

        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2000" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2001" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2002" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2003" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2004" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2005" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2006" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2007" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2008" ) );
        addresses.add( InetAddress.getByName( "2607:f8b0:4000:80b:0:0:0:2009" ) );

        for (int i = 0; i < 10000; i++) {
            InetAddress resolved = hostResolver.randomize( addresses );
            histograph.incr( resolved.toString() );
        }

        System.out.printf( "%s\n", histograph.toString() );

        assertEquals( "{/2607:f8b0:4000:80b:0:0:0:2003=1045, /2607:f8b0:4000:80b:0:0:0:2006=1021, /2607:f8b0:4000:80b:0:0:0:2001=1011, /2607:f8b0:4000:80b:0:0:0:2007=1009, /2607:f8b0:4000:80b:0:0:0:2005=1005, /2607:f8b0:4000:80b:0:0:0:2008=1005, /2607:f8b0:4000:80b:0:0:0:2004=998, /2607:f8b0:4000:80b:0:0:0:2000=990, /2607:f8b0:4000:80b:0:0:0:2009=964, /2607:f8b0:4000:80b:0:0:0:2002=952}",
                      histograph.toString() );

    }


}