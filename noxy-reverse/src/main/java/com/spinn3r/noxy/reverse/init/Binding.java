package com.spinn3r.noxy.reverse.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Binding {

    private String address;

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Binding{" +
                 "address='" + address + '\'' +
                 '}';
    }
}
