package com.spinn3r.noxy.forward.init;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.artemis.init.AtomicReferenceProvider;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.artemis.init.Config;
import com.spinn3r.artemis.init.advertisements.Hostname;
import com.spinn3r.artemis.init.resource_mutexes.PortMutex;
import com.spinn3r.artemis.init.resource_mutexes.PortMutexes;
import com.spinn3r.artemis.init.resource_mutexes.ResourceMutexException;
import com.spinn3r.artemis.util.net.HostPort;
import com.spinn3r.noxy.Allocator;
import com.spinn3r.noxy.discovery.*;
import com.spinn3r.noxy.resolver.BalancingProxyHostResolver;
import com.spinn3r.noxy.resolver.IPV4ProxyHostResolver;
import com.spinn3r.noxy.resolver.IPV6ProxyHostResolver;
import com.spinn3r.noxy.resolver.StandardProxyHostResolver;
import com.spinn3r.noxy.forward.filters.ForwardProxyHttpFiltersSourceAdapter;
import com.spinn3r.noxy.logging.Log5jLogListener;
import com.spinn3r.noxy.logging.LoggingHttpFiltersSourceAdapterFactory;
import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Config( path = "forward-proxy.conf",
         required = true,
         implementation = ForwardProxyConfig.class )
public class ForwardProxyService extends BaseService {

    private final ForwardProxyConfig forwardProxyConfig;

    private final LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory;

    private final MembershipFactory membershipFactory;

    private final Provider<Hostname> hostnameProvider;

    private final PortMutexes portMutexes;

    private final ForwardProxyPorts forwardProxyPorts = new ForwardProxyPorts();

    private final Provider<ForwardProxyPorts> forwardProxyPortsProvider = new AtomicReferenceProvider<>( forwardProxyPorts );

    private final List<HttpProxyServer> httpProxyServers = Lists.newArrayList();

    private final List<ProxyServerMeta> proxyServerMetas = Lists.newArrayList();

    @Inject
    ForwardProxyService(ForwardProxyConfig forwardProxyConfig, LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory, MembershipFactory membershipFactory, Provider<Hostname> hostnameProvider, PortMutexes portMutexes) {
        this.forwardProxyConfig = forwardProxyConfig;
        this.loggingHttpFiltersSourceAdapterFactory = loggingHttpFiltersSourceAdapterFactory;
        this.membershipFactory = membershipFactory;
        this.hostnameProvider = hostnameProvider;
        this.portMutexes = portMutexes;
    }

    @Override
    public void init() {
        provider( ForwardProxyPorts.class, forwardProxyPortsProvider );

        if ( forwardProxyConfig.getAllocator().equals( Allocator.POOLED ) ) {
            info( "Enabling pooled allocator for netty" );
            // this is a non-ideal way to set the allocator but right now
            // littleproxy doesn't expose this setting directly.
            System.setProperty( "io.netty.allocator.type", "pooled" );
        }

    }

    @Override
    public void start() throws Exception {

        int id = 0;

        for ( Proxy proxy : forwardProxyConfig.getProxies() ) {

            List<ProxyServerDescriptor> servers = proxy.getServers();

            if ( servers == null || servers.size() == 0 ) {
                warn( "No servers defined." );
                return;
            }

            info( "Starting proxy %,d in cluster %s and datacenter %s", id, proxy.getCluster(), proxy.getDatacenter() );

            Membership membership = null;

            if ( proxy.getCluster() != null ) {
                membership = membershipFactory.create( proxy.getCluster() );
            }

            HttpProxyServer proto = create( DefaultHttpProxyServer.bootstrap(), servers.get( 0 ), proxy, membership );
            httpProxyServers.add( proto );

            for (int i = 1; i < servers.size(); i++) {
                ProxyServerDescriptor proxyServerDescriptor = servers.get( i );
                httpProxyServers.add( create( proto.clone(), proxyServerDescriptor, proxy, membership ) );
            }

            ++id;

        }

    }

