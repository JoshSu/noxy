package com.spinn3r.noxy.logging;

import com.google.inject.Inject;
import com.spinn3r.artemis.time.Clock;

/**
 *
 */
public class LoggingHttpFiltersSourceAdapterFactory {

    private final Clock clock;

    @Inject
    LoggingHttpFiltersSourceAdapterFactory(Clock clock) {
        this.clock = clock;
    }

    public LoggingHttpFiltersSourceAdapter create( LogListener logListener ) {
        return new LoggingHttpFiltersSourceAdapter( clock, logListener );
    }

}
