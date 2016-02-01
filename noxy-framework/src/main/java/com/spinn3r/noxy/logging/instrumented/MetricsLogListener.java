package com.spinn3r.noxy.logging.instrumented;

import com.google.inject.Inject;
import com.spinn3r.artemis.metrics.Stat;
import com.spinn3r.metrics.kairosdb.TaggedMetrics;
import com.spinn3r.noxy.logging.LogListener;
import com.spinn3r.noxy.logging.LogMessage;
import com.spinn3r.noxy.logging.SecureLogMessage;
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
    MetricsLogListener(TaggedMetrics taggedMetrics, LogMessageTagsProvider logMessageTagsProvider, SecureLogMessageTagsProvider secureLogMessageTagsProvider) {
        this.taggedMetrics = taggedMetrics;
        this.requestStat = stat( taggedMetrics, logMessageTagsProvider, "requests" );
        this.secureRequestStat = stat( taggedMetrics, secureLogMessageTagsProvider, "requests" );
    }

    @Override
    public void onLogMessage(LogMessage logMessage) {
        requestStat.incr( logMessage );
    }

    @Override
    public void onSecureLogMessage(SecureLogMessage secureLogMessage) {
        secureRequestStat.incr( secureLogMessage );
    }

    @Override
    public void clientToProxyRequest(HttpObject httpObject) {

    }

    @Override
    public void proxyToClientResponse(HttpObject httpObject) {

    }

}
