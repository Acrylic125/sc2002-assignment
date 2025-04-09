package com.group6.views.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectManager.BTOFullApplication;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class ApplicantReportView implements AuthenticatedView, PaginatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private int page = 1;
    private final List<User> applicants;

    public ApplicantReportView(List<User> applicants) {
        this.applicants = applicants;
    }

    @Override
    public int getLastPage() {
        int size = applicants.size();
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
        this.ctx = ctx;

        showOptions();

        return null;
    }

    private void showOptions() {
        final Scanner scanner = ctx.getScanner();

        Utils.joinStringDelimiter(new ArrayList<>(), ", ", " or ");
        final List<String> options = new ArrayList<>();
        options.add("'n' to go to next page");
        options.add("'p' to go to previous page");
        options.add("'page' to go to a specific page");
        options.add("leave empty ('') to go back");

        final String optionsStr = Utils.joinStringDelimiter(
                options,
                ", ",
                " or ");
        while (true) {
            showApplicants();
            System.out.println("");
            System.out.println("Page " + page + " / " + getLastPage() + " - " + optionsStr + ":");

            String opt = scanner.nextLine().trim();
            switch (opt) {
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
                    return;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private void showApplicants() {
        System.out.println(BashColors.format("Projects", BashColors.BOLD));
        if (applicants.isEmpty()) {
            System.out.println(BashColors.format("(No applicants found)", BashColors.LIGHT_GRAY));
            System.out.println("");
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        final int lastIndex = Math.min(page * PAGE_SIZE, applicants.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            User applicant = applicants.get(i);

            System.out.println("Applicant Name: " + applicant.getName());
            System.out.println("ID: " + applicant.getId());
            System.out.println("Age: " + applicant.getAge());
            System.out.println("Marital Status: " + applicant.getMaritalStatus());
            System.out.println("Projects Booked (Name, neighbourhood / Type): ");
            List<BTOFullApplication> bookedProjects = projectManager.getBookedApplicationsForUser(applicant.getId());
            if (bookedProjects.isEmpty()) {
                System.out.println(BashColors.format("  (No booked projects)", BashColors.LIGHT_GRAY));
            } else {
                for (BTOFullApplication application : bookedProjects) {
                    BTOProject project = application.getProject();
                    System.out.println("  " + project.getName() + ", " + project.getNeighbourhood() + " / "
                            + application.getApplication().getTypeId().getName());
                }
            }

            System.out.println();
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + applicants.size());
    }

}
