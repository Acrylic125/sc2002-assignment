package com.group6.views;

/**
 * Represents a view, to interface with the {@link com.group6.BTOSystem}.
 */
public interface View {
    /**
     * Renders the interface for the user to access the {@link com.group6.BTOSystem}.
     *
     * @param ctx View context.
     */
    void render(ViewContext ctx);
}
