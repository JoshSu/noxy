package com.spinn3r.noxy.discovery.zookeeper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.noxy.discovery.Cluster;
import com.spinn3r.noxy.discovery.Membership;
import com.spinn3r.noxy.discovery.MembershipFactory;
import org.apache.curator.framework.CuratorFramework;

/**
 *
 */
public class ZKMembershipFactory implements MembershipFactory {

    private final Provider<CuratorFramework> curatorFrameworkProvider;

    @Inject
    ZKMembershipFactory(Provider<CuratorFramework> curatorFrameworkProvider) {
        this.curatorFrameworkProvider = curatorFrameworkProvider;
    }

    @Override
    public Membership create(Cluster cluster) {
        return new ZKMembership( curatorFrameworkProvider, cluster );
    }

}
