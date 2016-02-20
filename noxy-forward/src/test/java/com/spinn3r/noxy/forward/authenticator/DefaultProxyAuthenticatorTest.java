package com.spinn3r.noxy.forward.authenticator;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class DefaultProxyAuthenticatorTest {

    DefaultProxyAuthenticator sut = new DefaultProxyAuthenticator( "hello", "world" );

    @Test
    public void testAuthenticate() throws Exception {

        assertTrue( sut.authenticate( "hello", "world" ) );

    }

    @Test
    public void testAuthenticateWithNullUsername() throws Exception {

        assertFalse( sut.authenticate( null, "world" ) );

    }


    @Test
    public void testAuthenticateWithNullPassword() throws Exception {

        assertFalse( sut.authenticate( "hello", null ) );

    }


    @Test
    public void testAuthenticateWithWrongPassword() throws Exception {

        assertFalse( sut.authenticate( "hello", "asdf" ) );

    }


}