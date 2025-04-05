package com.group6.views.hdbofficer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.users.HDBManager;
import com.group6.users.HDBOfficer;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;
import com.group6.views.applicant.ProjectsViewFilters;
import com.group6.views.applicant.ProjectsViewFiltersView;

public class HDBOfficerProjectsView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;
    private List<BTOProject> projects = new ArrayList<>();
    private List<BTOProject> filteredProjects = new ArrayList<>();

    @Override
    public int getLastPage() {
        int size = filteredProjects.size();
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
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();

        this.ctx = ctx;
        this.user = user;
        this.projects = projectManager.getProjects().values().stream()
                .filter((project) -> project.isVisibleToPublic()
                        || user instanceof HDBOfficer
                        || user instanceof HDBManager)
                .toList();
        // We ASSUME filters cannot be applied UNTIL the user goes to the filters view
        // and return which will call this.
        //
        // We just do it once here to avoid unnecessary filtering.
        this.filteredProjects = filters.applyFilters(this.projects, user);

        return this.showOptions();
    }

    private void showProjects() {
        List<BTOProject> projects = this.filteredProjects;
        System.out.println("Projects");
        if (projects.isEmpty()) {
            System.out.println("(No Projects Found)");
            System.out.println("");
            return;
        }

        final UserManager userManager = ctx.getBtoSystem().getUsers();

        final int lastIndex = Math.min(page * PAGE_SIZE, projects.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOProject project = projects.get(i);
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
            System.out.println("Types (No. Units Available / Total No. Units / Price):");
            if (types.size() <= 0) {
                System.out.println("  (No types available)");
            } else {
                types.sort((a, b) -> a.getId().compareTo(b.getId()));

                for (BTOProjectType type : types) {
                    System.out.println(
                            "  " + type.getId() + " " +
                                    project.getBookedCountForType(type.getId()) + " / " + type.getMaxQuantity()
                                    + " / $" + Utils.formatMoney(type.getPrice()));
                }
            }
            System.out.println("Application period: " + Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate()));
            System.out.println("Manager / Officers: " + managerName + " / " + officerNames);
            if (user instanceof HDBOfficer || user instanceof HDBManager) {
                System.out.println("Visible to public: " + project.isVisibleToPublic());
            }
            System.out.println("");
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + projects.size());
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showProjects();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'a' to apply, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "a":
                    return new HDBOfficerRegisterCheck();
                case "n":
                    if (!this.nextPage()) {
                        System.out.println("You are already on the last page.");
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "p":
                    if (!this.prevPage()) {
                        System.out.println("You are already on the first page.");
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "page":
                    Optional<Integer> pageOpt = this.requestPage(scanner);
                    if (pageOpt.isEmpty()) {
                        break;
                    }
                    if (!this.page(pageOpt.get())) {
                        System.out.println("Invalid page number.");
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "":
                    return null;
                default:
                    System.out.println("Invalid option.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

}
