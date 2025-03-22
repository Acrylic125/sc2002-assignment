package com.group6.views;

import com.group6.BTOSystem;

import java.util.Scanner;
import java.util.Stack;

public final class ViewContext {

    private final BTOSystem btoSystem;
    private final Scanner scanner;
    private final Stack<View> viewStack = new Stack<>();

    /**
     *
     * @param btoSystem bto system
     * @param scanner scanner for input
     */
    public ViewContext(BTOSystem btoSystem, Scanner scanner) {
        this.btoSystem = btoSystem;
        this.scanner = scanner;
    }

    /**
     * Getter for BTO System
     *
     * @return btoSystem
     */
    public BTOSystem getBtoSystem() {
        return btoSystem;
    }

    /**
     * Getter for scanner. Avoid recreating Scanner instances, use this shared instance.
     *
     * @return scanner
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     *
     * @return The top view.
     * @throws IllegalStateException If there are no more views.
     */
    public View getCurrentView() throws IllegalStateException {
        if (viewStack.isEmpty()) {
            throw new IllegalStateException("View stack is empty.");
        }
        return viewStack.peek();
    }

    /**
     * Goes to view
     *
     * @param view view to show next
     */
    public void pushView(View view) {
        viewStack.push(view);
        view.render(this);
    }

    /**
     * Goes to the previous view
     */
    public void popView() {
        View v = viewStack.pop();
        if (v == null) {
            return;
        }
        v.render(this);
    }


}
