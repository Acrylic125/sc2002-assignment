package com.group6.utils;

import java.util.Optional;

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
     * @param err  error
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
    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    /**
     * Error getter.
     *
     * @return {@link #err}
     */
    public Optional<E> getErr() {
        return Optional.ofNullable(err);
    }
}
