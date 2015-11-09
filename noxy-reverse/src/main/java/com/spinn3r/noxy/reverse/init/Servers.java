package com.spinn3r.noxy.reverse.init;

import com.spinn3r.noxy.discovery.Endpoint;

/**
 *
 */
public class Servers {

    public static Server apply( ServerTemplate serverTemplate, Endpoint endpoint ) {
        return new Server( endpoint.getAddress(), endpoint.getAddress(), serverTemplate.getDisabled(), serverTemplate.getCheck(), serverTemplate.getInter() );
    }

}
