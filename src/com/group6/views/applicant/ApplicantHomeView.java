package com.group6.views.applicant;

import com.group6.users.Applicant;
import com.group6.users.User;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.Optional;
import java.util.Scanner;

public class ApplicantHomeView implements View {
    private ViewContext ctx;
    private User user;

    @Override
    public View render(ViewContext ctx) {
        final Scanner scanner = ctx.getScanner();
        final Optional<User> userOpt = ctx.getUser();
        if (userOpt.isEmpty()) {
            System.out.println("You are not logged in. Please sign in.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }
        this.ctx = ctx;
        this.user = userOpt.get();

        if (!(user instanceof Applicant)) {
            System.out.println("You must be an applicant in order to view this.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }

        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println("Applicant Home Menu");
            System.out.println("1. View All Projects");
            System.out.println("2. View My Applied Projects");
            System.out.println("3. View My Enquiries");
            System.out.println("");
            System.out.println("Type the number of the option you want to select or leave empty ('') to cancel.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new ApplicantProjectsView();
                case "2":
                    return new ApplicantViewAppliedProjects();
                case "":
                    return null;
                default:
                    System.out.println("Invalid option.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

}
