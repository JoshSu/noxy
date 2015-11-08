package com.spinn3r.noxy.reverse.init;

/**
 * Used when expanding endpoints into Servers ...
 */
public class ServerTemplate {

    private boolean disabled;

    private boolean check = false;

    private long inter = 1000;

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
        return "ServerTemplate{" +
                 "disabled=" + disabled +
                 ", check=" + check +
                 ", inter=" + inter +
                 '}';
    }

}
