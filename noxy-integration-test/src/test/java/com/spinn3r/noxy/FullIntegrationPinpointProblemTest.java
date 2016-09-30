package com.spinn3r.noxy;

import com.google.inject.Inject;
import com.spinn3r.artemis.http.init.DebugWebserverReferencesService;
import com.spinn3r.artemis.http.init.DefaultWebserverReferencesService;
import com.spinn3r.artemis.http.init.WebserverService;
import com.spinn3r.artemis.init.Launcher;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.init.ServiceReferences;
import com.spinn3r.artemis.init.config.TestResourcesConfigLoader;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.metrics.init.MetricsService;
import com.spinn3r.artemis.network.builder.DirectHttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.ProxyReferences;
import com.spinn3r.artemis.network.builder.proxies.ProxyReference;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.artemis.time.init.SyntheticClockService;
import com.spinn3r.artemis.time.init.UptimeService;
import com.spinn3r.artemis.util.io.Sockets;
import com.spinn3r.noxy.discovery.support.init.DiscoveryListenerSupportService;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.init.ForwardProxyService;
import com.spinn3r.noxy.reverse.init.ReverseProxyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Ignore
@SuppressWarnings( "deprecation" )
public class FullIntegrationPinpointProblemTest extends com.spinn3r.artemis.test.zookeeper.BaseZookeeperTest {

    @Inject
    DirectHttpRequestBuilder directHttpRequestBuilder;

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

        forwardProxyComponents = new ForwardProxyComponents();
        forwardProxyLauncher.getInjector().injectMembers( forwardProxyComponents );

        reverseProxyLauncher = launchReverseProxy();
        reverseProxyComponents = new ReverseProxyComponents();
        reverseProxyLauncher.getInjector().injectMembers( reverseProxyComponents );

        mainLauncher = Launcher.newBuilder().build();
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
    @Ignore
    public void testBulkRequests1() throws Exception {

        ProxyReference proxy = ProxyReferences.create(String.format("http://localhost:%s", 8081 ) );

        int nrRequest = 100;

        for (int i = 0; i < nrRequest; i++) {

            String contentWithEncoding = directHttpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

            assertThat( contentWithEncoding, containsString( "CNN" ) );

        }

    }

    @Test
    @Ignore
    public void testBulkRequestsWithEcho() throws Exception {

        ProxyReference proxy = ProxyReferences.create(String.format("http://127.0.0.1:%s", 8081 ) );

        Sockets.waitForOpenPort( "127.0.0.1", 8081 );
        Sockets.waitForOpenPort( "127.0.0.1", 8100 );

        NetworkTests.test( 500, () -> {

            String contentWithEncoding =
              directHttpRequestBuilder
                .get( "http://127.0.0.1:8100/echo?message=hello" )
                .withProxy( proxy )
                .execute()
                .getContentWithEncoding();

            assertEquals( "hello", contentWithEncoding );

        } );

    }

    private Launcher launchForwardProxy() throws Exception {

        TestResourcesConfigLoader testResourcesConfigLoader
          = new TestResourcesConfigLoader( "src/test/resources/noxy-forward" );

        Launcher launcher = Launcher.newBuilder(testResourcesConfigLoader ).build();

        launcher.launch( new ForwardProxyServiceReferences() );

        return launcher;

    }

    private Launcher launchReverseProxy() throws Exception {

        TestResourcesConfigLoader testResourcesConfigLoader
          = new TestResourcesConfigLoader( "src/test/resources/noxy-reverse" );

        Launcher launcher = Launcher.newBuilder(testResourcesConfigLoader).build();

        launcher.launch( new ReverseProxyServiceReferences() );

        return launcher;

    }

    static class ForwardProxyComponents {

    }

    static class ForwardProxyServiceReferences extends ServiceReferences {

        public ForwardProxyServiceReferences() {

            add( MockHostnameService.class );
            add( MockVersionService.class );
            add( ConsoleLoggingService.class );
            add( MembershipSupportService.class );
            add( ForwardProxyService.class );

        }

    }

    static class ReverseProxyComponents {
//
//        @Inject
//        Provider<ListenerMetaIndex> listenerMetaIndexProvider;

    }

    static class ReverseProxyServiceReferences extends ServiceReferences {

        public ReverseProxyServiceReferences() {

            add( MockHostnameService.class );
            add( MockVersionService.class );
            add( SyntheticClockService.class );
            add( ConsoleLoggingService.class );
            add( UptimeService.class );
            add( MetricsService.class );
            add( DefaultWebserverReferencesService.class );
            add( DiscoveryListenerSupportService.class );
            add( ReverseProxyService.class );
//            add( ReverseProxyAdminWebserverReferencesService.class );
//            add( WebserverService.class );

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
