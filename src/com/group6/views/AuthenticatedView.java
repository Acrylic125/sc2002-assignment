package com.group6.views;

import com.group6.users.User;

import java.util.Optional;
import java.util.Scanner;

/**
 * Represents an authenticated view with all the authentication utilities
 * used in an authenticated view.
 */
public interface AuthenticatedView extends View {

    /**
     * Renders the view for the authenticated user.
     *
     * @param ctx View context.
     * @param user The authenticated user.
     * @return The next view to render.
     */
    View render(ViewContext ctx, User user);

    /**
     * Renders the view for the authenticated user.
     *
     * @param ctx View context.
     * @return The next view to render.
     */
    @Override
    default View render(ViewContext ctx) {
        final Scanner scanner = ctx.getScanner();
        final Optional<User> userOpt = ctx.getUser();
        if (userOpt.isEmpty()) {
            System.out.println("You are not logged in. Please sign in.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }

        final User user = userOpt.get();
        if (!isAuthorized(user)) {
            System.out.println("You are not authorized to view this.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }

        return render(ctx, user);
    }

    /**
     * Checks if the user is authorized to view this view.
     * Override it to change the default behaviour.
     *
     * @param user The authenticated user.
     * @return True if the user is authorized, false otherwise.
     */
    default boolean isAuthorized(User user) {
        return true;
    };

}
