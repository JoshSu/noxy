package com.spinn3r.noxy.forward.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.Allocator;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Datacenter;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardProxyConfig {

    private List<Proxy> proxies = Lists.newArrayList();

    private final Allocator allocator = Allocator.POOLED;

    public List<Proxy> getProxies() {
        return proxies;
    }

    public Allocator getAllocator() {
        return allocator;
    }

    @Override
    public String toString() {
        return "ForwardProxyConfig{" +
                 "proxies=" + proxies +
                 ", allocator=" + allocator +
                 '}';
    }

}
