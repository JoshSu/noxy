package com.spinn3r.noxy.reverse.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.discovery.Cluster;

import java.util.List;

/**
 * A configuration to listen on a port and direct all requests to a backend
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Listener {

    private String name;

    private Binding binding = null;

    private ServerTemplate serverTemplate = new ServerTemplate();

    private boolean logging = true;

    private boolean tracing = false;

    private int connectTimeout = 40000;

    private Checks checks = new Checks();

    private Cluster cluster = null;

    public String getName() {
        return name;
    }

    public Binding getBinding() {
        return binding;
    }

    public ServerTemplate getServerTemplate() {
        return serverTemplate;
    }

    public boolean getLogging() {
        return logging;
    }

    public boolean getTracing() {
        return tracing;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public Checks getChecks() {
        return checks;
    }

    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public String toString() {
        return "Listener{" +
                 "name='" + name + '\'' +
                 ", binding=" + binding +
                 ", serverTemplate=" + serverTemplate +
                 ", logging=" + logging +
                 ", tracing=" + tracing +
                 ", connectTimeout=" + connectTimeout +
                 ", checks=" + checks +
                 ", cluster=" + cluster +
                 '}';
    }
}
