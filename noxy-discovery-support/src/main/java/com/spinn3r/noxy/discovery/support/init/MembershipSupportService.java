package com.spinn3r.noxy.discovery.support.init;

import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.artemis.zookeeper.init.ZookeeperService;
import com.spinn3r.noxy.discovery.fixed.init.FixedDiscoveryService;
import com.spinn3r.noxy.discovery.none.init.NullDiscoveryService;
import com.spinn3r.noxy.discovery.zookeeper.init.ZKDiscoveryService;

/**
 *
 */
@Config( path = "membership-support.conf",
         required = false,
         implementation = MembershipSupportConfig.class )
public class MembershipSupportService extends BaseService {

    private final MembershipSupportConfig membershipSupportConfig;

    @Inject
    MembershipSupportService(MembershipSupportConfig membershipSupportConfig) {
        this.membershipSupportConfig = membershipSupportConfig;
    }

    @Override
    public void init() {

        switch ( membershipSupportConfig.getProvider() ) {

            case ZOOKEEPER:
                include( ZookeeperService.REF, ZKDiscoveryService.REF );
                break;

            case NONE:
                include( NullDiscoveryService.REF );
                break;

            default:
                throw new RuntimeException( "Unknown provider: " + membershipSupportConfig.getProvider() );

        }

    }


}
