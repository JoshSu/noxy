package com.spinn3r.noxy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.artemis.init.Launcher;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.init.ServiceReferences;
import com.spinn3r.artemis.init.config.TestResourcesConfigLoader;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.test.zookeeper.BaseZookeeperTest;
import com.spinn3r.noxy.discovery.support.init.DiscoveryListenerSupportService;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.init.ForwardProxyService;
import com.spinn3r.noxy.reverse.init.ReverseProxyService;
import com.spinn3r.noxy.reverse.meta.ListenerMeta;
import com.spinn3r.noxy.reverse.meta.ListenerMetaIndex;
import com.spinn3r.noxy.reverse.meta.OnlineServerMetaIndexProvider;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Test using a reverse proxy pointing to a forward proxy pointing to the Internet
 * and using zookeeper to have each component discovery each other.
 *
 * In the future we might use Netty's static webserver support to serve up files
 * and have a full pipeline
 */
public class FullIntegrationTest extends BaseZookeeperTest {

    ForwardProxyComponents forwardProxyComponents;

    ReverseProxyComponents reverseProxyComponents;

    Launcher forwardProxyLauncher;

    Launcher reverseProxyLauncher;

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

    }

    @Test
    public void test1() throws Exception {

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

        @Inject
        Provider<ListenerMetaIndex> listenerMetaIndexProvider;

    }

    static class ReverseProxyServiceReferences extends ServiceReferences {

        public ReverseProxyServiceReferences() {

            add( MockHostnameService.class );
            add( MockVersionService.class );
            add( ConsoleLoggingService.class );
            add( DiscoveryListenerSupportService.class );
            add( ReverseProxyService.class );

        }

    }

}
