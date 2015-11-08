package com.spinn3r.noxy.discovery.fixed.init;

import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.noxy.discovery.DiscoveryFactory;

/**
 *
 */
@Config( path = "fixed-discovery.conf",
         required = true,
         implementation = FixedDiscoveryConfig.class )
public class FixedDiscoveryService extends BaseService {

    @Override
    public void init() {
        advertise( DiscoveryFactory.class, FixedDiscoveryFactory.class );
    }

}
