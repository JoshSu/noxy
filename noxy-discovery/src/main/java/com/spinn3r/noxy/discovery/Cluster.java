package com.spinn3r.noxy.discovery;

/**
 *
 */
public class Cluster {

    private String name;

    public Cluster(String name) {
        this.name = name;
    }

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
