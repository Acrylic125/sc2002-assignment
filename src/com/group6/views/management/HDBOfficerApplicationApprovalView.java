package com.group6.views.management;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOApplicationStatus;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.ViewContext;
import com.group6.views.View;

public class HDBOfficerApplicationApprovalView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private final BTOProject project;

    private List<BTOApplication> applications;
    private ViewContext ctx;
    private int page = 1;

    public HDBOfficerApplicationApprovalView(BTOProject project) {
        this.project = project;
        this.applications = project.getApplications();
    }

    @Override
    public int getLastPage() {
        int size = applications.size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return page;
    }

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

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canApproveApplications();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        return showOptions();
    }

    private void showApplications() {
        final UserManager userManager = ctx.getBtoSystem().getUserManager();
        System.out.println(BashColors.format(
                "Applications for " + project.getName() + ", " + project.getNeighbourhood(), BashColors.BOLD));
        System.out.println("Application ID | Applicant | Type | Status");
        if (applications.isEmpty()) {
            System.out.println(BashColors.format("(No Applications)", BashColors.LIGHT_GRAY));
            System.out.println();
            return;
        }

        final int lastIndex = Math.min(page * PAGE_SIZE, applications.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            BTOApplication application = applications.get(i);
            Optional<User> userOpt = userManager.getUser(application.getApplicantUserId());
            String userStr = userOpt.map(User::getName).orElse(BashColors.format("(Unknown)", BashColors.LIGHT_GRAY));
            String statusStr = stringifyApplicationStatus(application.getStatus());
            System.out.println(application.getId() + " | " + userStr + " ("
                    + BashColors.format(application.getApplicantUserId(), BashColors.LIGHT_GRAY) + ")" + " | "
                    + application.getTypeId().getName() + " | "
                    + statusStr);
        }
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showApplications();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 's' to modify status, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "s":
                    showRequestStatusChange();
                    break;
                case "n":
                    this.requestNextPage(scanner);
                    break;
                case "p":
                    this.requestPrevPage(scanner);
                    break;
                case "page":
                    this.requestPage(scanner);
                    break;
                case "":
                    return null;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private void showRequestStatusChange() {
        Optional<BTOApplication> applicationOpt = requestApplication();
        if (applicationOpt.isEmpty()) {
            return;
        }

        BTOApplication application = applicationOpt.get();
        Optional<BTOApplicationStatus> statusOpt = requestApplicationStatus(application);
        if (statusOpt.isEmpty()) {
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        final Scanner scanner = ctx.getScanner();

        final Optional<User> applicantOpt = ctx.getBtoSystem().getUserManager().getUser(application.getApplicantUserId());
        if (applicantOpt.isEmpty()) {
            System.out.println(BashColors.format("Applicant not found.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }
        final User applicant = applicantOpt.get();

        Optional<Throwable> transitionErrOpt = Utils.tryCatch(() -> {
            projectManager.transitionApplicationStatus(project.getId(), application.getId(),
                    statusOpt.get());
        }).getErr();
        if (transitionErrOpt.isPresent()) {
            System.out.println(BashColors.format("Failed to update application status.", BashColors.RED));
            System.out.println(BashColors.format(
                    transitionErrOpt.get().getMessage(), BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        System.out.println(BashColors.format("Successfully updated application status.", BashColors.GREEN));

        if (statusOpt.get().equals(BTOApplicationStatus.BOOKED)) {
            Optional<Throwable> receiptErrOpt = Utils.tryCatch(() -> {
                projectManager.generateBookingReceipt(project.getId(), application.getId(), applicant);
            }).getErr();

            if (receiptErrOpt.isPresent()) {
                System.out.println(BashColors.format(
                        "Failed to generate receipt. The application status will remain as BOOKED.", BashColors.RED));
                System.out.println(BashColors.format(
                        receiptErrOpt.get().getMessage(), BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                return;
            }
            System.out.println(BashColors.format("Generated Receipt and sent to applicant.", BashColors.GREEN));
        }
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private Optional<BTOApplication> requestApplication() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter ID of Application or leave empty ('') to cancel: ", BashColors.BOLD));
            String appId = scanner.nextLine().trim();
            if (appId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOApplication> applicationOpt = project.getApplication(appId);
            if (applicationOpt.isEmpty()) {
                System.out.println(BashColors.format("Application not found.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return applicationOpt;
        }
    }

    private Optional<BTOApplicationStatus> requestApplicationStatus(BTOApplication application) {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        final BTOApplicationStatus[] allStatuses = BTOApplicationStatus.values();
        final Set<BTOApplicationStatus> validStatuses = projectManager
                .getValidApplicationStateTransitions(application.getStatus());
        final Optional<BTOProjectType> projectTypeOpt = project.getProjectType(application.getTypeId());
        if (projectTypeOpt.isEmpty()) {
            System.out.println(BashColors.format("Project type not found.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return Optional.empty();
        }
        final BTOProjectType projectType = projectTypeOpt.get();

        final List<String> approvalIssues = new LinkedList<>();
        if (!project.isApplicationWindowOpen()) {
            approvalIssues.add("Application window is closed");
        }
        if (project.getBookedCountForType(projectType.getId()) >= projectType.getMaxQuantity()) {
            approvalIssues.add("Quantity limit for type reached");
        }

        while (true) {
            System.out.println(BashColors.format("What status to change to?", BashColors.BOLD));
            System.out.println("Available statuses:");
            for (BTOApplicationStatus status : allStatuses) {
                boolean isStatusTransitionValid = validStatuses.contains(status);

                if ((status == BTOApplicationStatus.SUCCESSFUL || status == BTOApplicationStatus.BOOKED)) {
                    if (!approvalIssues.isEmpty()) {
                        String reason = Utils.joinStringDelimiter(approvalIssues, ", ", " and ");
                        System.out.println("  "
                                + BashColors.format(status.toString() + " (" + reason + ")",
                                        BashColors.RED));
                        continue;
                    }
                }
                if (!isStatusTransitionValid) {
                    System.out.println("  " + BashColors.format(status.toString(), BashColors.RED));
                    continue;
                }
                System.out.println("  " + status.toString());
            }

            System.out.println("Enter status (e.g. PENDING, SUCCESSFUL) to modify to or leave empty ('') to cancel: ");
            String _status = scanner.nextLine().trim().toUpperCase();
            if (_status.isEmpty()) {
                return Optional.empty();
            }

            TryCatchResult<BTOApplicationStatus, Throwable> statusRes = Utils
                    .tryCatch(() -> BTOApplicationStatus.valueOf(_status));
            if (statusRes.getErr().isPresent()) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            BTOApplicationStatus status = statusRes.getData().get();
            if (!validStatuses.contains(status)
                    || ((status == BTOApplicationStatus.SUCCESSFUL || status == BTOApplicationStatus.BOOKED)
                            && !approvalIssues.isEmpty())) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(status);
        }
    }

}
