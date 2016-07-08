package com.spinn3r.noxy.forward.init;

import com.google.inject.Inject;
import com.spinn3r.artemis.http.init.DebugWebserverReferencesService;
import com.spinn3r.artemis.http.init.DefaultWebserverReferencesService;
import com.spinn3r.artemis.http.init.WebserverService;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.metrics.init.MetricsService;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import com.spinn3r.artemis.network.builder.proxies.ProxyReference;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.artemis.time.init.UptimeService;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.TestServiceReferences;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 */
public class ForwardProxyServiceTest extends BaseLauncherTest {

    @Inject
    HttpRequestBuilder httpRequestBuilder;

    @Inject
    ForwardProxyPorts forwardProxyPorts;

    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp( new TestServiceReferences() );

    }

    @Test
    public void testRequestsWithProxyService() throws Exception {

        int port = forwardProxyPorts.getPort( "server0" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );
        String contentWithEncoding = httpRequestBuilder.get( "http://msnbc.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "<title>MSNBC" ) );

    }

    @Test
    public void testRequestsWithProxyServiceUsingSSL() throws Exception {

        int port = forwardProxyPorts.getPort( "server0" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );
        String contentWithEncoding = httpRequestBuilder.get( "https://www.google.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "<title>Google</title>" ) );

    }

    @Test
    public void testTestOnSecondaryServer() throws Exception {

        int port = forwardProxyPorts.getPort( "server1" );

        ProxyReference proxyReference = Proxies.create( String.format( "http://localhost:%s", port ) );
        String contentWithEncoding = httpRequestBuilder.get( "http://msnbc.com" ).withProxy( proxyReference ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "<title>MSNBC" ) );

    }

    @Test
    @Ignore
    public void testBulkRequests1() throws Exception {

        int port = forwardProxyPorts.getPort( "server1" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );

        int nrRequest = 100;

        for (int i = 0; i < nrRequest; i++) {

            String contentWithEncoding = httpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

            assertThat( contentWithEncoding, containsString( "CNN" ) );

        }

    }

    @Test
    @Ignore
    public void testBulkRequestsWithEcho() throws Exception {

        int port = forwardProxyPorts.getPort( "server1" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );

        int nrRequest = 5000;

        for (int i = 0; i < nrRequest; i++) {

            String contentWithEncoding = httpRequestBuilder.get( "http://localhost:7000/echo?message=hello" ).withProxy( proxy ).execute().getContentWithEncoding();

            assertEquals( "hello", contentWithEncoding );

        }

    }


    @Test
    public void testHttpHeaders() throws Exception {

        int port = forwardProxyPorts.getPort( "server1" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );
        String contentWithEncoding = httpRequestBuilder.get( "https://httpbin.org/get" ).withProxy( proxy ).execute().getContentWithEncoding();

        System.out.printf( "%s\n", contentWithEncoding );

    }

}