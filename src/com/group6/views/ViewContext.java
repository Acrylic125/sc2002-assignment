package com.group6.views;

import com.group6.BTOSystem;
import com.group6.users.User;

import java.util.Optional;
import java.util.Scanner;
import java.util.Stack;

/**
 * Represents the context of a {@link View}.
 */
public final class ViewContext {

    private final BTOSystem btoSystem;
    private final Scanner scanner;
    private final Stack<View> viewStack = new Stack<>();
    private User user;

    /**
     *
     * @param btoSystem bto system.
     * @param scanner   scanner for input.
     */
    public ViewContext(BTOSystem btoSystem, Scanner scanner) {
        this.btoSystem = btoSystem;
        this.scanner = scanner;
    }

    /**
     * BTO System getter.
     *
     * @return {@link #btoSystem}
     */
    public BTOSystem getBtoSystem() {
        return btoSystem;
    }

    /**
     * Getter for scanner. Avoid recreating Scanner instances, use this shared
     * instance.
     *
     * @return {@link #scanner}
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
     * Start from a view.
     *
     * @param view root view.
     */
    public void startFromView(View view) throws RuntimeException {
        if (!viewStack.isEmpty()) {
            throw new IllegalStateException(
                    "View stack is not empty. Can only start from a view if it was not started before.");
        }
        viewStack.push(view);
        while (!viewStack.isEmpty()) {
            View current = viewStack.peek();
            View next = current.render(this);
            if (current == next) {
                // View did not change.
                continue;
            }
            if (next == null) {
                viewStack.pop();
            } else {
                viewStack.push(next);
            }
        }
    }

    /**
     * User getter.
     *
     * @return {@link #user} can be null (i.e. not logged in).
     */
    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    /**
     * User setter.
     *
     * @param user user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

}
