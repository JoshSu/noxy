package com.spinn3r.noxy.discovery.fixed.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixedDiscoveryConfig {

    private Map<String,Group> groups = new HashMap<>();

    public Map<String,Group> getGroups() {
        return groups;
    }

}
