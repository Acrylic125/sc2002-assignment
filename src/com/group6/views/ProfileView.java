package com.group6.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.group6.users.RoleBasedUser;
import com.group6.users.User;
import com.group6.users.UserMaritalStatus;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;

public class ProfileView implements AuthenticatedView {

    private ViewContext ctx;
    private User user;

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        return showOptions();
    }

    private void showProfile() {
        System.out.println(BashColors.format("My Profile", BashColors.BOLD));
        System.out.println("Id: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Nric: " + user.getNric());
        System.out.println("Age: " + user.getAge());
        System.out.println("Marital Status: " + user.getMaritalStatus().getName());
        if (user instanceof RoleBasedUser) {
            System.out.println("Role: " + ((RoleBasedUser) user).getRole().getName());
        }
        System.out.println();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();
        final List<String> options = new ArrayList<>();
        options.add("'p' to change password");
        options.add("'a' to change age");
        options.add("'m' to change marital status");
        options.add("leave empty ('') to go back");

        final String optionsStr = Utils.joinStringDelimiter(
                options,
                ", ",
                " or ");

        while (true) {
            showProfile();
            System.out.println("Options: " + optionsStr + ":");

            String option = ctx.getScanner().nextLine().trim();
            switch (option) {
                case "p":
                    if (showChangePassword()) {
                        ctx.clearViewStack();
                        return new MenuView();
                    }
                    break;
                case "a":
                    showChangeAge();
                    break;
                case "m":
                    showChangeMaritalStatus();
                    break;
                case "":
                    return null;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private boolean showChangePassword() {
        final Scanner scanner = ctx.getScanner();
        System.out.println(BashColors.format("Change Password", BashColors.BOLD));
        System.out.println("Enter your new password or leave empty ('') to cancel:");
        System.out.println(
                BashColors.format("NOTE: You will be logged out upon password change.", BashColors.LIGHT_GRAY));
        String newPassword = scanner.nextLine().trim();
        if (newPassword.isEmpty()) {
            return false;
        }

        user.setPassword(newPassword);
        System.out.println(BashColors.format(
                "Password changed successfully. You have been logged out, please sign in again.", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
        return true;
    }

    private void showChangeAge() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            System.out.println(BashColors.format("Change Age", BashColors.BOLD));
            System.out.println("Enter your new age or leave empty ('') to cancel:");
            String newAge = scanner.nextLine().trim();
            if (newAge.isEmpty()) {
                return;
            }
            try {
                int age = Integer.parseInt(newAge);
                if (age < 0 || age > 120) {
                    System.out
                            .println(BashColors.format("Invalid age. Age must be between 0 and 120.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }
                user.setAge(age);
                System.out.println(BashColors.format("Age changed to " + age + ".", BashColors.GREEN));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return;
            } catch (NumberFormatException e) {
                System.out.println(BashColors.format("Invalid age.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
            }
        }
    }

    private void showChangeMaritalStatus() {
        final Scanner scanner = ctx.getScanner();
        final UserMaritalStatus[] maritalStatuses = UserMaritalStatus.values();

        while (true) {
            System.out.println(BashColors.format("Change Marital Status", BashColors.BOLD));
            for (UserMaritalStatus maritalStatus : maritalStatuses) {
                System.out.println("  " + maritalStatus.getName());
            }
            System.out.println();
            System.out.println("Enter your new marital status (e.g. 'Single') or leave empty ('') to cancel:");
            String newMaritalStatus = scanner.nextLine().trim().toUpperCase();
            if (newMaritalStatus.isEmpty()) {
                return;
            }

            UserMaritalStatus status = null;
            for (UserMaritalStatus maritalStatus : maritalStatuses) {
                if (maritalStatus.getName().equalsIgnoreCase(newMaritalStatus)) {
                    status = maritalStatus;
                    break;
                }
            }
            if (status == null) {
                System.out.println(BashColors.format("Invalid marital status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            user.setMartialStatus(status);
            System.out.println(
                    BashColors.format("Marital status changed to " + status.getName() + ".", BashColors.GREEN));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }
    }

}