    private HttpProxyServer create( HttpProxyServerBootstrap httpProxyServerBootstrap,
                                    ProxyServerDescriptor proxyServerDescriptor,
                                    Proxy proxy,
                                    Membership membership ) throws MembershipException {

        info( "Creating proxy server: %s", proxyServerDescriptor );

        HostPort addressHostPort = new HostPort( proxyServerDescriptor.getInbound().getAddress(), proxyServerDescriptor.getInbound().getPort() );

        PortMutex portMutex = null;

        if ( addressHostPort.getPort() <= 0 ) {

            try {

                portMutex = portMutexes.acquire( 8090, 9090 );

                addressHostPort = new HostPort( addressHostPort.getHostname(), portMutex.getPort() );

            } catch (ResourceMutexException e) {
                // this only really applies to testing scenarios.
                throw new RuntimeException( "Unable to acquire port mutex: ", e );
            }

        }

        forwardProxyPorts.register( proxyServerDescriptor.getName(), addressHostPort.getPort() );

        InetSocketAddress address = new InetSocketAddress( addressHostPort.getHostname(), addressHostPort.getPort() );
        InetSocketAddress networkInterface = new InetSocketAddress( proxyServerDescriptor.getOutbound().getAddress(), proxyServerDescriptor.getOutbound().getPort() );

        String name = proxyServerDescriptor.getName();

        if ( name == null ) {
            name = proxyServerDescriptor.getInbound().getAddress() + ":" + proxyServerDescriptor.getInbound().getPort();
        }

        HostResolver hostResolver;

        switch ( proxy.getHostResolutionMethod() ) {

            case IPV4:
                hostResolver = new IPV4ProxyHostResolver();
                break;

            case IPV6:
                hostResolver = new IPV6ProxyHostResolver();
                break;

            case BALANCING:
                hostResolver = new BalancingProxyHostResolver();
                break;

            case STANDARD:
                hostResolver = new StandardProxyHostResolver();
                break;

            default:
                throw new RuntimeException( "Unknown host resolution method: " + proxy.getHostResolutionMethod() );

        }

        // use a custom HostResolver for ipv6 and then one for ipv4 depending
        // on which mode a connection is taking.

        info( "Using host resolver: %s", hostResolver.getClass() );

        httpProxyServerBootstrap
          .withName( name )
          .withAddress( address )
          .withServerResolver( hostResolver )
          .withNetworkInterface( networkInterface );

        HttpFiltersSourceAdapter httpFiltersSourceAdapter = new HttpFiltersSourceAdapter();

        if ( proxy.getLogging() ) {
            Log5jLogListener log5jLogListener = new Log5jLogListener( proxy.getTracing() );
            httpFiltersSourceAdapter = loggingHttpFiltersSourceAdapterFactory.create( log5jLogListener );
        }

        httpProxyServerBootstrap.withFiltersSource( new ForwardProxyHttpFiltersSourceAdapter( httpFiltersSourceAdapter ) );

        HttpProxyServer httpProxyServer = httpProxyServerBootstrap.start();

        if ( membership != null ) {

            Endpoint endpoint = new Endpoint( addressHostPort.format(),
                                              hostnameProvider.get().getValue(),
                                              EndpointType.FORWARD_PROXY,
                                              proxy.getDatacenter() );

            info( "Advertising endpoint: %s", endpoint );

            membership.join( endpoint );

        }

        proxyServerMetas.add( new ProxyServerMeta( httpProxyServer, portMutex ) );

        return httpProxyServer;

    }

    @Override
    public void stop() throws Exception {

        List<ProxyServerMeta> proxyServerMetasReversed = Lists.newArrayList( proxyServerMetas );
        Collections.reverse( proxyServerMetasReversed );

        for (ProxyServerMeta proxyServerMeta : proxyServerMetasReversed) {
            proxyServerMeta.getHttpProxyServer().stop();
            if( proxyServerMeta.getPortMutex() != null ) {
                proxyServerMeta.getPortMutex().close();
            }
        }

    }

}
