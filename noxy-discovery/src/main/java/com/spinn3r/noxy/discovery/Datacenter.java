package com.spinn3r.noxy.discovery;

/**
 *
 */
public class Datacenter {

    private String name;

    private String location;

    /**
     *
     *
     * @param name The name of the data center.  This is just a human readable
     *             string and specific to your application.
     *
     * @param location The physical location of this datacenter.  Example: San Jose, CA
     */
    public Datacenter(String name, String location) {
        this.name = name;
        this.location = location;
    }

    private Datacenter() {
        // json constructor
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
