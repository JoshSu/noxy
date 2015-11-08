package com.spinn3r.noxy.reverse.meta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spinn3r.noxy.reverse.init.Server;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class ServerMetaIndex {

    private static final AtomicLong ID_GENERATOR = new AtomicLong( 0 );

    private final ImmutableList<ServerMeta> servers;

    private final Balancer<ServerMeta> balancer;

    private final long id;

    public ServerMetaIndex() throws UnknownHostException {
        this.servers = ImmutableList.of();
        this.balancer = new NullBalancer<>();
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public ServerMetaIndex( List<ServerMeta> servers ) {
        this.servers = ImmutableList.copyOf( servers );

        if ( servers.size() > 0 ) {
            this.balancer = new CyclicalBalancer<>( servers );
        } else {
            // we have to use a null balancer because the cyclical balancer
            // doesn't work with zero servers.
            this.balancer = new NullBalancer<>();
        }

        this.id = ID_GENERATOR.getAndIncrement();

    }

    public ImmutableList<ServerMeta> getServers() {
        return servers;
    }

    public Balancer<ServerMeta> getBalancer() {
        return balancer;
    }

    public long getId() {
        return id;
    }

    public static ServerMetaIndex fromServers( List<Server> servers ) throws UnknownHostException {

        List<ServerMeta> serverMetas = Lists.newArrayList();

        for (Server server : servers) {
            serverMetas.add( new ServerMeta( server ) );
        }

        return new ServerMetaIndex( serverMetas );

    }

}
