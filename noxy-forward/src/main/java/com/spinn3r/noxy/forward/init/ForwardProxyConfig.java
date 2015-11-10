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

    private List<Proxy> proxies = Lists.newArrayList();

    public List<Proxy> getProxies() {
        return proxies;
    }

    @Override
    public String toString() {
        return "ForwardProxyConfig{" +
                 "proxies=" + proxies +
                 '}';
    }

}
