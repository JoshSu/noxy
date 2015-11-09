package com.spinn3r.noxy.discovery.support.init;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MembershipSupportConfig {

    private MembershipProvider provider = MembershipProvider.NONE;

    public MembershipProvider getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return "MembershipSupportConfig{" +
                 "provider=" + provider +
                 '}';
    }

}
