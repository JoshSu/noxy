package com.spinn3r.noxy.forward.init;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Advertisement to map listener name to port.  Used primarily for testing.
 */
public class ForwardProxyPorts {

    private Map<String,Integer> index = Maps.newConcurrentMap();

    public void register( String name, int port ) {
        index.put( name, port );
    }

    public int getPort( String name ) {

        if ( ! index.containsKey( name ) ) {
            throw new RuntimeException( String.format( "no entry for %s in %s", name, index ) );
        }

        return index.get( name );
    }

}
