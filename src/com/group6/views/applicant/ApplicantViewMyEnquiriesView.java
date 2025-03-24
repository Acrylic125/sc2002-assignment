package com.group6.views.applicant;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.HDBManager;
import com.group6.users.HDBOfficer;
import com.group6.users.User;
import com.group6.views.View;
import com.group6.views.ViewContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ApplicantViewMyEnquiriesView implements View {
    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;

    private int getLastPage(List<BTOProject> projects) {
        return projects.size() / PAGE_SIZE + 1;
    }

    @Override
    public View render(ViewContext ctx) {
        final Scanner scanner = ctx.getScanner();
        final Optional<User> userOpt = ctx.getUser();
        if (userOpt.isEmpty()) {
            System.out.println("You are not logged in. Please sign in.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.ctx = ctx;
        this.user = userOpt.get();

        List<BTOProject> projects = new ArrayList<>(
                projectManager.getProjects().values().stream()
                        .filter((project) -> {
                            if (!(project.isVisibleToPublic()
                                    || user instanceof HDBOfficer
                                    || user instanceof HDBManager)) {
                                return false;
                            }
                            return project.getEnquiries()
                                    .stream()
                                    .anyMatch((enquiry) -> enquiry.getSenderMessage().getSenderUserId()
                                            .equals(user.getId()));
                        })
                        .toList());
        projects.sort((a, b) -> a.getName().compareTo(b.getName()));
        showProjects(projects);

        return showOptions(projects);
    }

    private void showProjects(List<BTOProject> projects) {
        int lastIndex = Math.min(page * PAGE_SIZE, projects.size());
        System.out.println("Your Eqnuiries:");
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

    private View showOptions(List<BTOProject> projects) {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println("Page " + page + " / " + getLastPage(projects) +
                    " - Type 'e' to enquire, 'n' to go to next page, 'p' to go to previous page,'page' to go to a specific page, or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "e":
                    return new ApplicantProjectEnquiryView(true);
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
