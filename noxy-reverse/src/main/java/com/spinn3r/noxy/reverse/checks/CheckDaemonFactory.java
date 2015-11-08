package com.spinn3r.noxy.reverse.checks;

import com.google.inject.Inject;
import com.spinn3r.noxy.reverse.init.Listener;
import com.spinn3r.noxy.reverse.meta.OnlineServerMetaIndexProvider;
import com.spinn3r.noxy.reverse.meta.ServerMetaIndexProvider;
import com.spinn3r.artemis.time.Clock;

/**
 *
 */
public class CheckDaemonFactory {

    private final Clock clock;

    @Inject
    CheckDaemonFactory(Clock clock) {
        this.clock = clock;
    }

    public CheckDaemon create( Listener listener, ServerMetaIndexProvider serverMetaIndexProvider, OnlineServerMetaIndexProvider onlineServerMetaIndexProvider) {
        return new CheckDaemon( clock, listener, serverMetaIndexProvider, onlineServerMetaIndexProvider );
    }

}
