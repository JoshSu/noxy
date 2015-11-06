package com.spinn3r.noxy.discovery;

/**
 *
 */
public class Endpoint {

    private String address;

    private String hostname;

    private EndpointType endpointType;

    private Datacenter datacenter;

    /**
     *
     * @param address The IP:port or hostname:port of this host.
     * @param hostname The host name of this host.  Only used for humans and not used for anything else.
     * @param endpointType The type of the host, webserver, forward or reverse proxy, etc.
     * @param datacenter The datacenter this box is located in.
     */
    public Endpoint(String address, String hostname, EndpointType endpointType, Datacenter datacenter) {
        this.address = address;
        this.hostname = hostname;
        this.endpointType = endpointType;
        this.datacenter = datacenter;
    }

    public String getAddress() {
        return address;
    }

    public String getHostname() {
        return hostname;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                 "address='" + address + '\'' +
                 ", hostname='" + hostname + '\'' +
                 ", hostType=" + endpointType +
                 ", datacenter=" + datacenter +
                 '}';
    }

}
