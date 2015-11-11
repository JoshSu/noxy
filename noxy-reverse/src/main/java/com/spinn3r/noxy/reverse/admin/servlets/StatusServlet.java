package com.spinn3r.noxy.reverse.admin.servlets;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.spinn3r.artemis.json.JSON;
import com.spinn3r.noxy.reverse.init.Listener;
import com.spinn3r.noxy.reverse.meta.*;
import org.eclipse.jetty.servlet.DefaultServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 *
 */
public class StatusServlet extends DefaultServlet {

    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    private final Provider<ListenerMetaIndex> listenerMetaIndexProvider;

    @Inject
    StatusServlet(Provider<ListenerMetaIndex> listenerMetaIndexProvider) {
        this.listenerMetaIndexProvider = listenerMetaIndexProvider;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType( APPLICATION_JSON );
        response.setCharacterEncoding( UTF_8 );

        List<ListenerInfo> listenerInfos = Lists.newArrayList();

        for (ListenerMeta listenerMeta : listenerMetaIndexProvider.get().getListenerMetas()) {

            ListenerInfo listenerInfo = new ListenerInfo( listenerMeta.getListener(), listenerMeta.getServerMetaIndexProvider().get().getServers() );
            listenerInfos.add( listenerInfo );

        }

        AdminInfo adminInfo = new AdminInfo( listenerInfos );

        try( OutputStream out = response.getOutputStream() ) {
            out.write( JSON.toJSON( adminInfo ).getBytes( Charsets.UTF_8 ) );
        }

    }

    public class AdminInfo {

        private final List<ListenerInfo> listeners;

        public AdminInfo(List<ListenerInfo> listeners) {
            this.listeners = listeners;
        }

        public List<ListenerInfo> getListeners() {
            return listeners;
        }

    }

    public class ListenerInfo {

        private final Listener listener;

        private final ImmutableList<ServerMeta> servers;

        public ListenerInfo(Listener listener, ImmutableList<ServerMeta> servers) {
            this.listener = listener;
            this.servers = servers;
        }

        public Listener getListener() {
            return listener;
        }

        public ImmutableList<ServerMeta> getServers() {
            return servers;
        }

    }

}
