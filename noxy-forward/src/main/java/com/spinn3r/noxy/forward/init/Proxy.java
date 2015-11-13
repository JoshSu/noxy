package com.spinn3r.noxy.forward.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Datacenter;
import com.spinn3r.noxy.forward.HostResolutionMethod;

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

    private boolean enableRequestTracing = false;

    private List<ProxyServerDescriptor> servers = Lists.newArrayList();

    private HostResolutionMethod hostResolutionMethod = HostResolutionMethod.IPV4;

    public Cluster getCluster() {
        return cluster;
    }

    public Datacenter getDatacenter() {
        return datacenter;
    }

    public boolean getEnableRequestLogging() {
        return enableRequestLogging;
    }

    public boolean getEnableRequestTracing() {
        return enableRequestTracing;
    }

    public List<ProxyServerDescriptor> getServers() {
        return servers;
    }

    public HostResolutionMethod getHostResolutionMethod() {
        return hostResolutionMethod;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                 "cluster=" + cluster +
                 ", datacenter=" + datacenter +
                 ", enableRequestLogging=" + enableRequestLogging +
                 ", enableRequestTracing=" + enableRequestTracing +
                 ", servers=" + servers +
                 ", hostResolutionMethod=" + hostResolutionMethod +
                 '}';
    }

}
