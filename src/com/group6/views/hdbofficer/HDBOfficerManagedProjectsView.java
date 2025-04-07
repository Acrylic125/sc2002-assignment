package com.group6.views.hdbofficer;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class HDBOfficerManagedProjectsView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private int page = 1;
    private ViewContext ctx;
    private User user;
    private List<BTOProject> managedProjects = new ArrayList<>();

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canManageProjects();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.managedProjects = projectManager.getOfficerManagingProjects(user.getId());

        return showOptions();
    }

    private void showManagedProjects() {
        final UserManager userManager = ctx.getBtoSystem().getUsers();

        System.out.println(BashColors.format("My Managed Projects", BashColors.BOLD));
        if (managedProjects.isEmpty()) {
            System.out.println(BashColors.format("(No managed projects found)", BashColors.LIGHT_GRAY));
            return;
        }
        final int lastIndex = Math.min(page * PAGE_SIZE, managedProjects.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOProject project = managedProjects.get(i);

            Optional<User> managerOpt = userManager.getUser(project.getManagerUserId());
            List<User> officers = project.getManagingOfficerRegistrations().stream()
                    .map((reg) -> {
                        return userManager.getUser(reg.getOfficerUserId());
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            String managerName = managerOpt.isPresent() ? managerOpt.get().getName()
                    : BashColors.format("(Unknown)", BashColors.LIGHT_GRAY);
            String officerNames = !officers.isEmpty()
                    ? officers.stream().map(User::getName).reduce((a, b) -> {
                        if (a.isEmpty()) {
                            return b;
                        }
                        return a + ", " + b;
                    }).get()
                    : BashColors.format("(None)", BashColors.LIGHT_GRAY);

            System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
            System.out.println("ID: " + project.getId());
            boolean isWindowOpen = project.isApplicationWindowOpen();
            String projectOpenWindowStr = Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate());
            System.out.println("Application and Registration period: " + BashColors.format(projectOpenWindowStr,
                    isWindowOpen
                            ? BashColors.GREEN
                            : BashColors.RED));
            String officerSlotsStr = officers.size() + " / " + project.getOfficerLimit();
            if (officers.size() >= project.getOfficerLimit()) {
                officerSlotsStr = BashColors.format(officerSlotsStr, BashColors.RED);
            }
            System.out.println("Manager / Officers (" + officerSlotsStr + "): " + managerName + " / " + officerNames);
            System.out.println("");
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + managedProjects.size());
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();
        final List<String> options = new ArrayList<>();
        final UserPermissions permissions = user.getPermissions();
        if (permissions.canApproveApplications()) {
            options.add("'a' to go to Applications");
        }
        if (permissions.canApproveWithdrawal()) {
            options.add("'w' to go to Withdrawals");
        }
        options.add("'n' to go to next page");
        options.add("'p' to go to previous page");
        options.add("'page' to go to a specific page");
        options.add("leave empty ('') to go back");

        while (true) {
            showManagedProjects();
            ;
            String optionsStr = Utils.joinStringDelimiter(
                    options,
                    ", ",
                    " or ");
            System.out.println("Page " + page + " / " + getLastPage() + " - " + optionsStr + ":");

            String option = scanner.nextLine().trim();
            switch (option) {
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
                case "a":
                    if (permissions.canApproveApplications()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }

                        return new HDBOfficerApplicationApprovalView(projectOpt.get());
                    }
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private Optional<BTOProject> requestProject() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            System.out.println(BashColors.format(
                    "Enter ID of Project or leave empty ('') to cancel: ",
                    BashColors.BOLD));
            String projectid = scanner.nextLine().trim();
            if (projectid.equals("")) {
                return Optional.empty();
            }

            final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
            final Optional<BTOProject> projectOpt = projectManager.getProject(projectid);
            if (projectOpt.isEmpty()) {
                System.out.println(BashColors.format(
                        "Project not found.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            BTOProject project = projectOpt.get();
            if (!project.isManagingOfficer(user.getId())) {
                System.out.println(BashColors.format(
                        "You are not managing this project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return projectOpt;
        }
    }

    @Override
    public int getLastPage() {
        int size = managedProjects.size();
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

}
