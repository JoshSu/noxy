package com.spinn3r.noxy.discovery.zookeeper;

import java.util.List;

/**
 * Called during internal operations for unit testing purposes.
 */
public interface ZKDiscoveryStateListener {

    void onChildren( List<String> children );

}
