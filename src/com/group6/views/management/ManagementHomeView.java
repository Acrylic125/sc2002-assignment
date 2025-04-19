package com.group6.views.management;

import com.group6.users.User;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.views.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Management portal view.
 */
public class ManagementHomeView implements AuthenticatedView {
    private final boolean isRootView;

    private ViewContext ctx;
    private User user;

    /**
     * Constructor for ManagementHomeView.
     *
     * @param isRootView whether this is the root view
     */
    public ManagementHomeView(boolean isRootView) {
        this.isRootView = isRootView;
    }

<<<<<<< HEAD
<<<<<<< HEAD
=======
    /**
     *
     * @param user the user to be checked.
     * @return true if the user is authorized to view this view.
     */
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canManageProjects();
    }

<<<<<<< HEAD
=======
    /**
     * View renderer.
     *
     * @param ctx  view context
     * @param user authenticated user
     * @return next view
     */
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;
        return showOptions();
    }

<<<<<<< HEAD
=======
    /**
     * Show the options for the management portal.
     *
     * @return next view
     */
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();
        final UserPermissions userPermissions = user.getPermissions();

<<<<<<< HEAD
        List<ViewOption> options = new ArrayList<>();
        if (userPermissions.canCreateProject()) {
            options.add(new ViewOption("Create New Project", CreateBTOProjectView::new));
        }
        if (userPermissions.canRegisterForProject()) {
            options.add(new ViewOption("Register to Manage Project", RegisterProjectsView::new));
            options.add(new ViewOption("View My Registrations", MyOfficerRegistrationsView::new));
        }
        options.add(new ViewOption("View My Managed Projects", ManagedProjectsView::new));
        if (userPermissions.canGenerateApplicantsReport()) {
            options.add(new ViewOption("Generate Applicants Report", () -> {
                final List<User> applicants = ctx.getBtoSystem().getUserManager().getUsers().values().stream()
                        .filter(user -> user.getPermissions().canApply()).toList();
                return new ApplicantReportView(applicants);
            }));
        }
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
            System.out.println();
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
                if (optionIndex >= 0 && optionIndex < options.size()) {
                    ViewOption option = options.get(optionIndex);
                    if (option != null) {
                        return option.getCallback().get();
                    }
                }
            } catch (NumberFormatException _e) {
            }
            System.out.println(BashColors.format("Invalid option.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
    }

    private void logout() {
        ctx.clearViewStack();
        ctx.setUser(null);
        System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
=======
    while (true) {
      System.out.println(
          BashColors.format("Applicant Home Menu", BashColors.BOLD));
      int i = 1;
      for (ViewOption option : options) {
        String[] str = option.getOption();
        if (str.length > 0) {
          System.out.println(i++ + ". " + str[0]);
          for (int j = 1; j < str.length; j++) {
            System.out.println("   " + str[j]);
          }
=======
        List<ViewOption> options = new ArrayList<>();//a ViewOption list that has all options based on user's Permissions (HDB Officer vs HDB Manager)
        if (userPermissions.canCreateProject()) {
            options.add(
                    new ViewOption("Create New Project", CreateBTOProjectView::new));
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
        }
        if (userPermissions.canRegisterForProject()) {
            options.add(new ViewOption("Register to Manage Project",
                    RegisterProjectsView::new));
            options.add(new ViewOption("View My Registrations",
                    MyOfficerRegistrationsView::new));
        }
        options.add(
                new ViewOption("View My Managed Projects", ManagedProjectsView::new));
        if (userPermissions.canGenerateApplicantsReport()) {
            options.add(new ViewOption("Generate Applicants Report", () -> { //Lambda Expression with stream function to filter and obtain a list of applicants that canApply
                final List<User> applicants = ctx.getBtoSystem()
                        .getUserManager()
                        .getUsers()
                        .values()
                        .stream()
                        .filter(user -> user.getPermissions().canApply())
                        .toList();
                return new ApplicantReportView(applicants);
            }));
        }
        // options.add(new ViewOption("Approve Applicant Applications", () -> new
        // HDBOfficerApplicationApprovalView()));
        if (isRootView) {
            options.add(new ViewOption("Logout", () -> {
                logout();
                return new MenuView();
            }));
        }
<<<<<<< HEAD
      } catch (NumberFormatException err) {
      }
      System.out.println(BashColors.format("Invalid option.", BashColors.RED));
      System.out.println("Type anything to continue.");
      scanner.nextLine();
>>>>>>> 976532601e4868fdaddbf77fa3de6e9d44812785
    }
  }
=======
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b

        while (true) {
            System.out.println(
                    BashColors.format("Manager Portal", BashColors.BOLD));
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
            if (!isRootView) {
                System.out.println("Type the option (e.g. 1, 2, 3) you want to " +
                        "select or leave empty ('') to cancel.");
            } else {
                System.out.println(
                        "Type the option (e.g. 1, 2, 3) you want to select.");
            }

            String _optionIndex = scanner.nextLine().trim();
            if (!isRootView && _optionIndex.isEmpty()) {
                return null;
            }
            try {
                int optionIndex = Integer.parseInt(_optionIndex) - 1;
                if (optionIndex >= 0 && optionIndex < options.size()) {
                    ViewOption option = options.get(optionIndex);
                    if (option != null) {
                        return option.getCallback().get();
                    }
                }
            } catch (NumberFormatException err) {
            }
            System.out.println(BashColors.format("Invalid option.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
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