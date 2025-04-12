package com.group6.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    /**
     * Formats a Date object to DD/MM/YYYY pattern.
     * 
     * @param date The Date object to format
     * @return A string representation of the date in DD/MM/YYYY format
     */
    public static String formatToDDMMYYYY(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    /**
     * Formats a Date object to DD/MM/YYYY HH:mm:ss pattern.
     *
     * @param date The Date object to format
     * @return A string representation of the date in DD/MM/YYYY HH:mm:ss format
     */
    public static String formatToDDMMYYYYWithTime(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    public static String joinStringDelimiter(List<String> options, String delimiter, String lastDelimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            sb.append(options.get(i));
            if (i < options.size() - 1) {
                if (i == options.size() - 2) {
                    sb.append(lastDelimiter);
                } else {
                    sb.append(delimiter);
                }
            }
        }

        return sb.toString();
    }

}
