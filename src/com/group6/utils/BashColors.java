package com.group6.utils;

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

    BashColors(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public String format(String text) {
        return this + text + RESET;
    }

    public static String format(String text, BashColors... colors) {
        StringBuilder sb = new StringBuilder();
        for (BashColors color : colors) {
            sb.append(color);
        }
        sb.append(text).append(BashColors.RESET);
        return sb.toString();
    }

}
