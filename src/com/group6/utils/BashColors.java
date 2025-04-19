package com.group6.utils;

/**
 * Bash colors utility.
 */
public enum BashColors {

    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    GRAY("\u001B[90m"),
    LIGHT_GRAY("\u001B[37m"),
    BLACK("\u001B[30m"),
    BOLD("\u001B[1m"),
    UNDERLINE("\u001B[4m");

    private final String code;

    /**
     * Constructor for BashColors.
     *
     * @param code ANSI escape code for the color.
     */
    BashColors(String code) {
        this.code = code;
    }

    /**
     * Get the ANSI escape code for the color.
     *
     * @return ANSI escape code.
     */
    @Override
    public String toString() {
        return code;
    }

    /**
     * Format a string with the color.
     *
     * @param text The text to format.
     * @return The formatted string.
     */
    public String format(String text) {
        return this + text + RESET;
    }

    /**
     * Format a string with multiple colors.
     *
     * @param text   The text to format.
     * @param colors The colors to apply.
     * @return The formatted string.
     */
    public static String format(String text, BashColors... colors) {
        StringBuilder sb = new StringBuilder();
        for (BashColors color : colors) {
            sb.append(color);
        }
        sb.append(text).append(BashColors.RESET);
        return sb.toString();
    }

}