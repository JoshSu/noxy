package com.spinn3r.noxy;

import com.google.inject.Inject;
import com.spinn3r.artemis.init.Launcher;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.init.ServiceReferences;
import com.spinn3r.artemis.init.config.TestResourcesConfigLoader;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.network.builder.DirectHttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.Proxies;
import com.spinn3r.artemis.network.builder.proxies.ProxyReference;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.init.ForwardProxyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test using a reverse proxy pointing to a forward proxy pointing to the Internet
 * and using zookeeper to have each component discovery each other.
 *
 * In the future we might use Netty's static webserver support to serve up files
 * and have a full pipeline
 */
@Ignore
@SuppressWarnings( "deprecation" )
public class FullIntegrationWithForwardProxyOnlyTest extends com.spinn3r.artemis.test.zookeeper.BaseZookeeperTest {

    @Inject
    DirectHttpRequestBuilder directHttpRequestBuilder;

    ForwardProxyComponents forwardProxyComponents;

    Launcher forwardProxyLauncher;

    Launcher mainLauncher;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        forwardProxyLauncher = launchForwardProxy();

        forwardProxyComponents = new ForwardProxyComponents();
        forwardProxyLauncher.getInjector().injectMembers( forwardProxyComponents );

        forwardProxyComponents = new ForwardProxyComponents();
        forwardProxyLauncher.getInjector().injectMembers( forwardProxyComponents );

        mainLauncher = Launcher.newBuilder().build();
        mainLauncher.launch( new MainServiceReferences() );
        mainLauncher.getInjector().injectMembers( this );

    }

    @Override
    @After
    public void tearDown() throws Exception {

        if ( forwardProxyLauncher != null )
            forwardProxyLauncher.stop();

        if ( mainLauncher != null )
            mainLauncher.stop();

        super.tearDown();
    }
//
//    @Test
//    public void testChecksBringingForwardProxyOnline() throws Exception {
//
//        // ok.. both services should be up and running.. wait for the components to be up
//        // and running
//
//        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();
//
//        await().until( () -> {
//            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 2 ) );
//        } );
//
//    }
//
//    @Test
//    public void testStatusAPI() throws Exception {
//
//        // ok.. both services should be up and running.. wait for the components to be up
//        // and running
//
//        ListenerMeta listenerMeta = reverseProxyComponents.listenerMetaIndexProvider.get().getListenerMetas().get( 0 );
//
//        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();
//
//        await().until( () -> {
//            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 2 ) );
//        } );
//
//
//        String status = directHttpRequestBuilder.get( "http://localhost:7100/status" ).execute().getContentWithEncoding();
//
//        System.out.printf( "%s\n", status );
//
//    }
//
//    @Test
//    public void testRequestVolume() throws Exception {
//
//        int nrRequests = 100;
//
//        ListenerMeta listenerMeta = reverseProxyComponents.listenerMetaIndexProvider.get().getListenerMetas().get( 0 );
//
//        OnlineServerMetaIndexProvider onlineServerMetaIndexProvider = listenerMeta.getOnlineServerMetaIndexProvider();
//
//        await().until( () -> {
//            assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 2 ) );
//        } );
//
//        Proxy proxy = Proxies.create( String.format( "http://localhost:%s", 8080 ) );
//
//        for (int i = 0; i < nrRequests; i++) {
//
//            System.out.printf( "======== Fetching request %,d\n", i );
//
//            String contentWithEncoding = directHttpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();
//
//            assertThat( contentWithEncoding, containsString( "CNN" ) );
//
////
////            String contentWithEncoding = directHttpRequestBuilder.get( "https://www.google.com" ).withProxy( proxy ).execute().getContentWithEncoding();
////
////            assertThat( contentWithEncoding, containsString( "<title>Google</title>" ) );
//
//            // make sure we still have both boxes
//            //FIXME assertThat( onlineServerMetaIndexProvider.get().getBalancer().size(), equalTo( 2 ) );
//
//        }
//
//    }

//    @Test
//    public void testTestOnSecondaryServer() throws Exception {
//
//        Proxy proxy = Proxies.create( String.format( "http://localhost:%s", 8081 ) );
//        String contentWithEncoding = httpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();
//
//        assertThat( contentWithEncoding, containsString( "CNN" ) );
//
//
//
//    }

    @Test
    @Ignore
    public void testBulkRequests1() throws Exception {

        ProxyReference proxy = Proxies.create( String.format( "http://localhost:%s", 8081 ) );

        int nrRequest = 100;

        for (int i = 0; i < nrRequest; i++) {

            String contentWithEncoding = directHttpRequestBuilder.get( "http://cnn.com" ).withProxy( proxy ).execute().getContentWithEncoding();

            assertThat( contentWithEncoding, containsString( "CNN" ) );

        }

    }

    private Launcher launchForwardProxy() throws Exception {

        TestResourcesConfigLoader testResourcesConfigLoader
          = new TestResourcesConfigLoader( "src/test/resources/noxy-forward" );

        Launcher launcher = Launcher.newBuilder(testResourcesConfigLoader ).build();

        launcher.launch( new ForwardProxyServiceReferences() );

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

    static class MainServiceReferences extends ServiceReferences {
        public MainServiceReferences() {
            add( DirectNetworkService.class );
        }
    }

}
