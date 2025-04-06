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

public class ApplicantViewMyEnquiriesView implements PaginatedView, AuthenticatedView {
    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private int page = 1;
    private List<BTOProject> projects = new ArrayList<>();

    @Override
    public int getLastPage() {
        int size = projects.size();
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

        this.ctx = ctx;
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

    private void showProjects() {
        int lastIndex = Math.min(page * PAGE_SIZE, projects.size());
        System.out.println(BashColors.format("Your Eqnuiries:", BashColors.BOLD));
        System.out.println("Project Id | Project Name");
        if (projects.isEmpty()) {
            System.out.println("(No projects with your enquiries found)");
            return;
        }
        // Render the projects in the page.
        for (int i = (page - 1) * PAGE_SIZE; i < lastIndex; i++) {
            final BTOProject project = projects.get(i);
            System.out.println(project.getId() + " | " + project.getName());
        }
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showProjects();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'e' to enquire, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page, or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "e":
                    return new ApplicantProjectEnquiryView(true);
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
