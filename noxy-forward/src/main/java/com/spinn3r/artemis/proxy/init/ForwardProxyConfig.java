package com.spinn3r.artemis.proxy.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardProxyConfig {

    private boolean enableRequestLogging = true;

    private List<ProxyServerDescriptor> servers = Lists.newArrayList();

    public List<ProxyServerDescriptor> getServers() {
        return servers;
    }

    public boolean getEnableRequestLogging() {
        return enableRequestLogging;
    }

    @Override
    public String toString() {
        return "ProxyConfig{" +
                 "servers=" + servers +
                 '}';
    }

}
