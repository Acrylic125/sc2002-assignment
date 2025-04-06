package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOApplicationStatus;
import com.group6.btoproject.BTOApplicationWithdrawal;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.BTOProjectManager.BTOFullApplication;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantViewMyApplicationsView implements PaginatedView, AuthenticatedView {
    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private int page = 1;
    private List<BTOFullApplication> applications = new ArrayList<>();
    private List<BTOFullApplication> filteredApplications = new ArrayList<>();

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

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        this.applications = new ArrayList<>(
                projectManager.getAllApplicationsForUser(user.getId()));
        this.filteredApplications = ctx.getViewApplicationFilters().applyFilters(this.applications, user);

        return showOptions();
    }

    private void showApplications() {
        final List<BTOFullApplication> applications = this.filteredApplications;
        System.out.println(BashColors.format("My Applications", BashColors.BOLD));
        if (applications.isEmpty()) {
            System.out.println(BashColors.format("(No Projects Applied/Found)", BashColors.LIGHT_GRAY));
            System.out.println("");
            return;
        }
        final UserManager userManager = ctx.getBtoSystem().getUsers();

        final int firstIndex = (page - 1) * PAGE_SIZE;
        final int lastIndex = Math.min(page * PAGE_SIZE, applications.size());
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOFullApplication fullApplication = applications.get(i);
            final BTOProject project = fullApplication.getProject();
            final BTOApplication application = fullApplication.getApplication();
            final Optional<BTOApplicationWithdrawal> withdrawalOpt = fullApplication.getWithdrawal();
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
            String statusStr;
            switch (status) {
                case PENDING:
                    statusStr = BashColors.format("Pending", BashColors.YELLOW);
                    break;
                case BOOKED:
                    statusStr = BashColors.format("Booked", BashColors.BLUE);
                    break;
                case SUCCESSFUL:
                    statusStr = BashColors.format("Successful", BashColors.GREEN);
                    break;
                case UNSUCCESSFUL:
                    statusStr = BashColors.format("Unsuccessful", BashColors.RED);
                    break;
                default:
                    statusStr = "Unknown";
            }

            System.out.println("Status: " + statusStr);

            Optional<BTOProjectType> typeOpt = types.stream()
                    .filter(t -> t.getId().equals(application.getTypeId()))
                    .findFirst();

            if (typeOpt.isEmpty()) {
                System.out.println(
                        "No. of Units: (Unknown)");
                System.out.println("Price: (Unknown)");
                System.out.println(BashColors.format("  Type " + application.getTypeId().getName() + " does not exist in project.", BashColors.RED));
            } else {
                BTOProjectType type = typeOpt.get();
                System.out.println(
                        "No. of Units: " + project.getBookedCountForType(type.getId()) + " / " + type.getMaxQuantity());
                System.out.println("Price: $" + Utils.formatMoney(type.getPrice()));
            }

            System.out.println("Application period: " + Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate()));
            System.out.println("Manager / Officers: " + managerName + " / " + officerNames);
            if (withdrawalOpt.isPresent()) {
                System.out.println("Withdrawal Status: " + withdrawalOpt.get().getStatus());
            } else {
                System.out.println("Withdrawal Status: N/A");
            }
            System.out.println("");
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + applications.size());
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showApplications();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'e' to enquire, 'f' to filter, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "e":
                    return new ApplicantProjectEnquiryView();
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

}
