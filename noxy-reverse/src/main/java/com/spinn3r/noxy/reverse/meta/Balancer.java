package com.spinn3r.noxy.reverse.meta;

/**
 * Balance requests and fetch the next item in the balancing index.
 */
public interface Balancer<T> {

    /**
     * Return the next item in the balance index or null if we are exhausted.
     *
     */
    T next();

    int size();

}


