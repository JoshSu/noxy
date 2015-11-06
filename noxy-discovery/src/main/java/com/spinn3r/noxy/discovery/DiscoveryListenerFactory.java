package com.spinn3r.noxy.discovery;

/**
 *
 */
public interface DiscoveryListenerFactory {

    /**
     * Create a DiscoveryListener for the given cluster.  Each cluster you're
     * using should have a unique name (and other properties) which are
     * represented by the cluster.
     */
    DiscoveryListener create( Cluster cluster );

}
