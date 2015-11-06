package com.spinn3r.noxy.discovery;

/**
 *
 */
public class Datacenter {

    private String name;

    private String location;

    public Datacenter(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Datacenter{" +
                 "name='" + name + '\'' +
                 ", location='" + location + '\'' +
                 '}';
    }

}
