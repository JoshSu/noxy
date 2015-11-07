package com.spinn3r.noxy.discovery.zookeeper;

import com.google.inject.Provider;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.DiscoveryListener;
import com.spinn3r.noxy.discovery.Endpoint;
import org.apache.curator.framework.CuratorFramework;

/**
 *
 */
public class ZKDiscoveryListener implements DiscoveryListener {

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    private final Cluster cluster;

    public ZKDiscoveryListener(Provider<CuratorFramework> curatorFrameworkProvider, Cluster cluster) {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
        this.cluster = cluster;

        // I think we have to use inBackground() and we get back a String of paths
        // but then we have to stat them all and I think we also have to keep
        // an eye out for deleted nodes too...

//        curatorFrameworkProvider.get()
//          .getChildren()
//          .
//          .forPath( String.format( "/noxy-discovery/%s", cluster.getName() )
//

    }

    @Override
    public void onJoin(Endpoint endpoint) {



    }

    @Override
    public void onLeave(Endpoint endpoint) {

    }

}
