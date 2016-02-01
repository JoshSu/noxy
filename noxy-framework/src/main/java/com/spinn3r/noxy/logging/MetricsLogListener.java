package com.spinn3r.noxy.logging;

import com.google.inject.Inject;
import com.spinn3r.artemis.metrics.Stat;
import com.spinn3r.metrics.kairosdb.TaggedMetrics;
import io.netty.handler.codec.http.HttpObject;

import static com.spinn3r.artemis.metrics.Stat.stat;

/**
 *
 */
public class MetricsLogListener implements LogListener {

    private final TaggedMetrics taggedMetrics;

    private Stat<LogMessage> requestStat = null;

    private Stat<SecureLogMessage> secureRequestStat = null;

    @Inject
    MetricsLogListener(TaggedMetrics taggedMetrics, LogMessageTagsProvider logMessageTagsProvider) {
        this.taggedMetrics = taggedMetrics;
        this.requestStat = stat( taggedMetrics, logMessageTagsProvider, "requests" );
    }

    @Override
    public void onLogMessage(LogMessage logMessage) {
        requestStat.incr( logMessage );
    }

    @Override
    public void onSecureLogMessage(SecureLogMessage secureLogMessage) {

    }

    @Override
    public void clientToProxyRequest(HttpObject httpObject) {

    }

    @Override
    public void proxyToClientResponse(HttpObject httpObject) {

    }

}
