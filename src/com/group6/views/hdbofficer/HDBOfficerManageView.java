package com.group6.views.hdbofficer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.views.*;

public class HDBOfficerManageView implements AuthenticatedView{
    private final boolean isRootView;

    private ViewContext ctx;
    private User user;

    public HDBOfficerManageView(boolean isRootView) {
        this.isRootView = isRootView;
    }

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canManageProjects();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;
        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();
        final UserPermissions userPermissions = user.getPermissions();

        List<ViewOption> options = new ArrayList<>();
        if (userPermissions.canRegisterForProject()) {
            options.add(new ViewOption("Register to Manage Project", HDBOfficerRegisterProjectsView::new));
        }
        options.add(new ViewOption("View All Registrations", () -> {
            displayOfficerRegistrations();
            return null;
        }));
        options.add(new ViewOption("View Successful Applications (My managed projects) and Answer Enquiries", HDBOfficeRespondView::new));
        options.add(new ViewOption("Approve Applicant Applications", HDBOfficerAppApprovalView::new));
        if (isRootView) {
            options.add(new ViewOption("Logout", () -> {
                logout();
                return new MenuView();
            }));
        }

        while (true) {
            System.out.println(BashColors.format("Applicant Home Menu", BashColors.BOLD));
            int i = 1;
            for (ViewOption option : options) {
                String[] str = option.getOption();
                if (str.length > 0) {
                    System.out.println(i++ + ". " + str[0]);
                    for (int j = 1; j < str.length; j++) {
                        System.out.println("   " + str[j]);
                    }
                }
            }
            System.out.println("");

            String _optionIndex = scanner.nextLine().trim();
            if (!isRootView && _optionIndex.isEmpty()) {
                return null;
            }
            try {
                int optionIndex = Integer.parseInt(_optionIndex) - 1;
                ViewOption option = options.get(optionIndex);
                if (option != null) {
                    return option.getCallback().get();
                }
            } catch (NumberFormatException _) {}
            System.out.println(BashColors.format("Invalid option.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }

    private void displayOfficerRegistrations() {
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        // Get all projects where the officer has applied to register
        List<BTOProject> officerRegistrations = projectManager.getOfficerRegistrations(user.getId());

        if (officerRegistrations.isEmpty()) {
            System.out.println("You have not applied to register as a managing officer for any projects.");
        } else {
            System.out.println("Projects you have applied to register as a managing officer:");
            for (BTOProject project : officerRegistrations) {
                System.out.println("- Project Name: " + project.getName());
                System.out.println("  Project ID: " + project.getId());
                System.out.println("- Current status: " + project.getActiveOfficerRegistration(user.getId()).map(registration -> registration.getStatus().toString()));
                System.out.println("--------------------------------");
            }
        }
    }

    private void logout() {
        ctx.clearViewStack();
        ctx.setUser(null);
        System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
    }

}
