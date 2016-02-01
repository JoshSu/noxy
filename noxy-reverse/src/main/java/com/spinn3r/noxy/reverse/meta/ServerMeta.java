package com.spinn3r.noxy.reverse.meta;

import com.spinn3r.log5j.Logger;
import com.spinn3r.noxy.reverse.init.Server;
import com.spinn3r.noxy.reverse.net.InetSocketAddresses;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Metadata around a server so we can determine when it was last checked.
 */
public class ServerMeta {

    private static final Logger log = Logger.getLogger();

    private final Server server;

    private AtomicLong lastChecked = new AtomicLong( 0 );

    private InetSocketAddress inetSocketAddress;

    private AtomicBoolean offline = new AtomicBoolean( true );

    public ServerMeta(Server server) throws UnknownHostException {
        this.server = server;
        this.inetSocketAddress = InetSocketAddresses.parse( server.getAddress() );

        if ( ! server.getCheck() ) {
            this.setOffline( false );
        }

    }

    public long getLastChecked() {
        return lastChecked.get();
    }

    public void setLastChecked( long lastChecked ) {
        this.lastChecked.set( lastChecked );
    }

    public boolean getOffline() {
        return offline.get();
    }

    public StateChange setOffline( boolean offline ) {

        log.debug( "Marking server %s (%s) offline=%s", server.getAddress(), server.getName(), offline );

        boolean prev = this.offline.getAndSet( offline );

        if ( prev != offline ) {
            return StateChange.CHANGED;
        } else {
            return StateChange.NOT_CHANGED;
        }

    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public String toString() {
        return "ServerMeta{" +
                 "server=" + server +
                 ", lastChecked=" + lastChecked +
                 ", inetSocketAddress=" + inetSocketAddress +
                 ", offline=" + offline +
                 '}';
    }

}
