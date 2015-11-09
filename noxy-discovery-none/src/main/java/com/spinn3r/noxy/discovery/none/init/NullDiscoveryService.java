package com.spinn3r.noxy.discovery.none.init;

import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.ServiceReference;
import com.spinn3r.noxy.discovery.DiscoveryFactory;
import com.spinn3r.noxy.discovery.MembershipFactory;
import com.spinn3r.noxy.discovery.none.NullDiscoveryFactory;
import com.spinn3r.noxy.discovery.none.NullMembershipFactory;

/**
 *
 */
public class NullDiscoveryService extends BaseService {

    public static final ServiceReference REF = new ServiceReference( NullDiscoveryService.class );

    @Override
    public void init() {
        advertise( DiscoveryFactory.class, NullDiscoveryFactory.class );
        advertise( MembershipFactory.class, NullMembershipFactory.class );
    }

}
