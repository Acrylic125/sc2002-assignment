package com.group6.views;

import com.group6.users.*;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.utils.ValidateUtils;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

/**
 * Handles the user registration process for Applicants, Officers, and Managers.
 */
public class RegisterView implements View {

    private ViewContext ctx;

    /**
     * Runs the registration process, prompting the user for necessary details and
     * creating the user.
     * It will ask the user to input their personal details, and then create the
     * appropriate user.
     * After successful registration, it returns to the main menu.
     *
     * @return A MenuView instance, which represents the main menu after
     *         registration.
     */
    @Override
    public View render(ViewContext ctx) {
        this.ctx = ctx;

        final UserManager userManager = ctx.getBtoSystem().getUserManager();
        System.out.println(BashColors.format("User Registration", BashColors.BOLD));

        // 1. Choose role
        UserRole role = UserRole.APPLICANT;

        // 2. Common inputs
        Optional<String> nameOpt = requestName();
        if (nameOpt.isEmpty()) {
            return null;
        }

        Optional<String> nricOpt = requestNric();
        if (nricOpt.isEmpty()) {
            return null;
        }

        Optional<UserMaritalStatus> maritalStatusOpt = requestMaritalStatus();
        if (maritalStatusOpt.isEmpty()) {
            return null;
        }

        Optional<Integer> ageOpt = requestAge();
        if (ageOpt.isEmpty()) {
            return null;
        }

        Optional<String> passwordOpt = requestPassword();
        if (passwordOpt.isEmpty()) {
            return null;
        }

        User user = new RoleBasedUser(
                role, UUID.randomUUID().toString(), nameOpt.get(), nricOpt.get(), ageOpt.get(), maritalStatusOpt.get(),
                passwordOpt.get());
        userManager.registerUser(user);
        System.out.println(
                BashColors.format("✅ Registration successful as applicant! You can now log in.", BashColors.GREEN));

        return null;
    }

    /**
     * Requests the user to input their name.
     *
     * @return An Optional containing the name if valid, or an empty Optional if
     *         cancelled.
     */
    private Optional<String> requestName() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(
                BashColors.format("Type in your account name or leave empty ('') to cancel.", BashColors.BOLD));

        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    /**
     * Requests the user to input their NRIC.
     *
     * @return An Optional containing the NRIC if valid, or an empty Optional if
     *         cancelled.
     */
    private Optional<String> requestNric() {
        final Scanner scanner = ctx.getScanner();
        final UserManager userManager = ctx.getBtoSystem().getUserManager();
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
            if (userManager.getUserByNRIC(value).isPresent()) {
                System.out.println(BashColors.format("Invalid NRIC. User with the same NRIC is already registered.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(value);
        }
    }

    /**
     * Requests the user to input their marital status.
     *
     * @return An Optional containing the marital status if valid, or an empty
     *         Optional if cancelled.
     */
    private Optional<UserMaritalStatus> requestMaritalStatus() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            System.out.println(BashColors.format(
                    "Type in your marital status (SINGLE or MARRIED) or leave empty ('') to cancel.", BashColors.BOLD));

            String value = scanner.nextLine().trim().toUpperCase();
            if (value.equals("SINGLE")) {
                return Optional.of(UserMaritalStatus.SINGLE);
            }
            if (value.equals("MARRIED")) {
                return Optional.of(UserMaritalStatus.MARRIED);
            }
            System.out.println(BashColors.format(
                    "Invalid Marital status. Marital status must be either 'SINGLE' or 'MARRIED'.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }

    /**
     * Requests the user to input their age.
     *
     * @return An Optional containing the age if valid, or an empty Optional if
     *         cancelled.
     */
    private Optional<Integer> requestAge() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            System.out.println(BashColors.format("Type in your age or leave empty ('') to cancel.", BashColors.BOLD));

            String value = scanner.nextLine().trim().toUpperCase();
            TryCatchResult<Integer, Throwable> result = Utils.tryCatch(() -> Integer.parseInt(value));
            if (result.getErr().isPresent()) {
                System.out.println(BashColors.format("Invalid age. Age must be an integer.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            int age = result.getData().get();
            if (age < 0 || age > 120) {
                System.out.println(BashColors.format("Invalid age. Age must be between 0 and 120.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(age);
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
