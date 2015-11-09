package com.spinn3r.noxy.reverse;

import com.codahale.metrics.servlets.PingServlet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.artemis.http.ServerBuilder;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.network.NetworkException;
import com.spinn3r.artemis.network.builder.HttpRequest;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.noxy.discovery.support.init.DiscoveryListenerSupportService;
import com.spinn3r.noxy.reverse.init.ReverseProxyService;
import com.spinn3r.noxy.reverse.meta.ListenerMeta;
import com.spinn3r.noxy.reverse.meta.ListenerMetaIndex;
import com.spinn3r.noxy.reverse.meta.OnlineServerMetaIndexProvider;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

import static com.jayway.awaitility.Awaitility.*;
import static org.junit.Assert.*;

/**
 *
 */
public class ReverseProxyServiceTest extends BaseLauncherTest {

    @Inject
    HttpRequestBuilder httpRequestBuilder;

    @Inject
    PingServlet pingServlet;

    @Inject
    Provider<ListenerMetaIndex> listenerMetaIndexProvider;

    Map<String, Server> httpDaemonMap = new HashMap<>();

    @Override
    @Before
    public void setUp() throws Exception {

        super.setUp( MockHostnameService.class,
                     MockVersionService.class,
                     DirectNetworkService.class,
                     ConsoleLoggingService.class,
                     DiscoveryListenerSupportService.class,
                     ReverseProxyService.class );

        int startingPort = 1880;
        int nrHttpDaemons = 3;

        for (int i = 0; i < nrHttpDaemons; i++) {

            int port = startingPort + i;

            ServerBuilder serverBuilder = new ServerBuilder();

            Server server =
              serverBuilder
                .setPort( port )
                .setMaxThreads( 10 )
                .setUseLocalhost( true )
                .addServlet( "/ping", pingServlet )
                .build();

            server.start();

            httpDaemonMap.put( ":" + port, server );

        }

    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        for (Server server : httpDaemonMap.values()) {
            server.stop();
        }

    }

    @Test
    public void testBasicRequests() throws Exception {

        ListenerMeta listenerMeta = listenerMetaIndexProvider.get().getListenerMetas().get( 0 );

        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();

        await().until( () -> {
            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 3 ) );
        } );

        String content = fetch( "http://example.com/ping" );
        assertThat( content, containsString( "pong" ) );

        // now stop all the daemons to make sure things go offline.

        for (Server server : httpDaemonMap.values()) {
            server.stop();
        }

        await().until( () -> {
            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 0 ) );
        } );

        // now start them again to make sure they come back online.

        for (Server server : httpDaemonMap.values()) {
            server.start();
        }

        await().until( () -> {
            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 3 ) );
        } );

    }

    @Test
    public void testVerifyBadGatewayWithNoBackendServers() throws Exception {

        ListenerMeta listenerMeta = listenerMetaIndexProvider.get().getListenerMetas().get( 0 );

        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();

        for (Server server : httpDaemonMap.values()) {
            server.stop();
        }

        await().until( () -> {
            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 0 ) );
        } );

        // the backend servers should be down now so we should get a 502.
        assertEquals( 502, status( "http://example.com/ping" ) );

    }

    private String fetch( String link ) throws NetworkException {

        return httpRequestBuilder
                 .get( link )
                 .withProxy( Proxies.create( "http://localhost:8181" ) )
                 .execute()
                 .getContentWithEncoding();

    }

    // return the status code of the request
    private int status( String link ) {

        try {

            HttpRequest httpRequest =
              httpRequestBuilder
                .get( link )
                .withProxy( Proxies.create( "http://localhost:8181" ) )
                .execute();

            httpRequest.connect();

            return httpRequest.getResponseCode();

        } catch (NetworkException e) {
            return e.getResponseCode();
        }


    }

}