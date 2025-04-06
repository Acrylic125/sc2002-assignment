package com.group6.views.applicant;

import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.AuthenticatedView;
import com.group6.views.MenuView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.Scanner;

public class ApplicantHomeView implements AuthenticatedView {
    private ViewContext ctx;

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format("Applicant Home Menu", BashColors.BOLD));
            System.out.println("1. View All Projects");
            System.out.println("2. View My Applied Projects");
            System.out.println("3. View My Enquiries");
            System.out.println("4. Logout");
            System.out.println("");
            System.out.println("Type the option (e.g. 1, 2, 3) you want to select.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new ApplicantProjectsView();
                case "2":
                    return new ApplicantViewMyApplicationsView();
                case "3":
                    return new ApplicantViewMyEnquiriesView();
                case "4":
                    System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
                    ctx.setUser(null);
                    return new MenuView();
                default:
                    System.out.println("Invalid option.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

}
