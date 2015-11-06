package com.spinn3r.noxy.discovery;

/**
 * Main interface for daemons which would like to join/leave the cluster.
 */
public interface Membership {

    /**
     * Join the cluster.  This will cause the onJoin methods to be called
     * on all DiscoveryListeners in the cluster.
     */
    void join( Endpoint endpoint);

    /**
     * Leave the cluster.  This will cause the onLeave methods to be called
     * on all DiscoveryListeners in the cluster.  Usually called on shutdown.
     */
    void leave( Endpoint endpoint);

}
