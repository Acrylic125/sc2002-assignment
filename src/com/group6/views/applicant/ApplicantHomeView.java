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

        return null;
    }

}
