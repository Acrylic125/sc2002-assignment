package com.group6.views.applicant;

import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Applicant portal view.
 */
public class ApplicantHomeView implements AuthenticatedView {
    private final boolean isRootView;

    private ViewContext ctx;
    private User user;

    /**
     * Constructor for ApplicantHomeView.
     *
     * @param isRootView whether this is the root view
     */
    public ApplicantHomeView(boolean isRootView) {
        this.isRootView = isRootView;
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
        this.user = user;

        return showOptions();
    }

    /**
     * Show the options for the applicant portal.
     *
     * @return next view
     */
    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        List<ViewOption> options = new ArrayList<>();
        options.add(new ViewOption("View All Projects", ProjectsView::new));
        if (user.getPermissions().canApply()) {
            options.add(new ViewOption("View My Applied Projects",
                    ApplicantViewMyApplicationsView::new));
            options.add(new ViewOption("View My Enquiries",
                    ApplicantViewMyEnquiriesView::new));
            options.add(new ViewOption("View My Booking Receipts",
                    ApplicantReceiptsView::new));
        }
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
        if (this.isRootView) {
            options.add(new ViewOption("Logout", () -> {
                logout();
                return new MenuView();
            }));
<<<<<<< HEAD
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
            } catch (Throwable _e) {
            }
            System.out.println(BashColors.format("Invalid option.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
=======
      }
      System.out.println();
      if (!isRootView) {
        System.out.println("Type the option (e.g. 1, 2, 3) you want to "
                           + "select or leave empty ('') to cancel.");
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
>>>>>>> 976532601e4868fdaddbf77fa3de6e9d44812785
=======
>>>>>>> 47c9106c37b5f5c29871240ed9140c55f57d622b
        }

        while (true) {
            System.out.println(
                    BashColors.format("Applicant Portal", BashColors.BOLD));
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
                System.out.println("Type the option (e.g. 1, 2, 3) you want to "
                        + "select or leave empty ('') to cancel.");
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
            } catch (Throwable err) {
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
