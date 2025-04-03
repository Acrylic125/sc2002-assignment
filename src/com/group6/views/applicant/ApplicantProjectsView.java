package com.group6.views.applicant;

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
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantProjectsView implements PaginatedView, AuthenticatedView {

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
                .filter((project) -> ((project.isApplicationWindowOpen() || project.isApplicantBooked(user.getId()))
                        && project.isVisibleToPublic())
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
        System.out.println(BashColors.format("Projects", BashColors.BOLD));
        if (projects.isEmpty()) {
            System.out.println(BashColors.format("(No Projects Found)", BashColors.LIGHT_GRAY));
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
            boolean isWindowOpen = project.isApplicationWindowOpen();
            String projectOpenWindowStr = Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate());
            System.out.println("Application period: " + BashColors.format(projectOpenWindowStr, isWindowOpen
                    ? BashColors.GREEN
                    : BashColors.RED));
            System.out.println("Manager / Officers: " + managerName + " / " + officerNames);
            if (user instanceof HDBOfficer || user instanceof HDBManager) {
                boolean isVisibleToPublic = project.isVisibleToPublic();
                System.out.println("Visible to public: " + BashColors.format(isVisibleToPublic ? "YES" : "NO",
                        isVisibleToPublic ? BashColors.GREEN : BashColors.RED));
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
                    " - Type 'e' to enquire, 'a' to apply, 'f' to filter, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "a":
                    return new ApplicantApplyProjectView();
                case "e":
                    return new ApplicantProjectEnquiryView();
                case "f":
                    return new ProjectsViewFiltersView();
                case "n":
                    if (!this.nextPage()) {
                        System.out.println(BashColors.format("You are already on the last page.", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
                    break;
                case "p":
                    if (!this.prevPage()) {
                        System.out.println(BashColors.format("You are already on the first page.", BashColors.RED));
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
                        System.out.println(BashColors.format("Invalid page number.", BashColors.RED));
                        System.out.println("Type anything to continue.");
                        scanner.nextLine();
                    }
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
