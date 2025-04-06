package com.group6.views.hdbofficer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.AuthenticatedView;
import com.group6.views.*;
import com.group6.views.applicant.*;

public class HDBOfficerHomeView implements AuthenticatedView{
    private final boolean isRootView;

    private ViewContext ctx;

    public HDBOfficerHomeView(boolean isRootView) {
        this.isRootView = isRootView;
    }

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canManageProjects();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        List<ViewOption> options = new ArrayList<>();
        options.add(new ViewOption(
                "Go to Applicant Portal (" + BashColors.format("View applications and enquire", BashColors.LIGHT_GRAY) + ")",
                () -> new ApplicantHomeView(false)
        ));
        options.add(new ViewOption(
                "Go to Manager Portal (" + BashColors.format("Answer enquiries, Register for projects, Manage approved Projects", BashColors.LIGHT_GRAY) + ")",
                () -> new HDBOfficerManageView(false)
        ));
        options.add(new ViewOption(
                "Logout",
                () -> {
                    logout();
                    return new MenuView();
                }
        ));

        while (true) {
            System.out.println(BashColors.format("Choose a dashboard to view", BashColors.BOLD));
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

    private void logout() {
        ctx.clearViewStack();
        ctx.setUser(null);
        System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
    }

}
