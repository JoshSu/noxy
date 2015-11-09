package com.spinn3r.noxy.discovery.zookeeper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provider;
import com.spinn3r.artemis.fluent.Str;
import com.spinn3r.log5j.Logger;
import com.spinn3r.noxy.discovery.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class ZKDiscovery implements Discovery {

    private static final Logger log = Logger.getLogger();

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    private final Cluster cluster;

    private final String root;

    private Map<String,Endpoint> endpoints = Maps.newConcurrentMap();

    private List<DiscoveryListener> discoveryListeners = Lists.newCopyOnWriteArrayList();

    private ZKDiscoveryStateListener zkDiscoveryStateListener = new NullZKDiscoveryStateListener();

    public ZKDiscovery(Provider<CuratorFramework> curatorFrameworkProvider, Cluster cluster, DiscoveryListener discoveryListener) throws DiscoveryListenerException {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
        this.cluster = cluster;
        this.root = ZKClusterPaths.root( cluster );

        // I think we have to use inBackground() and we get back a String of paths
        // but then we have to stat them all and I think we also have to keep
        // an eye out for deleted nodes too...

        register( discoveryListener );

        discoverChildren();

    }

    @Override
    public void register(DiscoveryListener discoveryListener) {
        discoveryListeners.add( discoveryListener );
    }

    public void setZkDiscoveryStateListener(ZKDiscoveryStateListener zkDiscoveryStateListener) {
        this.zkDiscoveryStateListener = zkDiscoveryStateListener;
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

            try {
                curatorFramework
                  .create()
                  .creatingParentsIfNeeded()
                  .forPath( root );
            } catch (KeeperException.NodeExistsException e) {
                // this is normal because we just want to enforce that the root
                // dir exists.
            }

            List<String> children = curatorFramework
                .getChildren()
                .usingWatcher( new ChildrenWatcher() )
                .forPath( root );

            zkDiscoveryStateListener.onChildren( children );

            handleChildren( children );

        } catch (Throwable t) {
            throw new DiscoveryListenerException( "Could not get children: ", t );
        }

    }

    private void handleChildren(List<String> children) throws Exception {

        Set<String> existing = Sets.newHashSet( endpoints.keySet() );

        for (String child : children) {

            String path = ZKPaths.makePath( root, child );

            existing.remove( path );

            if ( endpoints.containsKey( child ) ) {
                continue;
            }

            CuratorFramework curatorFramework = curatorFrameworkProvider.get();

            byte[] data = curatorFramework.getData().forPath( path );
            Endpoint endpoint = Endpoint.fromJSON( data );

            endpoints.put( path, endpoint );
            fireOnJoin( endpoint );

        }

        Set<String> leaving = existing;
        for (String current : leaving) {
            fireOnLeave( endpoints.get( current ) );
            endpoints.remove( current );
        }

    }

    class ChildrenWatcher implements CuratorWatcher {

        @Override
        public void process(WatchedEvent watchedEvent) throws Exception {

            try {
                discoverChildren();
            } catch (Throwable t) {
                log.error( "Could not discover children: ", t );
            }

        }

    }

}
