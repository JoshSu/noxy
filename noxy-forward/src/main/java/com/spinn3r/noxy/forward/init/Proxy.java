package com.spinn3r.noxy.forward.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Datacenter;

import java.util.List;

/**
 * A configuration for a proxy with a cluster, datacenter, and a collection of
 * servers that listen on ports and bind to addresses.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Proxy {

    private Cluster cluster = null;

    private Datacenter datacenter = null;

    private boolean enableRequestLogging = true;

    private List<ProxyServerDescriptor> servers = Lists.newArrayList();

    public Cluster getCluster() {
        return cluster;
    }

    public Datacenter getDatacenter() {
        return datacenter;
    }

    public boolean getEnableRequestLogging() {
        return enableRequestLogging;
    }

    public List<ProxyServerDescriptor> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        return "Listener{" +
                 "cluster=" + cluster +
                 ", datacenter=" + datacenter +
                 ", enableRequestLogging=" + enableRequestLogging +
                 ", servers=" + servers +
                 '}';
    }

}
