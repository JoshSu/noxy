package com.spinn3r.noxy.forward.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authentication {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                 "username='" + username + '\'' +
                 ", password='" + password + '\'' +
                 '}';
    }

}
