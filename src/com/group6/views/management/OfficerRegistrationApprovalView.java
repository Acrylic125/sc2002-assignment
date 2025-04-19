package com.group6.views.management;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.HDBOfficerRegistration;
import com.group6.btoproject.HDBOfficerRegistrationStatus;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

/**
 * View for the management to approve or reject officer registrations.
 */
public class OfficerRegistrationApprovalView implements AuthenticatedView, PaginatedView {

    private static final int PAGE_SIZE = 3;

    private final BTOProject project;
    private final List<HDBOfficerRegistration> registrations;

    private ViewContext ctx;
    private User user;
    private int page = 1;

    /**
     * Constructor.
     *
     * @param project the project to be managed
     */
    public OfficerRegistrationApprovalView(BTOProject project) {
        this.project = project;
        this.registrations = project.getHdbOfficerRegistrations();
    }

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = registrations.size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    /**
     * @param page the page number to set
     */
    @Override
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the current page
     */
    @Override
    public int getPage() {
        return page;
    }

    /**
     * Stringify the officer registration status.
     * @param status the officer registration status
     * @return the stringified officer registration status
     */
    private String stringifyRegistrationStatus(HDBOfficerRegistrationStatus status) {
        switch (status) {
            case PENDING:
                return BashColors.format("Pending", BashColors.YELLOW);
            case SUCCESSFUL:
                return BashColors.format("Successful", BashColors.GREEN);
            case UNSUCCESSFUL:
                return BashColors.format("Unsuccessful", BashColors.RED);
        }
        return BashColors.format("(Unknown)", BashColors.LIGHT_GRAY);
    }

    /**
     * View renderer.
     *
     * @param ctx  view context
     * @param user authenticated user
     * @return next view
     */
    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        return showOptions();
    }

    /**
     * Show the officer registrations.
     */
    private void showRegistrations() {
        final UserManager userManager = ctx.getBtoSystem().getUserManager();
        System.out.println(BashColors.format(
                "Officer Registrations for " + project.getName() + ", " + project.getNeighbourhood(), BashColors.BOLD));
        System.out.println("Registration ID | Registrant | Status");
        if (registrations.isEmpty()) {
            System.out.println(BashColors.format("(No Registrations)", BashColors.LIGHT_GRAY));
            System.out.println();
            return;
        }

        final int lastIndex = Math.min(page * PAGE_SIZE, registrations.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            HDBOfficerRegistration registration = registrations.get(i);
            Optional<User> userOpt = userManager.getUser(registration.getOfficerUserId());
            String userStr = userOpt.map(User::getName).orElse(BashColors.format("(Unknown)", BashColors.LIGHT_GRAY));
            String statusStr = stringifyRegistrationStatus(registration.getStatus());
            System.out.println(registration.getId() + " | " + userStr + " ("
                    + BashColors.format(registration.getOfficerUserId(), BashColors.LIGHT_GRAY) + ")" + " | "
                    + statusStr);
        }
    }

    /**
     * Show the options for the officer registration approval view.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showRegistrations();
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

    /**
     * Show the request status change view.
     */
    private void showRequestStatusChange() {
        Optional<HDBOfficerRegistration> applicationOpt = requestApplication();
        if (applicationOpt.isEmpty()) {
            return;
        }

        HDBOfficerRegistration application = applicationOpt.get();
        Optional<HDBOfficerRegistrationStatus> statusOpt = requestApplicationStatus(application);
        if (statusOpt.isEmpty()) {
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        final Scanner scanner = ctx.getScanner();

        final Optional<User> applicantOpt = ctx.getBtoSystem().getUserManager().getUser(application.getOfficerUserId());
        if (applicantOpt.isEmpty()) {
            System.out.println(BashColors.format("Applicant not found.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        Optional<Throwable> transitionErrOpt = Utils.tryCatch(() -> {
            projectManager.transitionOfficerRegistrationStatus(project.getId(), application.getOfficerUserId(),
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

        System.out.println(BashColors.format("Successfully updated registration status.", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Request the application to modify.
     *
     * @return the application to modify
     */
    private Optional<HDBOfficerRegistration> requestApplication() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter ID of Registration or leave empty ('') to cancel: ", BashColors.BOLD));
            String appId = scanner.nextLine().trim();
            if (appId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<HDBOfficerRegistration> applicationOpt = project.getOfficerRegistration(appId);
            if (applicationOpt.isEmpty()) {
                System.out.println(BashColors.format("Regidtration not found.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return applicationOpt;
        }
    }

    /**
     * Request the application status to modify.
     *
     * @param application the application to modify
     * @return the application status to modify
     */
    private Optional<HDBOfficerRegistrationStatus> requestApplicationStatus(HDBOfficerRegistration application) {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        final HDBOfficerRegistrationStatus[] allStatuses = HDBOfficerRegistrationStatus.values();
        final Set<HDBOfficerRegistrationStatus> validStatuses = projectManager
                .getValidOfficerRegistrationStateTransitions(application.getStatus());

        final List<String> approvalIssues = new LinkedList<>();
        if (project.getManagingOfficerRegistrations().size() >= project.getOfficerLimit()) {
            approvalIssues.add("Officer limit reached");
        }
        final Date startDate = project.getApplicationOpenDate();
        final Date endDate = project.getApplicationCloseDate();
        final List<BTOProject> managedByOfficer = projectManager
                .getOfficerManagingProjects(application.getOfficerUserId());
        for (BTOProject managedProject : managedByOfficer) {
            Date _startDate = managedProject.getApplicationOpenDate();
            Date _endDate = managedProject.getApplicationCloseDate();
            if (Utils.isDateRangeIntersecting(
                    startDate, endDate,
                    _startDate, _endDate)) {
                approvalIssues.add("Officer already managing another project in the same time period");
            }
        }

        while (true) {
            System.out.println(BashColors.format("What status to change to?", BashColors.BOLD));
            System.out.println("Available statuses:");
            for (HDBOfficerRegistrationStatus status : allStatuses) {
                boolean isStatusTransitionValid = validStatuses.contains(status);

                if ((status == HDBOfficerRegistrationStatus.SUCCESSFUL)) {
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

            TryCatchResult<HDBOfficerRegistrationStatus, Throwable> statusRes = Utils
                    .tryCatch(() -> HDBOfficerRegistrationStatus.valueOf(_status));
            if (statusRes.getErr().isPresent()) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            HDBOfficerRegistrationStatus status = statusRes.getData().get();
            if (!validStatuses.contains(status)
                    || (status == HDBOfficerRegistrationStatus.SUCCESSFUL
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
