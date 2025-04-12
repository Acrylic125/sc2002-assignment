package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.*;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantApplyProjectView implements AuthenticatedView {
    private ViewContext ctx;
    private User user;

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canApply();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final Scanner scanner = ctx.getScanner();

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        List<BTOProject> activeProjects = projectManager.getActiveProjectsForUser(user.getId());
        if (!projectManager.getBookedApplicationsForUser(user.getId()).isEmpty()) {
            System.out.println(BashColors.format(
                    "You are already booked for the following projects. You may not apply for another project.",
                    BashColors.RED));
            activeProjects.forEach((project) -> {
                System.out.println(" - " + project.getName());
            });
            System.out.println();
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

        if (project.getActiveApplication(user.getId()).isPresent()) {
            System.out.println(BashColors.format(
                    "You have already applied for this project.",
                    BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }
        if (project.isManagedBy(user.getId())) {
            System.out.println(BashColors.format(
                    "You are managing this project. You may not apply for it.",
                    BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }

        final Optional<BTOProjectTypeID> typeOpt = showRequestProjectType(project);
        if (typeOpt.isEmpty()) {
            return null;
        }

        Utils.tryCatch(() -> {
            projectManager.requestApply(project.getId(), user.getId(), typeOpt.get());
        }).getErr().ifPresentOrElse((err) -> {
            System.out.println(BashColors.format("Failed to apply for project: ", BashColors.RED));
            System.out.println(err.getMessage());
        }, () -> {
            System.out.println(BashColors.format("Application submitted successfully.", BashColors.GREEN));
        });
        System.out.println("Type anything to continue.");
        scanner.nextLine();

        return null;
    }

    private Optional<BTOProject> showRequestProject() {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        while (true) {
            System.out.println(BashColors.format(
                    "Type in the project id you want to apply for, or leave empty ('') to cancel:", BashColors.BOLD));
            final String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println(
                        BashColors.format("Project not found, please type in a valid project id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            final BTOProject project = projectOpt.get();
            if (!project.isApplicationWindowOpen()) {
                System.out.println(BashColors.format(
                        "The application window for this project is closed. Please choose another project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            if (!project.isVisibleToPublic()) {
                System.out.println(BashColors.format(
                        "This project is not visible to the public, such projects cannot be applied to. Please choose another project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            if (user.getPermissions().canRegisterForProject()) {
                final Optional<HDBOfficerRegistration> reg = project.getActiveOfficerRegistration(user.getId());
                if (reg.isPresent()) {
                    HDBOfficerRegistration officerReg = reg.get();
                    if (officerReg.getStatus() == HDBOfficerRegistrationStatus.SUCCESSFUL) {
                        System.out.println(BashColors.format(
                                "You are a registered officer for this project. You may not apply for it.",
                                BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                        return Optional.empty();
                    }
                    if (officerReg.getStatus() == HDBOfficerRegistrationStatus.PENDING) {
                        System.out.println(BashColors.format(
                                "WARNING: You are currently registering to be an officer for this project. You may still be booked for this project but your officer registration cannot be accepted.",
                                BashColors.YELLOW));
                        System.out.println("Type 'y' to confirm, or anything else to cancel:");
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

    private Optional<BTOProjectTypeID> showRequestProjectType(BTOProject project) {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            final List<BTOProjectType> types = new ArrayList<>(project.getProjectTypes());

            System.out.println(BashColors.format("What type would you like to apply for?", BashColors.BOLD));
            System.out.println("Types (No. Units Available / Total No. Units / Price):");
            if (types.size() <= 0) {
                System.out.println(BashColors.format("  (No types available)", BashColors.LIGHT_GRAY));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return Optional.empty();
            } else {
                types.sort((a, b) -> a.getId().compareTo(b.getId()));

                int zeroSlotsLeftTypes = 0;
                for (BTOProjectType type : types) {
                    int quantityBooked = project.getBookedCountForType(type.getId());
                    String typeStr = "  " + type.getId().getName() + " " +
                            quantityBooked + " / " + type.getMaxQuantity()
                            + " / $" + Utils.formatMoney(type.getPrice());

                    final List<String> errs = new LinkedList<>();
                    Optional<String> verifyEligibilityErrOpt = project.verifyEligibilityToApply(user, type.getId());
                    if (verifyEligibilityErrOpt.isPresent()) {
                        errs.add(verifyEligibilityErrOpt.get());
                    }
                    if (quantityBooked >= type.getMaxQuantity()) {
                        errs.add("Fully booked");
                        zeroSlotsLeftTypes++;
                    }

                    if (!errs.isEmpty()) {
                        System.out.println(BashColors.format(typeStr, BashColors.RED));
                        for (String err : errs) {
                            System.out.println(BashColors.format("  - " + err, BashColors.RED));
                        }
                    } else {
                        System.out.println(typeStr);
                    }
                }

                if (zeroSlotsLeftTypes == types.size()) {
                    System.out.println(BashColors.format("All types are fully booked. Please choose another project.",
                            BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    return Optional.empty();
                }

                System.out.println("Type the type (e.g. '" + types.getFirst().getId().getName()
                        + "') or leave empty ('') to cancel:");
                final String typeId = scanner.nextLine().trim().toLowerCase();
                if (typeId.isEmpty()) {
                    return Optional.empty();
                }

                final Optional<BTOProjectType> typeOpt = types.stream()
                        .filter((type) -> type.getId().getName().toLowerCase().equals(typeId))
                        .findFirst();
                if (typeOpt.isEmpty()) {
                    System.out.println(
                            BashColors.format("Type not found, please type in a valid type id.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }

                BTOProjectType type = typeOpt.get();
                Optional<String> verifyEligibilityErrOpt = project.verifyEligibilityToApply(user, type.getId());
                if (verifyEligibilityErrOpt.isPresent()) {
                    System.out
                            .println(BashColors.format("You are not eligible to apply for this type.", BashColors.RED));
                    System.out.println(BashColors.format(verifyEligibilityErrOpt.get(), BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }

                return Optional.of(type.getId());
            }
        }
    }

}
