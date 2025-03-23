package com.group6.utils;

/**
 *
 * @param <T> data type
 * @param <E> error type
 */
public final class TryCatchResult<T, E extends Throwable> {

    private final T data;
    private final E err;

    /**
     * Constructor for TryCatchResult.
     *
     * @param data data
     * @param err error
     */
    public TryCatchResult(T data, E err) {
        this.data = data;
        this.err = err;
    }

    /**
     * Data getter.
     *
     * @return {@link #data}
     */
    public T getData() {
        return data;
    }

    /**
     * Error getter.
     *
     * @return {@link #err}
     */
    public E getErr() {
        return err;
    }
}
