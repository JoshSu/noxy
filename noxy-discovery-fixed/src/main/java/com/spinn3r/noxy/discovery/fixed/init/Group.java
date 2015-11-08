package com.spinn3r.noxy.discovery.fixed.init;

import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Endpoint;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Group {

    private Cluster cluster;

    private List<Endpoint> endpoints = new ArrayList<>();

    public Cluster getCluster() {
        return cluster;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

}
