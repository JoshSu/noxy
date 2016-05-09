package com.spinn3r.noxy;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.artemis.http.init.DebugWebserverReferencesService;
import com.spinn3r.artemis.http.init.DefaultWebserverReferencesService;
import com.spinn3r.artemis.http.init.WebserverPort;
import com.spinn3r.artemis.http.init.WebserverService;
import com.spinn3r.artemis.init.*;
import com.spinn3r.artemis.init.config.TestResourcesConfigLoader;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.metrics.init.MetricsService;
import com.spinn3r.artemis.network.NetworkException;
import com.spinn3r.artemis.network.builder.DirectHttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import com.spinn3r.artemis.network.builder.proxies.ProxyReference;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.artemis.time.init.SystemClockService;
import com.spinn3r.artemis.time.init.UptimeService;
import com.spinn3r.artemis.util.io.Sockets;
import com.spinn3r.noxy.discovery.support.init.DiscoveryListenerSupportService;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.init.ForwardProxyPorts;
import com.spinn3r.noxy.forward.init.ForwardProxyService;
import com.spinn3r.noxy.reverse.admin.init.ReverseProxyAdminWebserverReferencesService;
import com.spinn3r.noxy.reverse.init.ListenerPorts;
import com.spinn3r.noxy.reverse.init.ReverseProxyService;
import com.spinn3r.noxy.reverse.meta.ListenerMeta;
import com.spinn3r.noxy.reverse.meta.ListenerMetaIndex;
import com.spinn3r.noxy.reverse.meta.OnlineServerMetaIndexProvider;
import com.spinn3r.noxy.reverse.meta.ServerMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.Proxy;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test using a reverse proxy pointing to a forward proxy pointing to the public
 * Internet and using zookeeper to have each component discovery each other.
 *
 */
@SuppressWarnings( "deprecation" )
public class ReverseThenForwardProxyIntegrationTest extends com.spinn3r.artemis.test.zookeeper.BaseZookeeperTest {

    // TODO: see if we can measure the number of keep alive requests...

    @Inject
    DirectHttpRequestBuilder directHttpRequestBuilder;

    @Inject
    WebserverPort webserverPort;

    ForwardProxyComponents forwardProxyComponents;

    ReverseProxyComponents reverseProxyComponents;

    Launcher forwardProxyLauncher;

    Launcher reverseProxyLauncher;

    Launcher mainLauncher;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        forwardProxyLauncher = launchForwardProxy();
        reverseProxyLauncher = launchReverseProxy();

        forwardProxyComponents = new ForwardProxyComponents();
        forwardProxyLauncher.getInjector().injectMembers( forwardProxyComponents );

        forwardProxyComponents = new ForwardProxyComponents();
        forwardProxyLauncher.getInjector().injectMembers( forwardProxyComponents );

        reverseProxyComponents = new ReverseProxyComponents();
        reverseProxyLauncher.getInjector().injectMembers( reverseProxyComponents );

