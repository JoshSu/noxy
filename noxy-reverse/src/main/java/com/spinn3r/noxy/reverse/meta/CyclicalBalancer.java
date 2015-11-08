package com.spinn3r.noxy.reverse.meta;

import com.spinn3r.artemis.util.misc.CyclicalConcurrentIterator;

import java.util.List;

/**
 *
 */
public class CyclicalBalancer<T> implements Balancer<T> {

    private final CyclicalConcurrentIterator<T> cyclicalConcurrentIterator;

    private final int size;

    public CyclicalBalancer( List<T> items ) {
        this.cyclicalConcurrentIterator = new CyclicalConcurrentIterator<>( items );
        this.size = items.size();

    }

    @Override
    public T next() {
        return cyclicalConcurrentIterator.next();
    }

    @Override
    public int size() {
        return size;
    }

}
