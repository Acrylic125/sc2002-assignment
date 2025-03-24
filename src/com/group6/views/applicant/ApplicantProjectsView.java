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
import com.group6.utils.Utils;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantProjectsView implements View {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;
    private List<BTOProject> projects = new ArrayList<>();

    private int getLastPage() {
        return projects.size() / PAGE_SIZE + 1;
    }

    @Override
    public View render(ViewContext ctx) {
        final Optional<User> userOpt = ctx.getUser();
        if (userOpt.isEmpty()) {
            System.out.println("You are not logged in. Please sign in.");
            return null;
        }
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.ctx = ctx;
        this.user = userOpt.get();
        this.projects = new ArrayList<>(
                projectManager.getProjects().values().stream()
                        .filter((project) -> project.isVisibleToPublic()
                                || user instanceof HDBOfficer
                                || user instanceof HDBManager)
                        .toList());
        this.projects.sort((a, b) -> a.getName().compareTo(b.getName()));

        this.showProjects();
        return this.showOptions();
    }

    private void showProjects() {
        final UserManager userManager = ctx.getBtoSystem().getUsers();

        int lastIndex = Math.min(page * PAGE_SIZE, projects.size());
        // Render the projects in the page.
        for (int i = (page - 1) * PAGE_SIZE; i < lastIndex; i++) {
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
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 'e' to enquire, 'a' to apply, 'f' to filter, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "a":
                    return new ApplicantApplyProjectView();
                case "e":
                    return new ApplicantProjectEnquiryView();
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
