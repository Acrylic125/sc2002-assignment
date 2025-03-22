package com.group6.utils;

import java.util.function.Supplier;

public class Utils {

    /**
     * Go-style try catch
     *
     * @param supplier callback with the return data.
     * @return result, whether it has the data or an error.
     * @param <T> data type
     */
    public static <T> TryCatchResult<T, Throwable> tryCatch(Supplier<T> supplier) {
        try {
            return new TryCatchResult<>(supplier.get(), null);
        } catch (Throwable e) {
            return new TryCatchResult<>(null, e);
        }
    }

}
