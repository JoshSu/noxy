package com.spinn3r.noxy.discovery.support.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveryListenerSupportConfig {

    private DiscoveryListenerProvider provider = DiscoveryListenerProvider.FIXED;

    public DiscoveryListenerProvider getProvider() {
        return provider;
    }

}
