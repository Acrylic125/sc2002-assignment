package com.group6.views.applicant;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * View for the applicant to view their enquiries.
 */
public class ApplicantViewMyEnquiriesView implements PaginatedView, AuthenticatedView {
    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;
    private List<BTOProject> projects = new ArrayList<>();

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = projects.size();
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
        this.user = user;

        this.projects = new ArrayList<>(
                projectManager.getProjects().values().stream()
                        .filter((project) -> {
                            if (!(project.isVisibleToPublic() || user.getPermissions().canViewNonVisibleProjects())) {
                                return false;
                            }
                            return project.getEnquiries()
                                    .stream()
                                    .anyMatch((enquiry) -> enquiry.getSenderMessage().getSenderUserId()
                                            .equals(user.getId()));
                        })
                        .toList());
        projects.sort((a, b) -> a.getName().compareTo(b.getName()));

        return showOptions();
    }

    /**
     * Show the projects with user's enquiries.
     */
    private void showProjectsWithEnquiries() {
        int lastIndex = Math.min(page * PAGE_SIZE, projects.size());
        System.out.println(BashColors.format("Projects with Your Eqnuiries:", BashColors.BOLD));
        System.out.println("Project Id | Project Name");
        if (projects.isEmpty()) {
            System.out.println(BashColors.format("(No projects with your enquiries found)", BashColors.LIGHT_GRAY));
            return;
        }
        // Render the projects in the page.
        for (int i = (page - 1) * PAGE_SIZE; i < lastIndex; i++) {
            final BTOProject project = projects.get(i);
            System.out.println(project.getId() + " | " + project.getName());
        }
    }

    /**
     * Show the options.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showProjectsWithEnquiries();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'e' to enquire, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page, or leave empty ('') to go back:");

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
                        BashColors.format("Project not found, please type in a valid project id.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            return projectOpt;
        }
    }

}
