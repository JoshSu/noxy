package com.spinn3r.noxy.discovery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cluster {

    private String name;

    public Cluster(String name) {
        this.name = name;
    }

    private Cluster() {}

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                 "name='" + name + '\'' +
                 '}';
    }

}
