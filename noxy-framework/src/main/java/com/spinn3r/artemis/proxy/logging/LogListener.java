package com.spinn3r.artemis.proxy.logging;

/**
 * A listener that can listen to the requests being proxied.
 */
public interface LogListener {

    void onLogMessage( LogMessage logMessage );

    void onSecureLogMessage( SecureLogMessage secureLogMessage );

}
