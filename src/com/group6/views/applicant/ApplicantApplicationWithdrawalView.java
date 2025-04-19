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

/**
 * View for the applicant to withdraw from an application.
 */
public class ApplicantApplicationWithdrawalView implements AuthenticatedView {

    private ViewContext ctx;

    /**
     * Stringify the application status.
     *
     * @param status the application status
     * @return the stringified application status
     */
    private String stringifyApplicationStatus(BTOApplicationStatus status) {
        switch (status) {
            case PENDING:
                return BashColors.format("Pending", BashColors.YELLOW);
            case BOOKED:
                return BashColors.format("Booked", BashColors.BLUE);
            case SUCCESSFUL:
                return BashColors.format("Successful", BashColors.GREEN);
            case UNSUCCESSFUL:
                return BashColors.format("Unsuccessful", BashColors.RED);
        }
        return BashColors.format("(Unknown)", BashColors.LIGHT_GRAY);
    }

    /**
     * View renderer.
     * @return next view
     */
    @Override
    public View render(ViewContext ctx, User user) {
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        this.ctx = ctx;

        List<BTOProjectManager.BTOFullApplication> applications = new ArrayList<>(
                projectManager.getAllActiveApplicationsForUser(user.getId()));
        showRequestWithdrawal(applications);
        return null;
    }

    /**
     * Show the request withdrawal view.
     *
     * @param fullApplications the list of full applications
     */
    private void showRequestWithdrawal(List<BTOProjectManager.BTOFullApplication> fullApplications) {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
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
        System.out.println("Application ID | Project Name | Application Status");
        fullApplications.forEach((_fullApplication) -> {
            final BTOProject project = _fullApplication.getProject();
            final BTOApplication application = _fullApplication.getApplication();
            System.out.println(application.getId() + " | " + project.getName() + " | " +
                    stringifyApplicationStatus(application.getStatus()));
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

        final List<BTOApplicationWithdrawal> withdrawals = fullApplication.getWithdrawals();
        if (!withdrawals.isEmpty()) {
            if (withdrawals.stream()
                    .anyMatch((withdrawal) -> withdrawal.getStatus() == BTOApplicationWithdrawalStatus.SUCCESSFUL)) {
                System.out.println(BashColors.format("You have already withdrawn from this project.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return;
            }
            if (withdrawals.stream()
                    .anyMatch((withdrawal) -> withdrawal.getStatus() == BTOApplicationWithdrawalStatus.PENDING)) {
                System.out.println(BashColors.format("You have already requested to withdraw from this project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return;
            }
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
                        BashColors.format("Successfully requested to withdraw from the project.", BashColors.GREEN));
            });
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }
}
