package com.spinn3r.noxy.reverse.init;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Advertisement to map listener name to port.  Used primarily for testing.
 */
public class ListenerPorts {

    private Map<String,Integer> index = Maps.newConcurrentMap();

    public void register( String name, int port ) {
        index.put( name, port );
    }

    public int getPort( String name ) {
        return index.get( name );
    }

}
