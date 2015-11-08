package com.spinn3r.noxy.reverse.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

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

    private List<Server> servers = Lists.newCopyOnWriteArrayList();

    private boolean logging = true;

    private int connectTimeout = 40000;

    private Checks checks = new Checks();

    public String getName() {
        return name;
    }

    public Binding getBinding() {
        return binding;
    }

    public List<Server> getServers() {
        return servers;
    }

    public boolean getLogging() {
        return logging;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public Checks getChecks() {
        return checks;
    }

    @Override
    public String toString() {
        return "Listener{" +
                 "name='" + name + '\'' +
                 ", binding=" + binding +
                 ", servers=" + servers +
                 ", logging=" + logging +
                 ", connectTimeout=" + connectTimeout +
                 ", checks=" + checks +
                 '}';
    }

}
