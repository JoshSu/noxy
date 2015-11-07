package com.spinn3r.noxy.discovery.zookeeper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.DiscoveryListener;
import com.spinn3r.noxy.discovery.DiscoveryListenerFactory;
import org.apache.curator.framework.CuratorFramework;

/**
 *
 */
public class ZKDiscoveryListenerFactory implements DiscoveryListenerFactory {

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    @Inject
    ZKDiscoveryListenerFactory(Provider<CuratorFramework> curatorFrameworkProvider) {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
    }


    @Override
    public DiscoveryListener create(Cluster cluster) {
        return new ZKDiscoveryListener( curatorFrameworkProvider, cluster );
    }

}
