package com.group6.views.management;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.HDBOfficerRegistration;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.*;

public class OfficerRegistrationView implements AuthenticatedView {

    private ViewContext ctx;
    private User user;

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canRegisterForProject();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        // check if user is already registered for any project
        final List<BTOProject> managedByOfficer = projectManager.getOfficerManagingProjects(user.getId());
        for (BTOProject managedProject : managedByOfficer) {
            if (managedProject.isApplicationWindowOpen()) {
                System.out.println(BashColors.format(
                        "You are already registered to manage " + managedProject.getName() + ".",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return null;
            }
        }

        return showRequestRegistration();
    }

    private View showRequestRegistration() {
        final Scanner scanner = ctx.getScanner();

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter the Project ID of the project that you want to register to manage or leave empty ('') to cancel: ",
                    BashColors.BOLD));
            final String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return null;
            }

            // check if projectId is valid:
            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println(BashColors.format("Project not found.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            final BTOProject project = projectOpt.get();
            if (!project.isApplicationWindowOpen()) {
                System.out.println(
                        BashColors.format("The registration window for this project is closed.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            // check if user already registered to manage this project:
            Optional<HDBOfficerRegistration> registrationOpt = project.getActiveOfficerRegistration(user.getId());
            if (registrationOpt.isPresent()) {
                System.out.println(BashColors.format(
                        "You have already registered to manage this project so you may not register again.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return null;
            }

            // check if user is already booked into this project:
            if (project.isApplicantBooked(user.getId())) {
                System.out.println(BashColors.format(
                        "You are already booked into this project so you may not register to manage it.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return null;
            }

            // Warn the user if the user is currently applying for the project:
            if (user.getPermissions().canApply()) {
                Optional<BTOApplication> applicationOpt = project.getActiveApplication(user.getId());
                if (applicationOpt.isPresent()) {
                    System.out.println(BashColors.format(
                            "WARNING: You are currently applying to book for this project. You may still be registered to manage this project but your application cannot be accepted.",
                            BashColors.YELLOW));
                    System.out.println("Type 'y' to confirm, or anything else to cancel:");
                    final String option = scanner.nextLine().trim();
                    if (!option.equals("y")) {
                        return null;
                    }
                }
            }

            final int officerLimit = project.getOfficerLimit();
            if (project.getManagingOfficerRegistrations().size() >= officerLimit) {
                System.out.println(BashColors.format(
                        "The project has reached the maximum number of officers. You may not register to manage this project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
            }

            Utils.tryCatch(() -> {
                projectManager.requestRegisterOfficer(projectId, user.getId());
            }).getErr().ifPresentOrElse((err) -> {
                System.out.println(BashColors.format("Failed to register for project: ", BashColors.RED));
                System.out.println(err.getMessage());
            }, () -> {
                System.out.println(BashColors.format("Registration submitted successfully.", BashColors.GREEN));
            });
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }
    }

}
