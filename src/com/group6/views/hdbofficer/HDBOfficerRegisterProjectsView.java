package com.group6.views.hdbofficer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.HDBOfficerRegisterCheck;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;
import com.group6.views.applicant.ProjectsViewFilters;

public class HDBOfficerRegisterProjectsView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
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
        this.projects = projectManager.getProjects().values().stream()
                .filter((project) -> project.isVisibleToPublic() || user.getPermissions().canViewNonVisibleProjects())
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
            }).get() : "(None)";

            System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
            System.out.println("ID: " + project.getId());
            System.out.println("Types (No. Units Available / Total No. Units / Price):");
            if (types.isEmpty()) {
                System.out.println("  (No types available)");
            } else {
                types.sort((a, b) -> a.getId().compareTo(b.getId()));

                for (BTOProjectType type : types) {
                    System.out.println(
                            "  " + type.getId().getName() + ": " +
                                    project.getBookedCountForType(type.getId()) + " / " + type.getMaxQuantity()
                                    + " / $" + Utils.formatMoney(type.getPrice()));
                }
            }
            boolean isWindowOpen = project.isApplicationWindowOpen();
            String projectOpenWindowStr = Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate());
            System.out.println("Application / Registration period: " + BashColors.format(projectOpenWindowStr, isWindowOpen
                    ? BashColors.GREEN
                    : BashColors.RED));

            String officerSlotsStr = officers.size() + " / " + project.getOfficerLimit();
            if (officers.size() >= project.getOfficerLimit()) {
                officerSlotsStr = BashColors.format(officerSlotsStr, BashColors.RED);
            }
            System.out.println("Manager / Officers (" + officerSlotsStr + "): " + managerName + " / " + officerNames);
            boolean isVisibleToPublic = project.isVisibleToPublic();
            System.out.println("Visible to public: " + BashColors.format(isVisibleToPublic ? "YES" : "NO",
                    isVisibleToPublic ? BashColors.GREEN : BashColors.RED));
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
