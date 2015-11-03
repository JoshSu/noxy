package com.spinn3r.artemis.proxy;

import com.google.inject.Injector;
import com.spinn3r.artemis.init.Initializer;
import com.spinn3r.artemis.init.advertisements.Caller;
import com.spinn3r.artemis.init.config.ResourceConfigLoader;
import com.spinn3r.artemis.network.builder.HttpRequestBuilder;
import junit.framework.TestCase;
import org.junit.Test;

import java.awt.Robot;

import static com.spinn3r.artemis.util.text.TextFormatter.wrap;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ProxyServiceReferencesTest {

    public static final String ROLE = "robot";

    @Test
    public void testBindings() throws Exception {

        Initializer initializer = new Initializer( ROLE, new ResourceConfigLoader() );

        initializer.replace( Caller.class, new Caller( Robot.class) );

        initializer.init( new ProxyServiceReferences() );

        Injector injector = initializer.getInjector();

    }

}