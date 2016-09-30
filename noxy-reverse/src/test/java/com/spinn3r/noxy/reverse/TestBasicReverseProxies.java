package com.spinn3r.noxy.reverse;

import com.google.inject.Inject;
import com.spinn3r.artemis.init.BaseLauncherTest;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.network.NetworkException;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import com.spinn3r.artemis.network.builder.proxies.ProxyReferences;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.noxy.logging.LoggingHttpFiltersSourceAdapterFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
@Ignore
public class TestBasicReverseProxies extends BaseLauncherTest {

    @Inject
    HttpRequestBuilder httpRequestBuilder;

    @Inject
    LoggingHttpFiltersSourceAdapterFactory loggingHttpFiltersSourceAdapterFactory;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp( MockHostnameService.class,
                     MockVersionService.class,
                     DirectNetworkService.class,
                     ConsoleLoggingService.class );
    }

    @Test
    public void test1() throws Exception {


//        List<HostPort> hostPorts = HostPorts.createHostPorts( "136.243.74.82:80", "136.243.58.31:80", "136.243.58.32:80", "136.243.74.81:80" );
//
//        HostResolver hostResolver = new LoadBalancingReverseProxyHostResolver( serverMetaIndexProvider );
//
//        // FIXME: we have to implement
//        //
//        //    void proxyToServerResolutionSucceeded(String serverHostAndPort,
//        //                                          InetSocketAddress resolvedRemoteAddress);
//        //
//        // and then keep track of HTTP requests per backend server...
//
//        HttpProxyServerBootstrap httpProxyServerBootstrap = DefaultHttpProxyServer.bootstrap();
//        httpProxyServerBootstrap.withPort( 8080 );
//        httpProxyServerBootstrap.withServerResolver( hostResolver );
////        httpProxyServerBootstrap.withFiltersSource( new HttpFiltersSourceAdapter() {
////
////            @Override
////            public HttpFilters filterRequest(HttpRequest originalRequest) {
////                return new HttpFiltersAdapter( originalRequest ) {
////
////                    // https://github.com/adamfisk/LittleProxy/issues/76
////
////                    @Override
////                    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
////
////                        if (httpObject instanceof HttpRequest) {
////
////                            // FIXME: we have to balance on the URI AND the host name.
////
////                            HttpRequest httpRequest = (HttpRequest)httpObject;
////
//////                            try {
////                                //URI uri = new URI( httpRequest.getUri() );
//////                                 httpRequest.setUri( "http://cnn.com" );
////
//////                            } catch (URISyntaxException e) {
//////
//////                                // FIXME:
//////                                throw new RuntimeException( e );
//////                            }
////
////                        }
////
////                        return super.clientToProxyRequest( httpObject );
////
////                    }
////
////                };
////            }
////
////        } )
//
//        Log5jLogListener log5jLogListener = new Log5jLogListener();
//        LoggingHttpFiltersSourceAdapter loggingHttpFiltersSourceAdapter = loggingHttpFiltersSourceAdapterFactory.create( log5jLogListener );
//        httpProxyServerBootstrap.withFiltersSource( loggingHttpFiltersSourceAdapter );
//
//        httpProxyServerBootstrap.start();
//
//        // Thread.sleep( Long.MAX_VALUE );
//
//        for (int i = 0; i < 10; i++) {
//            String content = fetch( "http://spinn3r.com" );
//            assertThat( content, containsString( "Spinn3r" ) );
//
//        }

    }

    private String fetch( String link ) throws NetworkException {

        return httpRequestBuilder
                 .get( link )
                 .withProxy(ProxyReferences.create("http://localhost:8080" ) )
                 .execute()
                 .getContentWithEncoding();

    }

}

