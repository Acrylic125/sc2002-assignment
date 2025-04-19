package com.group6.views;

import com.group6.utils.BashColors;

import java.util.Scanner;

/**
 * Represents the initial authentication menu.
 * Allows users to login, register, or exit the application.
 */
public class MenuView implements View {

    /**
     * View renderer.
     *
     * @param ctx view context
     * @return next view
     */
    @Override
    public View render(ViewContext ctx) {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format("Welcome to HDB System", BashColors.BOLD));
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("");
            System.out.println("Type the option (e.g. 1, 2, 3) you want to select.");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return new LoginView();
                case "2":
                    return new RegisterView();
                case "3":
                    System.out.println(BashColors.format("Goodbye!", BashColors.BOLD));
                    return null;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }
}
