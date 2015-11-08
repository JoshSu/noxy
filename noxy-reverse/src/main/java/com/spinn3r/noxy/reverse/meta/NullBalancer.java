package com.spinn3r.noxy.reverse.meta;

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
