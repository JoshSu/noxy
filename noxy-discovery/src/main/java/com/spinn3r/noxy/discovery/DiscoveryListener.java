package com.spinn3r.noxy.discovery;

/**
 *
 */
public interface DiscoveryListener {

    /**
     * Called when a host enters the cluster.
     */
    void onEnter( Host host );

    /**
     * Called when a host leaves the cluster.
     */
    void onLeave( Host host );

}
