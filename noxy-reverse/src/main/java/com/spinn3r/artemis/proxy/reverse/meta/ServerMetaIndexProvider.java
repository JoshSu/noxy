package com.spinn3r.artemis.proxy.reverse.meta;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides an indirection to the ServerIndex so that we can get() it at runtime
 * which can also be swapped in by a background subsystem.  For example, we can
 * reload the config and change the list of servers we're using.
 */
public class ServerMetaIndexProvider {

    private AtomicReference<ServerMetaIndex> reference = new AtomicReference<>( null );

    public ServerMetaIndex get() {
        return reference.get();
    }

    public void set( ServerMetaIndex serverMetaIndex) {
        this.reference.set( serverMetaIndex );
    }

}
