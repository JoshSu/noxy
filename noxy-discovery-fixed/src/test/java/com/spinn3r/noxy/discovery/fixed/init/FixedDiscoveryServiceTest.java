package com.spinn3r.noxy.discovery.fixed.init;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.noxy.discovery.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class FixedDiscoveryServiceTest extends BaseLauncherTest {

    @Inject
    DiscoveryFactory discoveryFactory;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp( FixedDiscoveryService.class );
    }

    @Test
    public void testInit() throws Exception {

        Cluster cluster = new Cluster( "spinn3r" );

        List<Endpoint> endpoints = Lists.newArrayList();

        DiscoveryListener discoveryListener = new DiscoveryListener() {

            @Override
            public void onJoin(Endpoint endpoint) {
                endpoints.add( endpoint );
            }

            @Override
            public void onLeave(Endpoint endpoint) {

            }

        };

        Discovery discovery = discoveryFactory.create( cluster, discoveryListener );

        assertEquals( "[Endpoint{address='localhost:1234', hostname='localhost', endpointType=FORWARD_PROXY, datacenter=Datacenter{name='fal21', location='Falkenstein, Germany'}}]", endpoints.toString() );

    }

}