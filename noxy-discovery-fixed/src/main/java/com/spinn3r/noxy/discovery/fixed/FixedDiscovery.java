package com.spinn3r.noxy.discovery.fixed;

import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Discovery;
import com.spinn3r.noxy.discovery.DiscoveryListener;
import com.spinn3r.noxy.discovery.Endpoint;
import com.spinn3r.noxy.discovery.fixed.init.FixedDiscoveryConfig;
import com.spinn3r.noxy.discovery.fixed.init.ClusterReference;

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

        if ( ! fixedDiscoveryConfig.getClusters().containsKey( cluster.getName() ) ) {
            throw new RuntimeException( "No cluster group defined for: " + cluster.getName() );
        }

        ClusterReference clusterReference = fixedDiscoveryConfig.getClusters().get( cluster.getName() );

        for (Endpoint endpoint : clusterReference.getEndpoints()) {
            discoveryListener.onJoin( endpoint );
        }

    }

}
