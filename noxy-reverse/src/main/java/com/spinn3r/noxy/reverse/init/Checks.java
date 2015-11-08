package com.spinn3r.noxy.reverse.init;

/**
 *
 */
public class Checks {

    private long timeout = 1_000;

    public long getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "Checks{" +
                 "timeout=" + timeout +
                 '}';
    }

}
