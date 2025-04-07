package com.group6.views.applicant;

import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplicantHomeView implements AuthenticatedView {
    private final boolean isRootView;

    private ViewContext ctx;

    public ApplicantHomeView(boolean isRootView) {
        this.isRootView = isRootView;
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        List<ViewOption> options = new ArrayList<>();
        options.add(new ViewOption("View All Projects", ApplicantProjectsView::new));
        options.add(new ViewOption("View My Applied Projects", ApplicantViewMyApplicationsView::new));
        options.add(new ViewOption("View My Enquiries", ApplicantViewMyEnquiriesView::new));
        options.add(new ViewOption("View My Booking Receipts", ApplicantReceiptsView::new));
        if (this.isRootView) {
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
