package com.spinn3r.noxy;

import java.io.IOException;

import static com.jayway.awaitility.Awaitility.await;

/**
 *
 */
public class NetworkTests {
    /**
     * Make sure that the first request works, which might take a few seconds
     * due to service startup, then perform the HTTP requests and then return.
     */
    public static void test( int nrRequests , NetworkRequester networkRequester ) throws IOException {

        await().until( () -> {
            try {
                networkRequester.request();
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
        } );

        for (int i = 0; i < nrRequests; i++) {

            System.out.printf( "====: execution request\n" );

            networkRequester.request();
        }

    }

}
