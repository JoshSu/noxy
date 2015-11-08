package com.spinn3r.noxy.reverse.meta;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 *
 */
public class ListenerMetaIndex {

    private final List<ListenerMeta> listenerMetas;

    public ListenerMetaIndex(List<ListenerMeta> listenerMetas) {
        this.listenerMetas = listenerMetas;
    }

    public ImmutableList<ListenerMeta> getListenerMetas() {
        return ImmutableList.copyOf( listenerMetas );
    }

}
