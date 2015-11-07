package com.spinn3r.noxy.discovery.zookeeper.init;

import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.noxy.discovery.DiscoveryListenerFactory;
import com.spinn3r.noxy.discovery.MembershipFactory;
import com.spinn3r.noxy.discovery.zookeeper.ZKDiscoveryListener;
import com.spinn3r.noxy.discovery.zookeeper.ZKDiscoveryListenerFactory;
import com.spinn3r.noxy.discovery.zookeeper.ZKMembership;
import com.spinn3r.noxy.discovery.zookeeper.ZKMembershipFactory;

/**
 *
 */
public class ZKNoxyDiscoveryService extends BaseService {

    @Override
    public void init() {
        advertise( DiscoveryListenerFactory.class, ZKDiscoveryListenerFactory.class );
        advertise( MembershipFactory.class, ZKMembershipFactory.class );
    }
}