        mainLauncher = Launcher.forResourceConfigLoader().build();
        mainLauncher.launch( new MainServiceReferences() );
        mainLauncher.getInjector().injectMembers( this );

    }

    @Override
    @After
    public void tearDown() throws Exception {

        if ( reverseProxyLauncher != null )
            reverseProxyLauncher.stop();

        if ( forwardProxyLauncher != null )
            forwardProxyLauncher.stop();

        if ( mainLauncher != null )
            mainLauncher.stop();

        super.tearDown();
    }

    @Test
    public void testChecksBringingForwardProxyOnline() throws Exception {

        // ok.. both services should be up and running.. wait for the components to be up
        // and running

        assertNotNull( reverseProxyComponents );
        assertNotNull( reverseProxyComponents.listenerMetaIndexProvider );

        ListenerMeta listenerMeta = reverseProxyComponents.listenerMetaIndexProvider.get().getListenerMetas().get( 0 );

        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();

        await().until( () -> {
            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 2 ) );
        } );

    }

    @Test
    public void testStatusAPI() throws Exception {

        // ok.. both services should be up and running.. wait for the components to be up
        // and running

        ListenerMeta listenerMeta = reverseProxyComponents.listenerMetaIndexProvider.get().getListenerMetas().get( 0 );

        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();

        await().until( () -> {
            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 2 ) );
        } );


        String url = String.format( "http://localhost:%s/status", webserverPort.getPort() );

        String status = directHttpRequestBuilder.get( url ).execute().getContentWithEncoding();

        System.out.printf( "%s\n", status );

    }

    @Test
    @Ignore
    public void testBulkRequestsWithEcho() throws Exception {

        int forwardProxyPort = forwardProxyComponents.forwardProxyPorts.getPort( "server0" );

        ProxyReference proxy = Proxies.create( String.format( "http://127.0.0.1:%s", forwardProxyPort ) );

        Sockets.waitForOpenPort( "127.0.0.1", forwardProxyPort );
        Sockets.waitForOpenPort( "127.0.0.1", webserverPort.getPort() );

        NetworkTests.test( 500, () -> {

            String url = String.format( "http://127.0.0.1:%s/echo?message=hello", webserverPort.getPort() );

            String contentWithEncoding =
              directHttpRequestBuilder
                .get( url )
                .withProxy( proxy )
                .execute()
                .getContentWithEncoding();

            assertEquals( "hello", contentWithEncoding );

        } );

    }

    @Test
    @Ignore
    public void testCNNBulkRequests1() throws Exception {

        int forwardProxyPort = forwardProxyComponents.forwardProxyPorts.getPort( "server0" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", forwardProxyPort ) );

        int nrRequest = 100;

        for (int i = 0; i < nrRequest; i++) {

            String contentWithEncoding = directHttpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

            assertThat( contentWithEncoding, containsString( "CNN" ) );

        }

    }

    @Test
    public void testMSNBC1() throws Exception {

        await().until( () -> {
            ImmutableList<ServerMeta> onlineServers = getOnlineServersFromReverseProxy();
            assertThat( onlineServers.size(), greaterThan( 0 ) );
          } );

        int forwardProxyPort = forwardProxyComponents.forwardProxyPorts.getPort( "server0" );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", forwardProxyPort ) );

        String contentWithEncoding = directHttpRequestBuilder.get( "http://msnbc.com" ).withProxy( proxy ).execute().getContentWithEncoding();

        assertThat( contentWithEncoding, containsString( "<title>MSNBC:" ) );

    }

    /**
     * Stop the running proxy servers, make sure they are stopped, then verify that
     * we have a Bad Gateway exception..
     *
     * @throws Exception
     */
    @Test
    public void testWithoutRunningForwardProxyServers() throws Exception {

        int reverseProxyPort = reverseProxyComponents.listenerPortsProvider.get().getPort( "default" );

        forwardProxyLauncher.stop();

        await().until( () -> {
            ImmutableList<ServerMeta> onlineServers = getOnlineServersFromReverseProxy();
            assertThat( onlineServers.size(), equalTo( 0 ) );
        } );

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", reverseProxyPort ) );

        try {
            directHttpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();
        } catch (NetworkException e) {
            assertEquals( 502, e.getResponseCode() );
            return;
        }

        throw new RuntimeException( "Didn't get 502 response code" );

    }

    private ImmutableList<ServerMeta> getOnlineServersFromReverseProxy() {

        ListenerMetaIndex listenerMetaIndex = reverseProxyComponents.listenerMetaIndexProvider.get();

        ListenerMeta listenerMeta = listenerMetaIndex.getListenerMetas().get( 0 );

        return listenerMeta.getOnlineServerMetaIndexProvider().get()
            .getOnlineServers();

    }


    private Launcher launchForwardProxy() throws Exception {

        TestResourcesConfigLoader testResourcesConfigLoader
          = new TestResourcesConfigLoader( "src/test/resources/noxy-forward" );

        Launcher launcher = Launcher.forConfigLoader( testResourcesConfigLoader ).build();

        launcher.launch( new ForwardProxyServiceReferences() );

        return launcher;

    }

    private Launcher launchReverseProxy() throws Exception {

        TestResourcesConfigLoader testResourcesConfigLoader
          = new TestResourcesConfigLoader( "src/test/resources/noxy-reverse" );

        Launcher launcher = Launcher.forConfigLoader(testResourcesConfigLoader).build();

        launcher.launch( new ReverseProxyServiceReferences() );

        return launcher;

    }

    static class ForwardProxyComponents {

        @Inject
        ForwardProxyPorts forwardProxyPorts;

    }

    static class ForwardProxyServiceReferences extends ServiceReferences {

        public ForwardProxyServiceReferences() {

            add( MockHostnameService.class );
            add( MockVersionService.class );
            add( MockCallerService.class );
            add( ConsoleLoggingService.class );
            add( MetricsService.class );
            add( MembershipSupportService.class );
            add( ForwardProxyService.class );

        }

    }

    static class ReverseProxyComponents {

        @Inject
        Provider<ListenerMetaIndex> listenerMetaIndexProvider;

        @Inject
        Provider<ListenerPorts> listenerPortsProvider;

    }

    static class ReverseProxyServiceReferences extends ServiceReferences {

        public ReverseProxyServiceReferences() {

            add( MockHostnameService.class );
            add( MockVersionService.class );
            add( MockCallerService.class );
            add( SystemClockService.class );
            add( ConsoleLoggingService.class );
            add( UptimeService.class );
            add( MetricsService.class );
            add( DefaultWebserverReferencesService.class );
            add( DiscoveryListenerSupportService.class );
            add( ReverseProxyService.class );
            add( ReverseProxyAdminWebserverReferencesService.class );
            add( WebserverService.class );

        }

    }

    static class MainServiceReferences extends ServiceReferences {

        public MainServiceReferences() {

            add( MockVersionService.class );
            add( MockHostnameService.class );
            add( UptimeService.class );
            add( MetricsService.class );
            add( DirectNetworkService.class );
            add( DefaultWebserverReferencesService.class );
            add( DebugWebserverReferencesService.class );
            add( WebserverService.class );

        }

    }

}
