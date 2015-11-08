package com.spinn3r.noxy.logging;

import com.spinn3r.log5j.Logger;

/**
 * A log listener that just logs messages to log5j (Spinn3r's internal async
 * logger).
 */
public class Log5jLogListener implements LogListener {

    private static final Logger log = Logger.getLogger();

    @Override
    public void onLogMessage(LogMessage logMessage) {
        log.info( "%s %s %s %s %sms %s %s",
                  logMessage.getHttpMethod(),
                  logMessage.getUri(),
                  logMessage.getProtocolVersion(),
                  logMessage.getHttpResponseStatus().code(),
                  logMessage.getDuration(),
                  logMessage.getResolutionServerHostAndPort(),
                  logMessage.getResolvedRemoteAddress() );
    }

    @Override
    public void onSecureLogMessage(SecureLogMessage secureLogMessage) {
        log.info( "%s %s %s %s %s",
                  secureLogMessage.getHttpMethod(),
                  secureLogMessage.getUri(),
                  secureLogMessage.getProtocolVersion(),
                  secureLogMessage.getResolutionServerHostAndPort(),
                  secureLogMessage.getResolvedRemoteAddress() );
    }

}
