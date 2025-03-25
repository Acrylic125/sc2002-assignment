package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.HDBOfficerRegistration;
import com.group6.btoproject.HDBOfficerRegistrationStatus;
import com.group6.users.Applicant;
import com.group6.users.HDBManager;
import com.group6.users.HDBOfficer;
import com.group6.users.User;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantApplyProjectView implements AuthenticatedView {
    private ViewContext ctx;
    private User user;

    @Override
    public boolean isAuthorized(User user) {
        return user instanceof Applicant;
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final Scanner scanner = ctx.getScanner();

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        List<BTOProject> activeProjects = projectManager.getActiveProjectsForUser(user.getId());
        if (!activeProjects.isEmpty()) {
            System.out.println("You already applied for the following project(s):");
            activeProjects.forEach((project) -> {
                System.out.println(" - " + project.getName());
            });
            System.out.println("");
            System.out.println("Consider withdrawing your application before applying for a new project.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }

        final Optional<BTOProject> projectOpt = showRequestProject();
        if (projectOpt.isEmpty()) {
            return null;
        }
        final BTOProject project = projectOpt.get();

        final Optional<String> typeOpt = showRequestProjectType(project);
        if (typeOpt.isEmpty()) {
            return null;
        }

        Utils.tryCatch(() -> {
            project.requestApply(user.getId(), typeOpt.get());
        }).getErr().ifPresentOrElse((err) -> {
            System.out.println("Failed to apply for project: ");
            System.out.println(err.getMessage());
        }, () -> {
            System.out.println("Application submitted successfully.");
        });
        System.out.println("Type anything to continue.");
        scanner.nextLine();

        return null;
    }

    private Optional<BTOProject> showRequestProject() {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        while (true) {
            System.out.println("Type in the project id you want to apply for, or leave empty ('') to cancel:");
            final String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println("Project not found, please type in a valid project id.");
                continue;
            }

            final BTOProject project = projectOpt.get();
            if (!project.isApplicationWindowOpen()) {
                System.out.println("The application window for this project is closed. Please choose another project.");
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            if (user instanceof HDBOfficer) {
                final Optional<HDBOfficerRegistration> reg = project.getActiveOfficerRegistration(user.getId());
                if (reg.isPresent()) {
                    HDBOfficerRegistration officerReg = reg.get();
                    if (officerReg.getStatus() == HDBOfficerRegistrationStatus.SUCCESSFUL) {
                        System.out.println("You are a registered officer for this project. You may not apply for it.");
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                        return Optional.empty();
                    } else {
                        System.out.println(
                                "WARNING: You are currently registering to be an officer for this project. You may still apply for this project but your officer registration cannot be accepted.");
                        System.out.println("Type 'y' to continue, or anything else to cancel:");
                        final String option = scanner.nextLine().trim();
                        if (!option.equals("y")) {
                            return Optional.empty();
                        }
                    }
                }
            }
            return projectOpt;
        }
    }

    private Optional<String> showRequestProjectType(BTOProject project) {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            final List<BTOProjectType> types = new ArrayList<>(project.getProjectTypes());

            System.out.println("What type would you like to apply for?");
            System.out.println("Types (No. Units Available / Total No. Units / Price):");
            if (types.size() <= 0) {
                System.out.println("  (No types available)");
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return Optional.empty();
            } else {
                types.sort((a, b) -> a.getId().compareTo(b.getId()));

                int zeroSlotsLeftTypes = 0;
                for (BTOProjectType type : types) {
                    int quantityBooked = project.getBookedCountForType(type.getId());
                    System.out.println(
                            "  " + type.getId() + " " +
                                    quantityBooked + " / " + type.getMaxQuantity()
                                    + " / $" + Utils.formatMoney(type.getPrice()));
                    if (quantityBooked >= type.getMaxQuantity()) {
                        zeroSlotsLeftTypes++;
                    }
                }

                if (zeroSlotsLeftTypes == types.size()) {
                    System.out.println("All types are fully booked. Please choose another project.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    return Optional.empty();
                }

                System.out
                        .println("Type the type (e.g. '" + types.get(0).getId() + "') or leave empty ('') to cancel:");
                final String typeId = scanner.nextLine().trim();
                if (typeId.isEmpty()) {
                    return Optional.empty();
                }

                final Optional<BTOProjectType> typeOpt = types.stream()
                        .filter((type) -> type.getId().equals(typeId))
                        .findFirst();
                if (typeOpt.isEmpty()) {
                    System.out.println("Type not found. Please type in a valid type id.");
                    continue;
                }
                return Optional.of(typeId);
            }
        }
    }

}
