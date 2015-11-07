package com.spinn3r.noxy.discovery.zookeeper;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.artemis.init.Launcher;
import com.spinn3r.artemis.init.ServiceReferences;
import com.spinn3r.artemis.test.zookeeper.BaseZookeeperTest;
import com.spinn3r.artemis.zookeeper.init.ZookeeperService;
import com.spinn3r.noxy.discovery.*;
import com.spinn3r.noxy.discovery.zookeeper.init.ZKNoxyDiscoveryService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ZKTest extends BaseZookeeperTest {

    Launcher launcher;

    @Inject
    DiscoveryFactory discoveryFactory;

    @Inject
    MembershipFactory membershipFactory;

    @Inject
    Provider<CuratorFramework> curatorFrameworkProvider;

    Cluster cluster = new Cluster( "test" );

    Datacenter datacenter = new Datacenter( "local", "Castro Valley, CA" );
    Endpoint endpoint = new Endpoint( "localhost:12345", "localhost", EndpointType.WEBSERVER, datacenter );

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        launcher = Launcher.forResourceConfigLoader().build();
        launcher.launch( new TestServiceReferences() );

        launcher.getInjector().injectMembers( this );

    }

    @Test
    public void testMembershipJoinAndLeave() throws Exception {

        Membership membership = membershipFactory.create( cluster );
        membership.join( endpoint );

        // now assert that the node is created and that it's the right paths and mode.

        String path = ZKClusterPaths.endpoint( cluster, endpoint );

        CuratorFramework curatorFramework = curatorFrameworkProvider.get();
        byte[] data = curatorFramework.getData().forPath( path );
        Endpoint endpoint0 = Endpoint.fromJSON( data );

        Stat stat = curatorFramework.checkExists().forPath( path );

        System.out.printf( "stat: %s\n", stat );
        System.out.printf( "stat (ephemeral owner): %s\n", stat.getEphemeralOwner() );
        assertTrue( stat.getEphemeralOwner() > 0 );

        assertNotNull( stat );

        assertEquals( endpoint.toString(), endpoint0.toString() );

        membership.leave( endpoint );

        stat = curatorFramework.checkExists().forPath( path );
        assertNull( stat );

    }

    @Test
    @Ignore
    public void testDiscoveryListener() throws Exception {

        Discovery discovery = discoveryFactory.create( cluster );

        Map<String,Endpoint> endpointMap = Maps.newConcurrentMap();

        DiscoveryListener discoveryListener = new DiscoveryListener() {

            @Override
            public void onJoin(Endpoint endpoint) {
                endpointMap.put( endpoint.getAddress(), endpoint );
            }

            @Override
            public void onLeave(Endpoint endpoint) {
                endpointMap.remove( endpoint.getAddress() );
            }

        };

        // FIXME: this test won't work because the second call

        discovery.register( discoveryListener );

        Membership membership = membershipFactory.create( cluster );
        membership.join( endpoint );

        System.out.printf( "done\n" );

    }

    @Test
    @Ignore
    public void testCreatePathTwice() throws Exception {

        CuratorFramework curatorFramework = curatorFrameworkProvider.get();

        String path = "/foo";

        curatorFramework.create()
          .creatingParentsIfNeeded()
          .forPath( path );

        curatorFramework.create()
          .creatingParentsIfNeeded()
          .forPath( path );


    }

    static class TestServiceReferences extends ServiceReferences {

        public TestServiceReferences() {
            add( ZookeeperService.class );
            add( ZKNoxyDiscoveryService.class );
        }
    }

}