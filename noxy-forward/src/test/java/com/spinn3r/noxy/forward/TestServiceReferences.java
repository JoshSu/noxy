package com.spinn3r.noxy.forward;

import com.spinn3r.artemis.http.init.DebugWebserverReferencesService;
import com.spinn3r.artemis.http.init.DefaultWebserverReferencesService;
import com.spinn3r.artemis.http.init.WebserverService;
import com.spinn3r.artemis.init.MockHostnameService;
import com.spinn3r.artemis.init.MockVersionService;
import com.spinn3r.artemis.init.ServiceReferences;
import com.spinn3r.artemis.logging.init.ConsoleLoggingService;
import com.spinn3r.artemis.metrics.init.MetricsService;
import com.spinn3r.artemis.network.init.DirectNetworkService;
import com.spinn3r.artemis.time.init.UptimeService;
import com.spinn3r.noxy.discovery.support.init.MembershipSupportService;
import com.spinn3r.noxy.forward.init.ForwardProxyService;

/**
 *
 */
public class TestServiceReferences extends ServiceReferences {

    public TestServiceReferences() {
        add( MockHostnameService.class );
        add( MockVersionService.class );
        add( UptimeService.class );
        add( MetricsService.class );
        add( ConsoleLoggingService.class );
        add( DirectNetworkService.class );
        add( MembershipSupportService.class );
        add( ForwardProxyService.class );
        add( DefaultWebserverReferencesService.class );
        add( DebugWebserverReferencesService.class );
        add( WebserverService.class );
    }

}
