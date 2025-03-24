package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.*;
import com.group6.users.User;
import com.group6.utils.Utils;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantApplicationWithdrawalView implements View {

    private ViewContext ctx;
    private User user;

    @Override
    public View render(ViewContext ctx) {
        final Optional<User> userOpt = ctx.getUser();
        final Scanner scanner = ctx.getScanner();
        if (userOpt.isEmpty()) {
            System.out.println("You are not logged in. Please sign in.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.ctx = ctx;
        this.user = userOpt.get();

        List<BTOProjectManager.BTOFullApplication> applications = new ArrayList<>(
                projectManager.getAllApplicationsForUser(user.getId()));
        showRequestWithdrawal(applications);
        return null;
    }

    private void showRequestWithdrawal(List<BTOProjectManager.BTOFullApplication> fullApplications) {
        final Scanner scanner = ctx.getScanner();
        if (fullApplications.isEmpty()) {
            System.out.println("You do not have any active applications to withdraw from.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        BTOProjectManager.BTOFullApplication fullApplication;
        // SHOULD NOT HAPPEN BUT IN CASE!
        if (fullApplications.size() > 1) {
            System.out
                    .println("WARNING: It seems you have multiple active applications. This should not have happened.");
            System.out.println("Please select the application you want to withdraw from.");
            System.out.println("Project ID | Project Name | Application Status | Withdrawal Status");
            fullApplications.forEach((_fullApplication) -> {
                final BTOProject project = _fullApplication.getProject();
                final BTOApplication application = _fullApplication.getApplication();
                final Optional<BTOApplicationWithdrawal> withdrawalOpt = _fullApplication.getWithdrawal();
                String withdrawalStatus = withdrawalOpt.isPresent() ? withdrawalOpt.get().getStatus().toString()
                        : "N/A";
                System.out.println(project.getId() + " | " + project.getName() + " | " + application.getStatus() + " | "
                        + withdrawalStatus);
            });

            while (true) {
                System.out.println("Type the project ID you want to withdraw from or leave empty ('') to cancel:");
                String projectId = scanner.nextLine().trim();
                if (projectId.isEmpty()) {
                    return;
                }
                final Optional<BTOProjectManager.BTOFullApplication> fullApplicationOpt = fullApplications.stream()
                        .filter((_fullApplication) -> _fullApplication.getProject().getId().equals(projectId))
                        .findFirst();
                if (fullApplicationOpt.isEmpty()) {
                    System.out.println("Project not found. Please type in a valid project ID.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }
                fullApplication = fullApplicationOpt.get();
                break;
            }
        } else {
            fullApplication = fullApplications.getFirst();
        }

        final Optional<BTOApplicationWithdrawal> withdrawalOpt = fullApplication.getWithdrawal();
        if (withdrawalOpt.isPresent()) {
            final BTOApplicationWithdrawal withdrawal = withdrawalOpt.get();
            if (withdrawal.getStatus() == BTOApplicationWithdrawalStatus.SUCCESSFUL) {
                System.out.println("You have already withdrawn from this project.");
            } else {
                System.out.println("You have already requesting to withdraw from this project.");
            }
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        BTOProject project = fullApplication.getProject();
        System.out.println("Are you sure you want to withdraw from the project " + project.getName() + "?");
        System.out.println("Type 'y' to confirm or type anything else to cancel:");
        String option = scanner.nextLine().trim();
        if (option.equals("y")) {
            Utils.tryCatch(() -> {
                project.requestWithdrawApplication(fullApplication.getApplication().getId());
            }).getErr().ifPresentOrElse((err) -> {
                System.out.println("Failed to withdraw from the project: ");
                System.out.println(err.getMessage());
            }, () -> {
                System.out.println("Successfully requested to withdraw from the project.");
            });
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }
}
