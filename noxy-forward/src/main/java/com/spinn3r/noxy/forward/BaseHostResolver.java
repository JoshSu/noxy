package com.spinn3r.noxy.forward;

import com.google.common.collect.Lists;
import org.littleshoot.proxy.HostResolver;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 */
public abstract class BaseHostResolver implements HostResolver {

    private Random random = new Random();

    protected InetAddress randomize( List<InetAddress> addresses ) {

        // the JDK used to randomize on the first miss, then stick to the first
        // IP but this isn't good enough for a proxy box and we need to randomize
        // on each request.

        if ( addresses == null || addresses.size() == 0 )
            return null;

        int idx = random.nextInt( addresses.size() );
        return addresses.get( idx );

    }


    protected List<InetAddress> filter( InetAddress[] addresses, Class<? extends InetAddress> inetAddrType ) {

        List<InetAddress> result = Lists.newArrayList();

        for (InetAddress address : addresses) {
            if ( address.getClass().isAssignableFrom( inetAddrType ) ) {
                result.add( address );
            }
        }

        return result;

    }

}
