package com.group6.views.hdbofficer;

import java.util.List;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.Applicant;
import com.group6.users.User;
import com.group6.users.HDBOfficer;
import com.group6.views.*;
import com.group6.views.applicant.*;

public class HDBOfficerManageView implements AuthenticatedView{
    private ViewContext ctx;
    private User user;
    @Override
    public boolean isAuthorized(User user) {
        return user instanceof HDBOfficer;
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;
        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println("HDB Manage Home Menu");
            System.out.println("1. Register to Manage Project");
            System.out.println("2. View All Registrations");
            System.out.println("3. View Successful Applications (My managed projects) and Answer Enquiries");
            System.out.println("4. Approve Applicant Applications");
            System.out.println("");
            System.out.println("Type the number of the option you want to select or leave empty ('') to cancel.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new HDBOfficerProjectsView();
                case "2":
                    displayOfficerRegistrations();
                    return null;
                case "3":
                    return new HDBOfficeRespondView();
                case "4":
                    return new HDBOfficerAppApprovalView();
                default:
                    System.out.println("Invalid option.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private void displayOfficerRegistrations() {
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        // Get all projects where the officer has applied to register
        List<BTOProject> officerRegistrations = projectManager.getOfficerRegistrations(user.getId());

        if (officerRegistrations.isEmpty()) {
            System.out.println("You have not applied to register as a managing officer for any projects.");
        } else {
            System.out.println("Projects you have applied to register as a managing officer:");
            for (BTOProject project : officerRegistrations) {
                System.out.println("- Project Name: " + project.getName());
                System.out.println("  Project ID: " + project.getId());
                System.out.println("- Current status: " + project.getActiveOfficerRegistration(user.getId()).map(registration -> registration.getStatus().toString()));
                System.out.println("--------------------------------");
            }
        }
    }

}
