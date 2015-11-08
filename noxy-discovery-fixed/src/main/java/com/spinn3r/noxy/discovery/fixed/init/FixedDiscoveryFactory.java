package com.spinn3r.noxy.discovery.fixed.init;

import com.google.inject.Inject;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Discovery;
import com.spinn3r.noxy.discovery.DiscoveryFactory;
import com.spinn3r.noxy.discovery.DiscoveryListenerException;
import com.spinn3r.noxy.discovery.fixed.FixedDiscovery;

/**
 *
 */
public class FixedDiscoveryFactory implements DiscoveryFactory {

    private final FixedDiscoveryConfig fixedDiscoveryConfig;

    @Inject
    FixedDiscoveryFactory(FixedDiscoveryConfig fixedDiscoveryConfig) {
        this.fixedDiscoveryConfig = fixedDiscoveryConfig;
    }

    @Override
    public Discovery create(Cluster cluster) throws DiscoveryListenerException {
        return new FixedDiscovery( fixedDiscoveryConfig, cluster );
    }

}
