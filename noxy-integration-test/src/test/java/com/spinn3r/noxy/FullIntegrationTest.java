package com.spinn3r.noxy;

import com.spinn3r.artemis.init.Launcher;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.init.ServiceReferences;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.test.zookeeper.BaseZookeeperTest;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.init.ForwardProxyService;
import org.junit.Before;
import org.junit.Test;

/**
 * Test using a reverse proxy pointing to a forward proxy pointing to the Internet
 * and using zookeeper to have each component discovery each other.
 *
 * In the future we might use Netty's static webserver support to serve up files
 * and have a full pipeline
 */
public class FullIntegrationTest extends BaseZookeeperTest {

    Launcher forwardProxyLauncher;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        forwardProxyLauncher = launchForwardProxy();

    }

    @Test
    public void test1() throws Exception {


    }

    private Launcher launchForwardProxy() throws Exception {

        Launcher launcher = Launcher.forResourceConfigLoader().build();

        launcher.launch( new ForwardProxyServiceReferences() );

        return launcher;

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

}
