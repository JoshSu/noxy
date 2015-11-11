package com.spinn3r.noxy.reverse.admin.init;

import com.google.inject.Inject;
import com.spinn3r.artemis.http.ServletReferences;
import com.spinn3r.artemis.init.BaseService;
import com.spinn3r.noxy.reverse.admin.servlets.StatusServlet;

/**
 * Inject servlets for use to admin the proxy.
 */
public class ReverseProxyAdminWebserverReferencesService extends BaseService {

    private final ServletReferences servletReferences;

    private final StatusServlet statusServlet;

    @Inject
    ReverseProxyAdminWebserverReferencesService(ServletReferences servletReferences, StatusServlet statusServlet) {
        this.servletReferences = servletReferences;
        this.statusServlet = statusServlet;
    }

    @Override
    public void init() {
        servletReferences.add( "/status", statusServlet );
    }

}
