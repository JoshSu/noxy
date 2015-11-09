package com.spinn3r.noxy.discovery.none;

import com.spinn3r.noxy.discovery.*;

/**
 *
 */
public class NullMembershipFactory implements MembershipFactory {

    @Override
    public Membership create(Cluster cluster) {

        return new Membership() {
            @Override
            public void join(Endpoint endpoint) throws MembershipException {

            }

            @Override
            public void leave(Endpoint endpoint) throws MembershipException {

            }

        };

    }
}
