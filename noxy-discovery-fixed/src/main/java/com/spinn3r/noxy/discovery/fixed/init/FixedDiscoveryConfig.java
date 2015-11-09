package com.spinn3r.noxy.discovery.fixed.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixedDiscoveryConfig {

    private Map<String,ClusterReference> clusters = new HashMap<>();

    public Map<String,ClusterReference> getClusters() {
        return clusters;
    }

}
