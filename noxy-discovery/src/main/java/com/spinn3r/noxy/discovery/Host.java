package com.spinn3r.noxy.discovery;

/**
 *
 */
public class Host {

    private String address;

    private String name;

    private HostType hostType;

    private Datacenter datacenter;

    /**
     *
     * @param address The IP:port or hostname:port of this host.
     * @param name The host name of this host.  Only used for humans and not used for anything else.
     * @param hostType The type of the host, webserver, forward or reverse proxy, etc.
     * @param datacenter The datacenter this box is located in.
     */
    public Host(String address, String name, HostType hostType, Datacenter datacenter) {
        this.address = address;
        this.name = name;
        this.hostType = hostType;
        this.datacenter = datacenter;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public HostType getHostType() {
        return hostType;
    }

    public Datacenter getDatacenter() {
        return datacenter;
    }

    @Override
    public String toString() {
        return "Host{" +
                 "address='" + address + '\'' +
                 ", name='" + name + '\'' +
                 ", hostType=" + hostType +
                 ", datacenter=" + datacenter +
                 '}';
    }

}
