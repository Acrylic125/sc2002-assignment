package com.group6.views.applicant;

import java.util.*;

import com.group6.btoproject.*;
import com.group6.btoproject.BTOProjectManager.BTOFullApplication;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

/**
 * View for the applicant to view their applications.
 */
public class ApplicantViewMyApplicationsView implements PaginatedView, AuthenticatedView {
    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;
    private List<BTOFullApplication> applications = new ArrayList<>();
    private List<BTOFullApplication> filteredApplications = new ArrayList<>();

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = filteredApplications.size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    /**
     * Set page
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
     * Stringify the withdrawal status.
     * @return the stringified withdrawal status
     */
    private String stringifyWithdrawalStatus(BTOApplicationWithdrawalStatus status) {
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

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        this.applications = new ArrayList<>(
                projectManager.getAllApplicationsForUser(user.getId()));
        this.filteredApplications = ctx.getViewApplicationFilters().applyFilters(this.applications, user);

        return showOptions();
    }

    /**
     * Show the applications.
     */
    private void showApplications() {
        final List<BTOFullApplication> applications = this.filteredApplications;
        System.out.println(BashColors.format("My Applications", BashColors.BOLD));
        if (applications.isEmpty()) {
            System.out.println(BashColors.format("(No Projects Applied/Found)", BashColors.LIGHT_GRAY));
            System.out.println();
            return;
        }
        final UserManager userManager = ctx.getBtoSystem().getUserManager();

        final int firstIndex = (page - 1) * PAGE_SIZE;
        final int lastIndex = Math.min(page * PAGE_SIZE, applications.size());
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOFullApplication fullApplication = applications.get(i);
            final BTOProject project = fullApplication.getProject();
            final BTOApplication application = fullApplication.getApplication();
            final List<BTOApplicationWithdrawal> withdrawals = fullApplication.getWithdrawals();
            final List<BTOProjectType> types = new ArrayList<>(project.getProjectTypes());

            Optional<User> managerOpt = userManager.getUser(project.getManagerUserId());
            List<User> officers = project.getManagingOfficerRegistrations().stream()
                    .map((reg) -> {
                        return userManager.getUser(reg.getOfficerUserId());
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            String managerName = managerOpt.isPresent() ? managerOpt.get().getName() : "(Unknown)";
            String officerNames = !officers.isEmpty()
                    ? officers.stream().map(User::getName).reduce((a, b) -> {
                        if (a.isEmpty()) {
                            return b;
                        }
                        return a + ", " + b;
                    }).get()
                    : "(None)";

            System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
            System.out.println("ID: " + project.getId());

            BTOApplicationStatus status = application.getStatus();
            String statusStr = stringifyApplicationStatus(status);

            System.out.println("Status: " + statusStr);

            Optional<BTOProjectType> typeOpt = types.stream()
                    .filter(t -> t.getId().equals(application.getTypeId()))
                    .findFirst();

            if (typeOpt.isEmpty()) {
                System.out.println(BashColors.format("Type: (Unknown)", BashColors.RED));
                System.out.println("  No. of Units: (Unknown)");
                System.out.println("  Price: (Unknown)");
                System.out.println(BashColors.format(
                        "  Type " + application.getTypeId().getName() + " does not exist in project.", BashColors.RED));
            } else {
                BTOProjectType type = typeOpt.get();
                System.out.println("Type: " + type.getId().getName());
                System.out.println("  No. of Units: " + project.getBookedCountForType(type.getId()) + " / "
                        + type.getMaxQuantity());
                System.out.println("  Price: $" + Utils.formatMoney(type.getPrice()));
            }

            System.out.println("Application period: " + Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate()));
            System.out.println("Manager / Officers: " + managerName + " / " + officerNames);
            if (withdrawals.isEmpty()) {
                System.out
                        .println("Withdrawal Status: " + BashColors.format("(No Withdrawals)", BashColors.LIGHT_GRAY));
            } else {
                System.out.println("Withdrawal Status: ");
                withdrawals.forEach((withdrawal) -> {
                    String withdrawalStatus = stringifyWithdrawalStatus(withdrawal.getStatus());
                    System.out.println(
                            "  " + withdrawalStatus + BashColors.format(" requested on: ", BashColors.LIGHT_GRAY)
                                    + Utils.formatToDDMMYYYY(new Date(withdrawal.getRequestedOn())));
                });
            }
            System.out.println();
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + applications.size());
    }

    /**
     * Show the options for the applicant.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showApplications();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'e' to enquire, 'w' to view withdrawals, 'f' to filter, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page, or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "e":
                    Optional<BTOProject> projectOpt = showRequestProject();
                    if (projectOpt.isEmpty()) {
                        break;
                    }
                    final BTOProject project = projectOpt.get();
                    return new ProjectEnquiryView(project, () -> project.getEnquiries().stream()
                            .filter((enquiry) -> enquiry.getSenderMessage().getSenderUserId().equals(user.getId()))
                            .toList());
                case "w":
                    return new ApplicantApplicationWithdrawalView();
                case "f":
                    return new ApplicantViewMyApplicationsFilterView();
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
     * Show the request project view.
     *
     * @return the project
     */
    private Optional<BTOProject> showRequestProject() {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        while (true) {
            System.out.println(BashColors.format(
                    "Type in the project id you or leave empty ('') to cancel:", BashColors.BOLD));
            final String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println(
                        BashColors.format("Project not found, please type in a valid project id.", BashColors.BOLD));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            return projectOpt;
        }
    }

}
