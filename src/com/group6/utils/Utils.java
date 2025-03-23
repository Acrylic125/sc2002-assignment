package com.group6.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
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

    /**
     * Converts a double to a comma-separated format, rounded to 2 decimal places.
     * 
     * @param value The double value to format
     * @return A string representation of the value with commas and 2 decimal places
     */
    public static String formatMoney(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(value);
    }

}
