package com.spinn3r.noxy.forward.authenticator;

import org.littleshoot.proxy.ProxyAuthenticator;

/**
 *
 */
public class DefaultProxyAuthenticator implements ProxyAuthenticator {

    private final String username;

    private final String password;

    public DefaultProxyAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean authenticate(String username, String password) {

        if ( username == null || password == null )
            return false;

        return this.username.equals( username ) && this.password.equals( password );

    }

}
