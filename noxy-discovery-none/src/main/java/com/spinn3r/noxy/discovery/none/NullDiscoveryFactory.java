package com.spinn3r.noxy.discovery.none;

import com.spinn3r.noxy.discovery.*;
import com.spinn3r.noxy.discovery.none.init.NullDiscoveryService;

/**
 *
 */
public class NullDiscoveryFactory implements DiscoveryFactory {

    @Override
    public Discovery create(Cluster cluster, DiscoveryListener discoveryListener) throws DiscoveryListenerException {

        return new Discovery() {
            @Override
            public void register(DiscoveryListener discoveryListener) {

            }

        };

    }

}
