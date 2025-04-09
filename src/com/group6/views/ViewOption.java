package com.group6.views;

import java.util.function.Supplier;

public class ViewOption {
    private final String[] option;
    private final Supplier<View> callback;

    public ViewOption(String option, Supplier<View> callback) {
        this.option = new String[] { option };
        this.callback = callback;
    }

    public ViewOption(String[] option, Supplier<View> callback) {
        this.option = option;
        this.callback = callback;
    }

    public String[] getOption() {
        return option;
    }

    public Supplier<View> getCallback() {
        return callback;
    }
}
