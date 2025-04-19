package com.group6.views;

import com.group6.users.User;
import com.group6.users.UserAuthenticator;
import com.group6.utils.BashColors;
import com.group6.utils.ValidateUtils;

import java.util.Optional;
import java.util.Scanner;

/**
 * Represents the login view where users can enter their NRIC and password to
 * authenticate.
 * This view handles user authentication and redirects to the appropriate home
 * view
 * based on the user's role.
 */
public class LoginView implements View {
    private static final int MAX_ATTEMPTS = 3;
    private ViewContext ctx;

    /**
     * Renders the login interface, allowing users to enter their credentials.
     *
     * @param ctx The view context containing dependencies like Scanner.
     * @return The next view based on login outcome (user's home view or main menu).
     */
    @Override
    public View render(ViewContext ctx) {
        this.ctx = ctx;

        Scanner scanner = ctx.getScanner();
        System.out.println(BashColors.format("User Login", BashColors.BOLD));

        final UserAuthenticator userAuthenticator = new UserAuthenticator(ctx.getBtoSystem().getUserManager());

        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            Optional<String> nricOpt = requestNric();
            if (nricOpt.isEmpty()) {
                return null;
            }

            Optional<String> passwordOpt = requestPassword();
            if (passwordOpt.isEmpty()) {
                return null;
            }

            User user = userAuthenticator.authenticate(nricOpt.get(), passwordOpt.get());
            if (user != null) {
                ctx.setUser(user);
                System.out.println(BashColors.format("Login successful! You are logged in as " + user.getName() + ".",
                        BashColors.GREEN));
                return new RoleBasedHomeView();
            }

            attempts++;
            int left = MAX_ATTEMPTS - attempts;
            if (left <= 0) {
                break;
            }
            System.out
                    .println(BashColors.format("Incorrect NRIC or password. Attempts left: " + left, BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }

        System.out.println(BashColors.format("Too many failed attempts. Cancelling...", BashColors.RED));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
        return null;
    }

    /**
     * Requests the user to input their NRIC.
     *
     * @return An Optional containing the NRIC if valid, or an empty Optional if
     *         cancelled.
     */
    private Optional<String> requestNric() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            System.out.println(BashColors.format("Type in your NRIC (e.g. S1234567A) or leave empty ('') to cancel.",
                    BashColors.BOLD));

            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                return Optional.empty();
            }

            if (!ValidateUtils.isValidNRIC(value)) {
                System.out.println(BashColors.format(
                        "Invalid NRIC. Format must start with S or T then 7 digits, and ending with a capital letter.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(value);
        }
    }

    /**
     * Requests the user to input their password.
     *
     * @return An Optional containing the password if provided, or an empty Optional
     *         if cancelled.
     */
    private Optional<String> requestPassword() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(BashColors.format("Type in your password or leave empty ('') to cancel.", BashColors.BOLD));

        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

}
