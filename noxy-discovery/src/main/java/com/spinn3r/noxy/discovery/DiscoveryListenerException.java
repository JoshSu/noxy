package com.spinn3r.noxy.discovery;

/**
 *
 */
@SuppressWarnings( "serial" )
public class DiscoveryListenerException extends Exception {
    public DiscoveryListenerException() {
    }

    public DiscoveryListenerException(String message) {
        super( message );
    }

    public DiscoveryListenerException(String message, Throwable cause) {
        super( message, cause );
    }

    public DiscoveryListenerException(Throwable cause) {
        super( cause );
    }
}


