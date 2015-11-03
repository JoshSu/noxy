package com.spinn3r.artemis.proxy.reverse.meta;

/**
 *
 */
public class NullBalancer<T> implements Balancer<T> {

    @Override
    public T next() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

}
