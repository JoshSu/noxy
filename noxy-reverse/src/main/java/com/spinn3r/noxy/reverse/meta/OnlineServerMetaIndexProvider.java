package com.spinn3r.noxy.reverse.meta;

import java.net.UnknownHostException;

/**
 * Contains hosts that are online only and not the full index.
 */
public class OnlineServerMetaIndexProvider extends ServerMetaIndexProvider {

    public OnlineServerMetaIndexProvider() throws UnknownHostException {
        this.set( new ServerMetaIndex() );
    }

}
