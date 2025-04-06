package com.group6.views;

import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.applicant.ApplicantProjectsView;
import com.group6.views.applicant.ApplicantViewMyApplicationsView;
import com.group6.views.applicant.ApplicantViewMyEnquiriesView;

import java.util.Scanner;

public class RoleBasedHomeView implements AuthenticatedView {

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
            System.out.println("");
            System.out.println("Type the option (e.g. 1, 2, 3) you want to select or leave empty ('') to cancel.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new ApplicantProjectsView();
                case "2":
                    return new ApplicantViewMyApplicationsView();
                case "3":
                    return new ApplicantViewMyEnquiriesView();
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
