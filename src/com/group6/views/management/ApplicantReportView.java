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

/**
 * View for the management to view the applicants.
 */
public class ApplicantReportView implements AuthenticatedView, PaginatedView {

    private static final int PAGE_SIZE = 3;

    private final List<User> applicants;

    private ViewContext ctx;
    private int page = 1;
    private List<User> filteredApplicants = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param applicants the list of applicants
     */
    public ApplicantReportView(List<User> applicants) {
        this.applicants = applicants;
        this.filteredApplicants.addAll(applicants);
    }

    /**
     * @return the last page
     */
    @Override
    public int getLastPage() {
        int size = filteredApplicants.size();
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
        this.ctx = ctx;

        final ApplicantReportFilters filters = ctx.getApplicantReportFilters();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
        this.filteredApplicants = filters.applyFilters(projectManager, applicants, user);

        return showOptions();
    }

    /**
     * Show the options for the user.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        Utils.joinStringDelimiter(new ArrayList<>(), ", ", " or ");
        final List<String> options = new ArrayList<>();
        options.add("'f' to go to filters");
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
                case "f":
                    return new ApplicantReportFilterView();
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

    /**
     * Show the applicants.
     */
    private void showApplicants() {
        System.out.println(BashColors.format("Applicants", BashColors.BOLD));
        if (filteredApplicants.isEmpty()) {
            System.out.println(BashColors.format("(No applicants found)", BashColors.LIGHT_GRAY));
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        final int lastIndex = Math.min(page * PAGE_SIZE, filteredApplicants.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            User applicant = filteredApplicants.get(i);

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
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + filteredApplicants.size());
    }

}
