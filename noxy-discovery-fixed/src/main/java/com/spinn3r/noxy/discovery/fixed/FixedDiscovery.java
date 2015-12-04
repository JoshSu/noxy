package com.spinn3r.noxy.discovery.fixed;

import com.google.common.base.Preconditions;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Discovery;
import com.spinn3r.noxy.discovery.DiscoveryListener;
import com.spinn3r.noxy.discovery.Endpoint;
import com.spinn3r.noxy.discovery.fixed.init.FixedDiscoveryConfig;
import com.spinn3r.noxy.discovery.fixed.init.ClusterReference;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class FixedDiscovery implements Discovery {

    private final FixedDiscoveryConfig fixedDiscoveryConfig;

    private final Cluster cluster;

    FixedDiscovery(FixedDiscoveryConfig fixedDiscoveryConfig, Cluster cluster, DiscoveryListener discoveryListener) {
        checkNotNull( fixedDiscoveryConfig );
        checkNotNull( cluster );
        checkNotNull( discoveryListener );
        this.fixedDiscoveryConfig = fixedDiscoveryConfig;
        this.cluster = cluster;
        register( discoveryListener );
    }

    @Override
    public void register(DiscoveryListener discoveryListener) {

        checkNotNull( fixedDiscoveryConfig, "No fixed discovery config" );
        checkNotNull( fixedDiscoveryConfig.getClusters(), "No clusters defined" );
        checkNotNull( cluster, "No cluster" );
        checkNotNull( cluster.getName(), "Cluster has no name" );

        if ( ! fixedDiscoveryConfig.getClusters().containsKey( cluster.getName() ) ) {
            throw new RuntimeException( "No cluster group defined for: " + cluster.getName() );
        }

        // go through and call all the onJoin methods of the discovery listener
        // for this cluster.

        ClusterReference clusterReference = fixedDiscoveryConfig.getClusters().get( cluster.getName() );

        for (Endpoint endpoint : clusterReference.getEndpoints()) {
            discoveryListener.onJoin( endpoint );
        }

    }

}
