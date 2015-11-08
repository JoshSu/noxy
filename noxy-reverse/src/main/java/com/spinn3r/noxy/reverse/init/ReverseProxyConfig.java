package com.spinn3r.noxy.reverse.init;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 */
public class ReverseProxyConfig {

    private List<Listener> listeners = Lists.newArrayList();

    public List<Listener> getListeners() {
        return listeners;
    }

}


