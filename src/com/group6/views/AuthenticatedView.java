package com.group6.views;

import com.group6.users.User;

import java.util.Optional;
import java.util.Scanner;

public interface AuthenticatedView extends View {

    View render(ViewContext ctx, User user);

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

    default boolean isAuthorized(User user) {
        return true;
    };

}
