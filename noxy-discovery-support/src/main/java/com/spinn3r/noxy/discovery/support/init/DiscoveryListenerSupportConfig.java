package com.spinn3r.noxy.discovery.support.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryListenerSupportConfig {

    private DiscoveryListenerProvider provider;

    public DiscoveryListenerProvider getProvider() {
        return provider;
    }

}
