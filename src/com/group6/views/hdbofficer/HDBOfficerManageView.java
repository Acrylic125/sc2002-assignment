package com.group6.views.hdbofficer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.group6.users.User;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.views.*;

public class HDBOfficerManageView implements AuthenticatedView {
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
            options.add(new ViewOption("View My Registrations", HDBOfficerViewRegistrationsView::new));
        }
        options.add(new ViewOption("View My managed projects", HDBOfficerManagedProjectsView::new));
        // options.add(new ViewOption("Approve Applicant Applications", () -> new
        // HDBOfficerApplicationApprovalView()));
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
            if (!isRootView) {
                System.out.println("Type the option (e.g. 1, 2, 3) you want to select or leave empty ('') to cancel.");
            } else {
                System.out.println("Type the option (e.g. 1, 2, 3) you want to select.");
            }

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

    private void logout() {
        ctx.clearViewStack();
        ctx.setUser(null);
        System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
    }

}
