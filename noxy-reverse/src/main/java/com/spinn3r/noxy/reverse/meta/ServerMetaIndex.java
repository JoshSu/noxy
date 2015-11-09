package com.spinn3r.noxy.reverse.meta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spinn3r.artemis.init.AtomicReferenceProvider;
import com.spinn3r.noxy.discovery.Endpoint;
import com.spinn3r.noxy.reverse.init.Server;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class ServerMetaIndex {

    private static final AtomicLong ID_GENERATOR = new AtomicLong( 0 );

    private final Balancer<ServerMeta> balancer;

    private final long id;

    private final Map<String,ServerMeta> servers = new ConcurrentHashMap<>();

    private final AtomicReference<ImmutableList<ServerMeta>> serversReference = new AtomicReference<>( null );

    public ServerMetaIndex() {
        this.balancer = new NullBalancer<>();
        this.id = ID_GENERATOR.getAndIncrement();
        this.configure();
    }

    public ServerMetaIndex( List<ServerMeta> serverMetas ) {

        for (ServerMeta serverMeta : serverMetas) {
            add( serverMeta );
        }

        if ( servers.size() > 0 ) {
            this.balancer = new CyclicalBalancer<>( serverMetas );
        } else {
            // we have to use a null balancer because the cyclical balancer
            // doesn't work with zero servers.
            this.balancer = new NullBalancer<>();
        }

        this.id = ID_GENERATOR.getAndIncrement();
        this.configure();

    }

    public ImmutableList<ServerMeta> getServers() {
        return serversReference.get();
    }

    public Balancer<ServerMeta> getBalancer() {
        return balancer;
    }

    public long getId() {
        return id;
    }

    public void add( ServerMeta serverMeta ) {
        this.servers.put( key( serverMeta ), serverMeta );
        configure();
    }

    public void remove( String key ) {
        this.servers.remove( key );
        this.configure();
    }

    public String key( ServerMeta serverMeta ) {
        return serverMeta.getServer().getAddress();
    }

    public String key( Endpoint endpoint ) {
        return endpoint.getAddress();
    }

    private void configure() {
        this.serversReference.set( ImmutableList.copyOf( servers.values() ) );
    }

}
