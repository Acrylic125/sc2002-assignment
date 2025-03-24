package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOApplicationWithdrawal;
import com.group6.btoproject.BTOApplicationWithdrawalStatus;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.BTOProjectManager.BTOFullApplication;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.Utils;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantViewAppliedProjects implements View {
    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;

    private int getLastPage(List<BTOFullApplication> applications) {
        return applications.size() / PAGE_SIZE + 1;
    }

    @Override
    public View render(ViewContext ctx) {
        final Optional<User> userOpt = ctx.getUser();
        final Scanner scanner = ctx.getScanner();
        if (userOpt.isEmpty()) {
            System.out.println("You are not logged in. Please sign in.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return null;
        }
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.ctx = ctx;
        this.user = userOpt.get();

        List<BTOFullApplication> applications = new ArrayList<>(
                projectManager.getAllApplicationsForUser(user.getId()));
        applications.sort((a, b) -> a.getProject().getName().compareTo(b.getProject().getName()));

        showApplications(applications);
        return showOptions(applications);
    }

    private void showApplications(List<BTOFullApplication> fullApplications) {
        final UserManager userManager = ctx.getBtoSystem().getUsers();

        int lastIndex = Math.min(page * PAGE_SIZE, fullApplications.size());
        // Render the projects in the page.
        for (int i = (page - 1) * PAGE_SIZE; i < lastIndex; i++) {
            final BTOFullApplication fullApplication = fullApplications.get(i);
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
            String officerNames = officers.size() > 0
                    ? officers.stream().map(User::getName).reduce((a, b) -> {
                        if (a.isEmpty()) {
                            return b;
                        }
                        return a + ", " + b;
                    }).get()
                    : "(None)";

            System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
            System.out.println("ID: " + project.getId());
            System.out.println("Status: " + fullApplication.getApplication().getStatus());

            Optional<BTOProjectType> typeOpt = types.stream()
                    .filter(t -> t.getId().equals(application.getTypeId()))
                    .findFirst();

            if (typeOpt.isEmpty()) {
                System.out.println(
                        "No. of Units: (Unknown)");
                System.out.println("  Type " + typeOpt.get().getId() + " does not exist in project.");
                System.out.println("Price: (Unknown)");
                System.out.println("  Type " + typeOpt.get().getId() + " does not exist in project.");
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
    }

    private View showOptions(List<BTOFullApplication> fullApplications) {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println("Page " + page + " / " + getLastPage(fullApplications) +
                    " - Type 'e' to enquire, 'a' to apply, 'f' to filter, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "e":
                    return new ApplicantProjectEnquiryView();
                case "w":
                    return new ApplicantApplicationWithdrawalView();
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
