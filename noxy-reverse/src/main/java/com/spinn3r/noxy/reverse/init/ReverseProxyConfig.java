package com.spinn3r.noxy.reverse.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.Allocator;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseProxyConfig {

    private Allocator allocator = Allocator.POOLED;

    private List<Listener> listeners = Lists.newArrayList();

    public List<Listener> getListeners() {
        return listeners;
    }

    public Allocator getAllocator() {
        return allocator;
    }

    @Override
    public String toString() {
        return "ReverseProxyConfig{" +
                 "allocator=" + allocator +
                 ", listeners=" + listeners +
                 '}';
    }
}


