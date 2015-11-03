package com.spinn3r.artemis.proxy.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxyServerDescriptor {

    private String name;

    private InetSocketAddressDescriptor inbound;

    private InetSocketAddressDescriptor outbound;

    public String getName() {
        return name;
    }

    public InetSocketAddressDescriptor getInbound() {
        return inbound;
    }

    public InetSocketAddressDescriptor getOutbound() {
        return outbound;
    }

    @Override
    public String toString() {
        return "ProxyServerDescriptor{" +
                 "name='" + name + '\'' +
                 ", inbound=" + inbound +
                 ", outbound=" + outbound +
                 '}';
    }

}
