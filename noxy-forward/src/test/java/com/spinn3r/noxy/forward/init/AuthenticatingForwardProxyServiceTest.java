package com.spinn3r.noxy.forward.init;

import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.artemis.init.config.ResourceConfigLoader;
import com.spinn3r.artemis.network.NetworkException;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import com.spinn3r.artemis.network.builder.proxies.ProxyReference;
import com.spinn3r.noxy.forward.TestServiceReferences;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 */
public class AuthenticatingForwardProxyServiceTest extends BaseLauncherTest {

    @Inject
    HttpRequestBuilder httpRequestBuilder;

    @Inject
    ForwardProxyPorts forwardProxyPorts;

    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp( new ResourceConfigLoader( "/profiles/authentication" ),  new TestServiceReferences() );

    }

    @Test
    public void testRequestsWithProxyServiceWithFailedAuthentication() throws Exception {

        try {

            int port = forwardProxyPorts.getPort( "server0" );

            ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );
            String contentWithEncoding = httpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

            assertThat( contentWithEncoding, containsString( "CNN" ) );

            throw new NetworkException( "proxy should have failed due to lack of auth" );

        } catch ( NetworkException ne ) {
            assertEquals( 407, ne.getResponseCode() );

        }

    }

    @Test
    public void testRequestsWithProperProxyAuthorization() throws Exception {

        int port = forwardProxyPorts.getPort( "server0" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", port ) );

        String contentWithEncoding =
          httpRequestBuilder
            .get( "http://msnbc.com" )
            .withRequestHeader( "Proxy-Authorization", "Basic YmF0bWFuOjEyMzQ1" )
            .withProxy( proxy ).execute().getContentWithEncoding();

        System.out.printf("FIXME content is now\n%s\n", contentWithEncoding );

        assertThat( contentWithEncoding, containsString( "<title>MSNBC" ) );

    }

}