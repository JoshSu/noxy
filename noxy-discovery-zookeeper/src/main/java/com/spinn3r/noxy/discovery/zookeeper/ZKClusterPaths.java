package com.spinn3r.noxy.discovery.zookeeper;

import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Endpoint;

/**
 *
 */
public class ZKClusterPaths {

    public static String root( Cluster cluster ) {
        return String.format( "/noxy-discovery/%s", cluster.getName() );
    }

    public static String endpoint( Cluster cluster, Endpoint endpoint) {
        return String.format( "%s/%s", root( cluster ) , endpoint.getAddress() );
    }

}
