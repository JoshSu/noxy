package com.spinn3r.noxy.discovery;

/**
 *
 */
public class MembershipException extends Exception {

    public MembershipException() {
    }

    public MembershipException(String message) {
        super( message );
    }

    public MembershipException(String message, Throwable cause) {
        super( message, cause );
    }

    public MembershipException(Throwable cause) {
        super( cause );
    }

}
