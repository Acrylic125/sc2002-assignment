package com.group6.views;

import com.group6.users.User;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.views.applicant.ApplicantHomeView;
import com.group6.views.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Role-based home view for authenticated users.
 * This view allows users to select their role and navigate to the appropriate portal.
 */
public class RoleBasedHomeView implements AuthenticatedView {

    private ViewContext ctx;
    private User user;

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
        this.user = user;

        return showOptions();
    }

    /**
     * Show the options for the role-based home view.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        final List<ViewOption> options = new ArrayList<>();
        final UserPermissions permissions = user.getPermissions();
        // Everyone can view applications.
        options.add(
                new ViewOption("Go to Applicant Portal (" +
                        BashColors.format("View applications and enquire",
                                BashColors.LIGHT_GRAY)
                        +
                        ")",
                        () -> new ApplicantHomeView(false)));
        if (permissions.canManageProjects()) {
            options.add(new ViewOption(
                    "Go to Manager Portal (" +
                            BashColors.format("Answer enquiries, Register for projects, "
                                    + "Manage approved Projects",
                                    BashColors.LIGHT_GRAY)
                            +
                            ")",
                    () -> new ManagementHomeView(false)));
        }
        options.add(new ViewOption("Go to Profile", () -> new ProfileView()));
        options.add(new ViewOption("Logout", () -> {
            logout();
            return new MenuView();
        }));

<<<<<<< HEAD
<<<<<<< HEAD
        while (true) {
            System.out.println(BashColors.format("Choose a dashboard to view", BashColors.BOLD));
=======
        while (true) {
            System.out.println(
                    BashColors.format("Choose a dashboard to view", BashColors.BOLD));
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
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
            System.out.println();
            System.out.println("Type the option (e.g. 1, 2, 3) you want to select.");

            String _optionIndex = scanner.nextLine().trim();
            try {
                int optionIndex = Integer.parseInt(_optionIndex) - 1;
                if (optionIndex >= 0 && optionIndex < options.size()) {
                    ViewOption option = options.get(optionIndex);
                    if (option != null) {
                        return option.getCallback().get();
                    }
                }
<<<<<<< HEAD
            } catch (NumberFormatException _e) {
=======
            } catch (NumberFormatException err) {
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
            }
            System.out.println(BashColors.format("Invalid option.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
<<<<<<< HEAD
=======
      String _optionIndex = scanner.nextLine().trim();
      try {
        int optionIndex = Integer.parseInt(_optionIndex) - 1;
        if (optionIndex >= 0 && optionIndex < options.size()) {
          ViewOption option = options.get(optionIndex);
          if (option != null) {
            return option.getCallback().get();
          }
>>>>>>> 976532601e4868fdaddbf77fa3de6e9d44812785
=======
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
        }
    }

    /**
     * Logout the user.
     */
    private void logout() {
        ctx.clearViewStack();
        ctx.setUser(null);
        ctx.resetFilters();

        System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
    }
}
