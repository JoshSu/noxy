package com.spinn3r.noxy.discovery.zookeeper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.noxy.discovery.*;
import org.apache.curator.framework.CuratorFramework;

/**
 *
 */
public class ZKDiscoveryFactory implements DiscoveryFactory {

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    @Inject
    ZKDiscoveryFactory(Provider<CuratorFramework> curatorFrameworkProvider) {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
    }


    @Override
    public Discovery create(Cluster cluster) throws DiscoveryListenerException {
        return new ZKDiscovery( curatorFrameworkProvider, cluster );
    }

}
