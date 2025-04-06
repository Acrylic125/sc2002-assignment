package com.group6.views.hdbofficer;

import java.util.Scanner;

import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.AuthenticatedView;
import com.group6.views.*;
import com.group6.views.applicant.*;

public class HDBOfficerHomeView implements AuthenticatedView{
    private ViewContext ctx;

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

        while (true) {
            System.out.println("HDB Officer Home Menu");
            System.out.println("1. Go to Applicant Portal (view applications and enquire)");
            System.out.println("2. Go to Manager Portal (Answer enquiries, Register for projects, Manage approved Projects)");
            System.out.println("3. Logout");
            System.out.println("");
            System.out.println("Type the number of the option you want to select.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new ApplicantHomeView();
                case "2":
                    return new HDBOfficerManageView();
                case "3":
                    System.out.println(BashColors.format("Logged out!", BashColors.GREEN));
                    ctx.setUser(null);
                    return new MenuView();
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

}
