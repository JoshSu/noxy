package com.spinn3r.noxy.reverse.checks;

import com.google.common.collect.Lists;
import com.spinn3r.noxy.reverse.init.Listener;
import com.spinn3r.noxy.reverse.meta.*;
import com.spinn3r.artemis.time.Clock;
import com.spinn3r.log5j.Logger;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class CheckDaemon implements Runnable {

    private static final Logger log = Logger.getLogger();

    private final Clock clock;

    private final Listener listener;

    private final ServerMetaIndexProvider serverMetaIndexProvider;

    private final OnlineServerMetaIndexProvider onlineServerMetaIndexProvider;

    private final AtomicBoolean terminated = new AtomicBoolean( false );

    CheckDaemon(Clock clock, Listener listener, ServerMetaIndexProvider serverMetaIndexProvider, OnlineServerMetaIndexProvider onlineServerMetaIndexProvider) {
        this.clock = clock;
        this.listener = listener;
        this.serverMetaIndexProvider = serverMetaIndexProvider;
        this.onlineServerMetaIndexProvider = onlineServerMetaIndexProvider;
    }

    @Override
    public void run() {

        try {

            while( true ) {

                if (terminated.get()) {
                    return;
                }

                long millisUntilNextCheck = performChecks();

                // now wait until the next check
                clock.sleepUninterruptibly( millisUntilNextCheck, TimeUnit.MILLISECONDS );

            }

        } catch ( Throwable t ) {
            log.error( "Could not continue to execute the check daemon: ", t );
        }

    }

    public void markTerminated() {
        this.terminated.set( true );
    }

    private List<ServerMeta> computeServersNeedingChecks(ServerMetaIndex serverMetaIndex) {

        List<ServerMeta> result = Lists.newArrayList();

        long now = clock.currentTimeMillis();

        for (ServerMeta serverMeta : serverMetaIndex.getServers()) {

            if ( Math.abs( now - serverMeta.getLastChecked() ) > serverMeta.getServer().getInter() ) {
                result.add( serverMeta );
            }

        }

        return result;

    }

    private long performChecks()  {

        ServerMetaIndex serverMetaIndex = serverMetaIndexProvider.get();

        // compute the checks which need to be updated as they have expired
        List<ServerMeta> serversNeedingChecks = computeServersNeedingChecks(serverMetaIndex);

        // the way this work is that we run one interval and create an
        // async socket per server testing if the port is open... if
        // it's open then the host passes, otherwise it fails and we take
        // it offline.

        List<ServerMetaFuture> serverMetaFutures = initiateChecks( serversNeedingChecks );

        // now see which ones have timed out...

        collectChecks( serverMetaFutures );

        List<ServerMeta> onlineServers = computeOnlineServers( serverMetaFutures );

        onlineServerMetaIndexProvider.set( new ServerMetaIndex( Lists.newCopyOnWriteArrayList( onlineServers )  ) );

        return computeTimeUntilNextCheck( serverMetaIndex.getServers() );

    }

    public long computeTimeUntilNextCheck( List<ServerMeta> serverMetas ) {

        long result = Long.MAX_VALUE;

        for (ServerMeta serverMeta : serverMetas) {

            if ( serverMeta.getServer().getInter() < result ) {
                result = serverMeta.getServer().getInter();
            }

        }

        return result;

    }

    private List<ServerMeta> computeOnlineServers( List<ServerMetaFuture> serverMetaFutures ) {

        List<ServerMeta> result = Lists.newArrayList();

        for (ServerMetaFuture serverMetaFuture : serverMetaFutures) {

            if ( ! serverMetaFuture.getServerMeta().getOffline() ) {
                result.add( serverMetaFuture.getServerMeta() );
            }

        }

        return result;

    }

    private void collectChecks(List<ServerMetaFuture> serverMetaFutures) {

        for (ServerMetaFuture serverMetaFuture : serverMetaFutures) {

            ServerMeta serverMeta = serverMetaFuture.getServerMeta();

            StateChange stateChange;

            try {

                serverMetaFuture.getConnectFuture().get( listener.getChecks().getTimeout(), TimeUnit.MILLISECONDS );
                stateChange = serverMeta.setOffline( false );

            } catch ( CancellationException|ExecutionException|TimeoutException e ) {

                stateChange = serverMeta.setOffline( true );

            } catch ( InterruptedException e ) {

                // just let the daemon complete.
                continue;

            } finally {

                if ( serverMeta.getLastChecked() == 0 ) {
                    // the state is ALWAYS changed if we've never changed before.
                    stateChange = StateChange.CHANGED;
                }

                serverMeta.setLastChecked( clock.currentTimeMillis() );

                // make sure to clean up after ourselves to avoid bleeding file handles/etc
                try {
                    serverMetaFuture.getAsynchronousSocketChannel().close();
                } catch (IOException e) {
                    // this shouldn't ever happen.
                    log.error( "Unable to close socket: ", e );
                }

            }

            if ( StateChange.CHANGED.equals( stateChange ) ) {
                log.info( "Server %s offline state changed (offline=%s)", serverMeta.getServer().getAddress(), serverMeta.getOffline() );
            }

        }

    }

    private List<ServerMetaFuture> initiateChecks(List<ServerMeta> serversNeedingChecks) {

        List<ServerMetaFuture> serverMetaFutures = Lists.newArrayList();

        for ( ServerMeta serverMeta : serversNeedingChecks ) {

            if ( ! serverMeta.getServer().getCheck() )
                continue;

            if ( serverMeta.getServer().getInter() <= 0 ) {
                log.warn( "Server %s has wrong check inter: %s", serverMeta.getServer().getAddress(), serverMeta.getServer().getInter() );
                continue;
            }

            try {

                AsynchronousSocketChannel asynchronousSocketChannel = AsynchronousSocketChannel.open();

                Future<Void> connectFuture = asynchronousSocketChannel.connect( serverMeta.getInetSocketAddress() );

                serverMetaFutures.add( new ServerMetaFuture( serverMeta, connectFuture, asynchronousSocketChannel ) );

            } catch (IOException e) {
                // I don't think this will ever happen.
                log.error( "Unable to open socket: ", e );
            }

        }

        return serverMetaFutures;

    }

    class ServerMetaFuture {

        private ServerMeta serverMeta;

        private Future<Void> connectFuture;

        private AsynchronousSocketChannel asynchronousSocketChannel;

        public ServerMetaFuture(ServerMeta serverMeta, Future<Void> connectFuture, AsynchronousSocketChannel asynchronousSocketChannel) {
            this.serverMeta = serverMeta;
            this.connectFuture = connectFuture;
            this.asynchronousSocketChannel = asynchronousSocketChannel;
        }

        public ServerMeta getServerMeta() {
            return serverMeta;
        }

        public Future<Void> getConnectFuture() {
            return connectFuture;
        }

        public AsynchronousSocketChannel getAsynchronousSocketChannel() {
            return asynchronousSocketChannel;
        }

    }

}
