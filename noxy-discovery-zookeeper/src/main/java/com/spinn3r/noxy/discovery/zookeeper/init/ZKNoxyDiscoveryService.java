package com.spinn3r.noxy.discovery.zookeeper.init;

import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.noxy.discovery.DiscoveryFactory;
import com.spinn3r.noxy.discovery.MembershipFactory;
import com.spinn3r.noxy.discovery.zookeeper.ZKDiscoveryFactory;
import com.spinn3r.noxy.discovery.zookeeper.ZKMembershipFactory;

/**
 *
 */
public class ZKNoxyDiscoveryService extends BaseService {

    @Override
    public void init() {
        advertise( DiscoveryFactory.class, ZKDiscoveryFactory.class );
        advertise( MembershipFactory.class, ZKMembershipFactory.class );
    }
}
