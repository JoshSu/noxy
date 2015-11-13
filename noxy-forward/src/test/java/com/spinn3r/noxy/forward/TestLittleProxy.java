package com.spinn3r.noxy.forward;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.network.builder.DefaultHttpRequestBuilderService;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 */
@Ignore // FIXME: eventually we need to un-ignore this once we decide to make
        // this production.
public class TestLittleProxy extends BaseLauncherTest {

    int PORT = 2080;

    @Inject
    HttpRequestBuilder httpRequestBuilder;

    HttpProxyServer httpProxyServer = null;

    Proxy proxy = Proxies.create( String.format( "http://localhost:%s", PORT ) );

    // TODO:
    //
    // - verify that we can log HTTP proxy requests.
    //
    // - (DONE) test ipv6 ... I think this works out of the box...
    //
    // - (DONE) change the IP for the requests based on the port
    //
    // - test that we get decent performance out of it

    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp( MockHostnameService.class,
                     MockVersionService.class,
                     ConsoleLoggingService.class,
                     DefaultHttpRequestBuilderService.class );

        httpProxyServer =
          DefaultHttpProxyServer.bootstrap()
            .withPort(PORT)
            .start();

    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        if ( httpProxyServer != null ) {
            httpProxyServer.stop();
        }

    }

    @Test
    public void testBasicProxyRequests() throws Exception {

        String contentWithEncoding = httpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "CNN" ) );

    }

    @Test
    public void testSSL() throws Exception {

        String contentWithEncoding = httpRequestBuilder.get( "https://www.facebook.com/" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "Facebook" ) );

    }

    @Test
    public void testWithLargeNumberOfPorts() throws Exception {

        int startingPort = 12_000;
        int nrPorts = 5000;
        for (int i = 1; i <= nrPorts; i++) {

            int port = startingPort + i;
            httpProxyServer.clone().withPort( port )
                                   //.withNetworkInterface( )
              .start();

        }

    }

    @Test
    public void testIPv6() throws Exception {

        // fe80::16dd:a9ff:feec:928c/64

        InetSocketAddress inetSocketAddress = new InetSocketAddress( "::1", 80 );

        httpProxyServer.clone().withPort( 8080 )
          .withNetworkInterface( inetSocketAddress )
          .start();

    }

}

