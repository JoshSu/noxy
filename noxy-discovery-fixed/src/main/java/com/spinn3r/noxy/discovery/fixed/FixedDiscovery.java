package com.spinn3r.noxy.discovery.fixed;

import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Discovery;
import com.spinn3r.noxy.discovery.DiscoveryListener;
import com.spinn3r.noxy.discovery.Endpoint;
import com.spinn3r.noxy.discovery.fixed.init.FixedDiscoveryConfig;
import com.spinn3r.noxy.discovery.fixed.init.Group;

/**
 *
 */
public class FixedDiscovery implements Discovery {

    private final FixedDiscoveryConfig fixedDiscoveryConfig;

    private final Cluster cluster;

    public FixedDiscovery(FixedDiscoveryConfig fixedDiscoveryConfig, Cluster cluster) {
        this.fixedDiscoveryConfig = fixedDiscoveryConfig;
        this.cluster = cluster;
    }

    @Override
    public void register(DiscoveryListener discoveryListener) {

        // go through and call all the onJoin methods of the discovery listener
        // for this cluster.

        if ( ! fixedDiscoveryConfig.getGroups().containsKey( cluster.getName() ) ) {
            throw new RuntimeException( "No cluster group defined for: " + cluster.getName() );
        }

        Group group = fixedDiscoveryConfig.getGroups().get( cluster.getName() );

        for (Endpoint endpoint : group.getEndpoints()) {
            discoveryListener.onJoin( endpoint );
        }

    }

}
