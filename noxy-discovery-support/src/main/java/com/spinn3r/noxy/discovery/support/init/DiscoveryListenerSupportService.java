package com.spinn3r.noxy.discovery.support.init;

import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.noxy.discovery.zookeeper.init.ZKNoxyDiscoveryService;

/**
 *
 */
@Config( path = "discovery-listener-support.conf",
         required = true,
         implementation = DiscoveryListenerSupportConfig.class )
public class DiscoveryListenerSupportService extends BaseService {

    private final DiscoveryListenerSupportConfig discoveryListenerSupportConfig;

    @Inject
    DiscoveryListenerSupportService(DiscoveryListenerSupportConfig discoveryListenerSupportConfig) {
        this.discoveryListenerSupportConfig = discoveryListenerSupportConfig;
    }

    @Override
    public void init() {

        switch ( discoveryListenerSupportConfig.getProvider() ) {

            case ZOOKEEPER:
                include( ZKNoxyDiscoveryService.class );
                break;

            default:
                throw new RuntimeException( "Unknown provider: " + discoveryListenerSupportConfig.getProvider() );

        }

    }

}
