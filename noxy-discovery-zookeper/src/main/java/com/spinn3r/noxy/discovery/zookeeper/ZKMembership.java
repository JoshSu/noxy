package com.spinn3r.noxy.discovery.zookeeper;

import com.google.common.base.Charsets;
import com.google.inject.Provider;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Endpoint;
import com.spinn3r.noxy.discovery.Membership;
import com.spinn3r.noxy.discovery.MembershipException;
import org.apache.curator.framework.CuratorFramework;

/**
 *
 */
public class ZKMembership implements Membership {

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    private final Cluster cluster;

    ZKMembership(Provider<CuratorFramework> curatorFrameworkProvider, Cluster cluster) {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
        this.cluster = cluster;
    }

    @Override
    public void join(Endpoint endpoint) throws MembershipException {

        try {
            String path = ZKClusterPaths.endpoint( cluster, endpoint );
            String json = endpoint.toJSON();
            byte[] json_data = json.getBytes( Charsets.UTF_8 );

            curatorFrameworkProvider.get().create().forPath( path, json_data );
        } catch (Exception e) {
            throw new MembershipException( String.format( "Could not join noxy cluster %s for endpoint: %s ", cluster.getName(), endpoint.getAddress() ),  e );
        }

    }

    @Override
    public void leave(Endpoint endpoint) throws MembershipException {

        try {
            String path = ZKClusterPaths.endpoint( cluster, endpoint );
            curatorFrameworkProvider.get().delete().forPath( path );
        } catch (Exception e) {
            throw new MembershipException( String.format( "Could not join noxy cluster %s for endpoint: %s ", cluster.getName(), endpoint.getAddress() ),  e );
        }

    }

}
