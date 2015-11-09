package com.spinn3r.noxy.reverse.init;

/**
 *
 */
public class Server {

    // consider implementing all of the haproxy server config directives.
    // https://cbonte.github.io/haproxy-dconv/configuration-1.5.html#5.2

    private String name;

    private String address;

    private boolean disabled;

    private boolean check = false;

    private long inter = 1000;

    public Server(String name, String address, boolean disabled, boolean check, long inter) {
        this.name = name;
        this.address = address;
        this.disabled = disabled;
        this.check = check;
        this.inter = inter;
    }

    private Server() {
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public boolean getCheck() {
        return check;
    }

    public long getInter() {
        return inter;
    }

    @Override
    public String toString() {
        return "Server{" +
                 "name='" + name + '\'' +
                 ", address='" + address + '\'' +
                 ", disabled=" + disabled +
                 ", check=" + check +
                 ", inter=" + inter +
                 '}';
    }

}


