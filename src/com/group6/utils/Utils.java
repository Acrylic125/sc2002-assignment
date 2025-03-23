package com.group6.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Utils {

    /**
     * Go-style try catch.
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

    /**
     * Go-style try catch.
     *
     * @param runnable callback with no return.
     * @return result, whether it has the data or an error.
     */
    public static TryCatchResult<Void, Throwable> tryCatch(Runnable runnable) {
        try {
            runnable.run();
            return new TryCatchResult<>(null, null);
        } catch (Throwable e) {
            return new TryCatchResult<>(null, e);
        }
    }

}
