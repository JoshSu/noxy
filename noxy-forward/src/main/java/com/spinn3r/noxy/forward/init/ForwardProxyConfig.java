package com.spinn3r.noxy.forward.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Datacenter;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardProxyConfig {

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

    public List<ProxyServerDescriptor> getServers() {
        return servers;
    }

    public boolean getEnableRequestLogging() {
        return enableRequestLogging;
    }

    @Override
    public String toString() {
        return "ForwardProxyConfig{" +
                 "cluster=" + cluster +
                 ", datacenter=" + datacenter +
                 ", enableRequestLogging=" + enableRequestLogging +
                 ", servers=" + servers +
                 '}';
    }

}
