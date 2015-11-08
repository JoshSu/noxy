package com.spinn3r.noxy.reverse.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
