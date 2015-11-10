package com.spinn3r.noxy.reverse;

import com.spinn3r.artemis.init.Initializer;
import com.spinn3r.artemis.init.InitializerBuilder;
import com.spinn3r.artemis.init.advertisements.Caller;

/**
 *
 */
public class Main {

    public static final String PRODUCT = "noxy";
    public static final String ROLE = "reverse";

    public static void main(String[] args) {

        try {

            Initializer initializer =
              InitializerBuilder.forRole( ROLE )
                .withProduct( PRODUCT )
                .build();

            initializer.replace( Caller.class, new Caller( Main.class) );
            initializer.launch( new ReverseProxyServiceReferences() );

            Thread.sleep( Long.MAX_VALUE );

        } catch (Exception e) {
            e.printStackTrace();
            System.exit( 1 );
        }

    }

}
