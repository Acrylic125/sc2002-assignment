package com.group6.views;

import java.util.function.Supplier;

/**
 * Represents a view option. Used in situations where there are conditional
 * view options based on the user.
 */
public class ViewOption {
    private final String[] option;
    private final Supplier<View> callback;

    /**
     * Constructor for ViewOption.
     *
     * @param option  the option to be displayed
     * @param callback the callback to be executed when the option is selected
     */
    public ViewOption(String option, Supplier<View> callback) {
        this.option = new String[] { option };
        this.callback = callback;
    }

    /**
     * Constructor for ViewOption.
     *
     * @param option  the options to be displayed
     * @param callback the callback to be executed when the option is selected
     */
    public ViewOption(String[] option, Supplier<View> callback) {
        this.option = option;
        this.callback = callback;
    }

    /**
     * Option getter.
     *
     * @return {@link #option}
     */
    public String[] getOption() {
        return option;
    }

    /**
     * Callback getter.
     *
     * @return {@link #callback}
     */
    public Supplier<View> getCallback() {
        return callback;
    }
}
