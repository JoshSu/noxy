package com.spinn3r.noxy.discovery.zookeeper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Provider;
import com.spinn3r.log5j.Logger;
import com.spinn3r.noxy.discovery.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class ZKDiscovery implements Discovery {

    private static final Logger log = Logger.getLogger();

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    private final Cluster cluster;

    private Map<String,Endpoint> endpoints = Maps.newConcurrentMap();

    private List<DiscoveryListener> discoveryListeners = Lists.newCopyOnWriteArrayList();

    public ZKDiscovery(Provider<CuratorFramework> curatorFrameworkProvider, Cluster cluster) throws DiscoveryListenerException {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
        this.cluster = cluster;

        // I think we have to use inBackground() and we get back a String of paths
        // but then we have to stat them all and I think we also have to keep
        // an eye out for deleted nodes too...

        discoverChildren();

    }

    @Override
    public void register(DiscoveryListener discoveryListener) {
        discoveryListeners.add( discoveryListener );
    }


    private void fireOnJoin( Endpoint endpoint ) {

        for (DiscoveryListener discoveryListener : discoveryListeners) {
            discoveryListener.onJoin( endpoint );
        }

    }

    private void fireOnLeave( Endpoint endpoint ) {

        for (DiscoveryListener discoveryListener : discoveryListeners) {
            discoveryListener.onLeave( endpoint );
        }

    }

    private void discoverChildren() throws DiscoveryListenerException {

        try {


            CuratorFramework curatorFramework = curatorFrameworkProvider.get();

            String root = ZKClusterPaths.root( cluster );

            curatorFramework.create()
              .creatingParentsIfNeeded()
              .forPath( root );

            List<String> children = curatorFramework
                .getChildren()
                .usingWatcher( new ChildrenWatcher() )
                .forPath( root );

            //discoverChildren( children );

        } catch (Throwable t) {
            throw new DiscoveryListenerException( "Could not get children: ", t );
        }


    }

    private void handleFoundChildren( List<String> paths ) {

        System.out.printf( "FIXME: here at least." );

        for (String path : paths) {
            System.out.printf( "FIXME: found child!!! " );
        }

    }

    class ChildrenWatcher implements CuratorWatcher {

        @Override
        public void process(WatchedEvent watchedEvent) throws Exception {
            try {
                discoverChildren();
            } catch (Throwable t) {
                log.error( "Could not discovery children: ", t );
            }
        }

    }

}
