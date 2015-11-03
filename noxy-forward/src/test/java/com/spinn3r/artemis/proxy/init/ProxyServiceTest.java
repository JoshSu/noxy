package com.spinn3r.artemis.proxy.init;

import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.network.builder.DefaultHttpRequestBuilderService;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.Proxy;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 *
 */
@Ignore
public class ProxyServiceTest extends BaseLauncherTest {

    @Inject
    HttpRequestBuilder httpRequestBuilder;

    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp( MockHostnameService.class,
                     MockVersionService.class,
                     ConsoleLoggingService.class,
                     DefaultHttpRequestBuilderService.class,
                     ProxyService.class );

    }

    @Test
    public void testRequestsWithProxyService() throws Exception {

        Proxy proxy = Proxies.create( String.format( "http://localhost:%s", 8080 ) );
        String contentWithEncoding = httpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "CNN" ) );

    }

    @Test
    public void testRequestsWithProxyServiceUsingSSL() throws Exception {

        Proxy proxy = Proxies.create( String.format( "http://localhost:%s", 8080 ) );
        String contentWithEncoding = httpRequestBuilder.get( "https://www.google.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "Facebook" ) );

    }

    @Test
    public void testTestOnSecondaryServer() throws Exception {

        Proxy proxy = Proxies.create( String.format( "http://localhost:%s", 8081 ) );
        String contentWithEncoding = httpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "CNN" ) );

    }

    @Test
    public void testHttpHeaders() throws Exception {

        Proxy proxy = Proxies.create( String.format( "http://localhost:%s", 8081 ) );
        String contentWithEncoding = httpRequestBuilder.get( "https://httpbin.org/get" ).withProxy( proxy ).execute().getContentWithEncoding();

        System.out.printf( "%s\n", contentWithEncoding );

    }

}