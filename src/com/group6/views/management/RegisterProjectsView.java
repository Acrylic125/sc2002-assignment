package com.group6.views.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.*;
import com.group6.views.applicant.ProjectsViewFilters;

/**
 * View for the management to register to manage a project.
 */
public class RegisterProjectsView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private int page = 1;
    private List<BTOProject> filteredProjects = new ArrayList<>();

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = filteredProjects.size();
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
     * View renderer.
     *
     * @param ctx  view context
     * @param user authenticated user
     * @return next view
     */
    @Override
    public View render(ViewContext ctx, User user) {
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        this.ctx = ctx;
        final List<BTOProject> projects = projectManager.getProjects().values().stream()
                .filter((project) -> project.isVisibleToPublic() || user.getPermissions().canViewNonVisibleProjects())
                .toList();
        // No filtering lol.
        this.filteredProjects = projects;

        return this.showOptions();
    }

    /**
     * Show projects that the user can register for.
     */
    private void showProjects() {
        List<BTOProject> projects = this.filteredProjects;
        System.out.println(BashColors.format("Projects", BashColors.BOLD));
        if (projects.isEmpty()) {
            System.out.println(BashColors.format("(No Projects Found)", BashColors.LIGHT_GRAY));
            System.out.println();
            return;
        }

        final UserManager userManager = ctx.getBtoSystem().getUserManager();

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
            System.out.println("Types (No. Units Taken / Total No. Units / Price):");
            if (types.isEmpty()) {
                System.out.println(BashColors.format("  (No types available)", BashColors.LIGHT_GRAY));
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
            System.out.println("Application period: " + BashColors.format(projectOpenWindowStr,
                    isWindowOpen
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
            System.out.println();
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + projects.size());
    }

    /**
     * Show options available.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showProjects();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'r' to register to manage, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "r":
                    return new OfficerRegistrationView();
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
