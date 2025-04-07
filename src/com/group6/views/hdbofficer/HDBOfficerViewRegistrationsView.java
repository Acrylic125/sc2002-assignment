package com.group6.views.hdbofficer;

import java.util.*;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.HDBOfficerRegistration;
import com.group6.btoproject.HDBOfficerRegistrationStatus;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

public class HDBOfficerViewRegistrationsView implements AuthenticatedView, PaginatedView {

    private static final int PAGE_SIZE = 3;

    private int page = 1;

    private ViewContext ctx;
    private User user;
    private List<BTOProjectManager.BTOFullOfficerRegistration> officerRegistrations = new ArrayList<>();

    private String stringifyOfficerStatus(HDBOfficerRegistrationStatus status) {
        return switch (status) {
            case PENDING -> BashColors.format( "Pending", BashColors.YELLOW);
            case SUCCESSFUL -> BashColors.format("Successful", BashColors.GREEN);
            case UNSUCCESSFUL -> BashColors.format("Unsuccessful", BashColors.RED);
        };
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        this.officerRegistrations = projectManager.getAllOfficerRegistrations(user.getId());

        return showOptions();
    }

    private void showRegistrations() {
        final UserManager userManager = ctx.getBtoSystem().getUsers();

        System.out.println(BashColors.format("My Officer Registrations", BashColors.BOLD));
        if (officerRegistrations.isEmpty()) {
            System.out.println(BashColors.format("(No registrations found)", BashColors.LIGHT_GRAY));
            return;
        }
        final int lastIndex = Math.min(page * PAGE_SIZE, officerRegistrations.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOProjectManager.BTOFullOfficerRegistration _registration = officerRegistrations.get(i);
            final BTOProject project = _registration.getProject();
            final HDBOfficerRegistration registration = _registration.getRegistration();

            Optional<User> managerOpt = userManager.getUser(project.getManagerUserId());
            List<User> officers = project.getManagingOfficerRegistrations().stream()
                    .map((reg) -> {
                        return userManager.getUser(reg.getOfficerUserId());
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            String managerName = managerOpt.isPresent() ? managerOpt.get().getName() : BashColors.format("(Unknown)", BashColors.LIGHT_GRAY);
            String officerNames = !officers.isEmpty()
                    ? officers.stream().map(User::getName).reduce((a, b) -> {
                if (a.isEmpty()) {
                    return b;
                }
                return a + ", " + b;
            }).get() : BashColors.format("(None)", BashColors.LIGHT_GRAY);

            System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
            System.out.println("ID: " + project.getId());
            System.out.println("Status: " + stringifyOfficerStatus(registration.getStatus()));
            boolean isWindowOpen = project.isApplicationWindowOpen();
            String projectOpenWindowStr = Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate());
            System.out.println("Application and Registration period: " + BashColors.format(projectOpenWindowStr, isWindowOpen
                    ? BashColors.GREEN
                    : BashColors.RED));
            String officerSlotsStr = officers.size() + " / " + project.getOfficerLimit();
            if (officers.size() >= project.getOfficerLimit()) {
                officerSlotsStr = BashColors.format(officerSlotsStr, BashColors.RED);
            }
            System.out.println("Manager / Officers (" + officerSlotsStr + "): " + managerName + " / " + officerNames);
            System.out.println("");
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + officerRegistrations.size());
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showRegistrations();;
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
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

    @Override
    public int getLastPage() {
        int size = officerRegistrations.size();
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

}
