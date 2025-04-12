package com.group6.views.applicant;

import java.util.ArrayList;
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

public class ApplicantApplicationWithdrawalView implements AuthenticatedView {

    private ViewContext ctx;

    @Override
    public View render(ViewContext ctx, User user) {
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.ctx = ctx;

        List<BTOProjectManager.BTOFullApplication> applications = new ArrayList<>(
                projectManager.getAllActiveApplicationsForUser(user.getId()));
        showRequestWithdrawal(applications);
        return null;
    }

    private void showRequestWithdrawal(List<BTOProjectManager.BTOFullApplication> fullApplications) {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        if (fullApplications.isEmpty()) {
            System.out.println(
                    BashColors.format("You do not have any active applications to withdraw from.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        BTOProjectManager.BTOFullApplication fullApplication;
        System.out.println(
                BashColors.format("Enter the application you want to withdraw from.", BashColors.BOLD));
        System.out.println("Application ID | Project Name | Application Status | Withdrawal Status");
        fullApplications.forEach((_fullApplication) -> {
            final BTOProject project = _fullApplication.getProject();
            final BTOApplication application = _fullApplication.getApplication();
            final Optional<BTOApplicationWithdrawal> withdrawalOpt = _fullApplication.getWithdrawal();
            String withdrawalStatus = withdrawalOpt.isPresent() ? withdrawalOpt.get().getStatus().toString()
                    : "N/A";
            System.out.println(
                    application.getId() + " | " + project.getName() + " | " + application.getStatus() + " | "
                            + withdrawalStatus);
        });

        while (true) {
            System.out.println("Type the application ID you want to withdraw from or leave empty ('') to cancel:");
            String applicationId = scanner.nextLine().trim();
            if (applicationId.isEmpty()) {
                return;
            }
            final Optional<BTOProjectManager.BTOFullApplication> fullApplicationOpt = fullApplications.stream()
                    .filter((_fullApplication) -> _fullApplication.getApplication().getId().equals(applicationId))
                    .findFirst();
            if (fullApplicationOpt.isEmpty()) {
                System.out.println(
                        BashColors.format("Application not found. Please type in a valid application ID.",
                                BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            fullApplication = fullApplicationOpt.get();
            break;
        }

        final Optional<BTOApplicationWithdrawal> withdrawalOpt = fullApplication.getWithdrawal();
        if (withdrawalOpt.isPresent()) {
            final BTOApplicationWithdrawal withdrawal = withdrawalOpt.get();
            if (withdrawal.getStatus() == BTOApplicationWithdrawalStatus.SUCCESSFUL) {
                System.out.println(BashColors.format("You have already withdrawn from this project.", BashColors.RED));
            } else {
                System.out.println(BashColors.format("You have already requesting to withdraw from this project.",
                        BashColors.RED));
            }
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        BTOProject project = fullApplication.getProject();
        System.out.println(BashColors.format(
                "Are you sure you want to withdraw from the project " + project.getName() + "?", BashColors.BOLD));
        System.out.println("Type 'y' to confirm or type anything else to cancel:");
        String option = scanner.nextLine().trim();
        if (option.equals("y")) {
            Utils.tryCatch(() -> {
                projectManager.requestWithdrawApplication(project.getId(), fullApplication.getApplication().getId());
            }).getErr().ifPresentOrElse((err) -> {
                System.out.println(BashColors.format("Failed to withdraw from the project: ", BashColors.RED));
                System.out.println(BashColors.format(err.getMessage(), BashColors.RED));
            }, () -> {
                System.out.println(
                        BashColors.format("Successfully requested to withdraw from the project.", BashColors.RED));
            });
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }
}
