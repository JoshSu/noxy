package com.spinn3r.artemis.proxy.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InetSocketAddressDescriptor {

    private String address;

    private int port = 0;

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "InetSocketAddressDescriptor{" +
                 "address='" + address + '\'' +
                 ", port='" + port + '\'' +
                 '}';
    }

}
