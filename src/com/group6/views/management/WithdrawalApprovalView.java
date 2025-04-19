package com.group6.views.management;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOApplicationWithdrawal;
import com.group6.btoproject.BTOApplicationWithdrawalStatus;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * View for the management to approve or reject withdrawal requests.
 */
public class WithdrawalApprovalView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private final BTOProject project;

    private final List<BTOApplicationWithdrawal> applications;
    private ViewContext ctx;
    private int page = 1;

    /**
     * Constructor for the WithdrawalApprovalView.
     *
     * @param project the project to be managed.
     */
    public WithdrawalApprovalView(BTOProject project) {
        this.project = project;
        this.applications = project.getWithdrawals();
    }

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = applications.size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    /**
     * @param page The page number to set.
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
     * Stringify the application status.
     *
     * @param status the application status
     * @return the stringified application status
     */
    private String stringifyApplicationStatus(BTOApplicationWithdrawalStatus status) {
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
     * @param user The check user.
     * @return true if the user is authorized to approve withdrawals, false otherwise.
     */
    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canApproveWithdrawal();
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
        return showOptions();
    }

    /**
     * Show the withdrawals that the user can approve or reject.
     */
    private void showWithdrawals() {
        final UserManager userManager = ctx.getBtoSystem().getUserManager();

        System.out.println(BashColors.format(
                "Withdrawals for " + project.getName() + ", " + project.getNeighbourhood(), BashColors.BOLD));
        System.out.println("Withdrawal ID | Application ID | Applicant | Type | Status");
        if (applications.isEmpty()) {
            System.out.println(BashColors.format("(No Withdrawals)", BashColors.LIGHT_GRAY));
            System.out.println();
            return;
        }

        final int lastIndex = Math.min(page * PAGE_SIZE, applications.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            BTOApplicationWithdrawal withdrawal = applications.get(i);
            Optional<BTOApplication> applicationOpt = project.getApplication(withdrawal.getApplicationId());
            String statusStr = stringifyApplicationStatus(withdrawal.getStatus());
            if (applicationOpt.isEmpty()) {
                System.out.println(BashColors.format(
                        withdrawal.getId() + " | " + withdrawal.getApplicationId() + " | "
                                + BashColors.format("(Unknown)", BashColors.LIGHT_GRAY) + " | "
                                + statusStr,
                        BashColors.RED));
                continue;
            }

            BTOApplication application = applicationOpt.get();
            Optional<User> userOpt = userManager.getUser(application.getApplicantUserId());
            String userStr = userOpt.map(User::getName).orElse(BashColors.format("(Unknown)", BashColors.LIGHT_GRAY));
            System.out.println(
                    withdrawal.getId() + " | "
                            + application.getId() + " | "
                            + userStr + " ("
                            + BashColors.format(application.getApplicantUserId(), BashColors.LIGHT_GRAY) + ")" + " | "
                            + application.getTypeId().getName() + " | "
                            + statusStr);
        }
    }

    /**
     * Show the options for the user to approve or reject the withdrawals.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showWithdrawals();
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
        Optional<BTOApplicationWithdrawal> withdrawalOpt = requestWithdrawal();
        if (withdrawalOpt.isEmpty()) {
            return;
        }

        BTOApplicationWithdrawal withdrawal = withdrawalOpt.get();
        Optional<BTOApplicationWithdrawalStatus> statusOpt = requestWithdrawalStatus(withdrawal);
        if (statusOpt.isEmpty()) {
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        final Scanner scanner = ctx.getScanner();

        Optional<Throwable> transitionErrOpt = Utils.tryCatch(() -> {
            projectManager.transitionWithdrawApplicationStatus(
                    project.getId(), withdrawal.getApplicationId(),
                    statusOpt.get());
        }).getErr();
        if (transitionErrOpt.isPresent()) {
            System.out.println(BashColors.format("Failed to update withdrawal status.", BashColors.RED));
            System.out.println(BashColors.format(
                    transitionErrOpt.get().getMessage(), BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }
        System.out.println(BashColors.format("Successfully updated withdrawal status.", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Request the withdrawal to be modified.
     *
     * @return the withdrawal to be modified
     */
    private Optional<BTOApplicationWithdrawal> requestWithdrawal() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter ID of Withdrawal or leave empty ('') to cancel: ", BashColors.BOLD));
            String appId = scanner.nextLine().trim();
            if (appId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOApplicationWithdrawal> withdrawalOpt = project.getWithdrawal(appId);
            if (withdrawalOpt.isEmpty()) {
                System.out.println(BashColors.format("Withdrawal not found.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return withdrawalOpt;
        }
    }

    /**
     * Request the status to be modified.
     *
     * @param withdrawal the withdrawal to be modified
     * @return the status to be modified
     */
    private Optional<BTOApplicationWithdrawalStatus> requestWithdrawalStatus(BTOApplicationWithdrawal withdrawal) {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        final BTOApplicationWithdrawalStatus[] allStatuses = BTOApplicationWithdrawalStatus.values();
        final Set<BTOApplicationWithdrawalStatus> validStatuses = projectManager
                .getValidWithdrawalStateTransitions(withdrawal.getStatus());

        while (true) {
            System.out.println(BashColors.format("What status to change to?", BashColors.BOLD));
            System.out.println("Available statuses:");
            for (BTOApplicationWithdrawalStatus status : allStatuses) {
                boolean isStatusTransitionValid = validStatuses.contains(status);

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

            TryCatchResult<BTOApplicationWithdrawalStatus, Throwable> statusRes = Utils
                    .tryCatch(() -> BTOApplicationWithdrawalStatus.valueOf(_status));
            if (statusRes.getErr().isPresent()) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            BTOApplicationWithdrawalStatus status = statusRes.getData().get();
            if (!validStatuses.contains(status)) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(status);
        }
    }

}
