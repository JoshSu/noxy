package com.spinn3r.noxy.reverse.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseProxyConfig {

    private List<Listener> listeners = Lists.newArrayList();

    public List<Listener> getListeners() {
        return listeners;
    }

}


