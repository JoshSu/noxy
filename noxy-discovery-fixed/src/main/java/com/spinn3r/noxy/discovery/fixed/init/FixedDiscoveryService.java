package com.spinn3r.noxy.discovery.fixed.init;

import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.artemis.init.ServiceReference;
import com.spinn3r.noxy.discovery.DiscoveryFactory;
import com.spinn3r.noxy.discovery.fixed.FixedDiscoveryFactory;

/**
 *
 */
@Config( path = "fixed-discovery.conf",
         required = true,
         implementation = FixedDiscoveryConfig.class )
public class FixedDiscoveryService extends BaseService {

    public static final ServiceReference REF = new ServiceReference( FixedDiscoveryService.class );

    @Override
    public void init() {
        advertise( DiscoveryFactory.class, FixedDiscoveryFactory.class );
    }

}
