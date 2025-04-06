package com.group6.views.hdbofficer;

import java.util.Scanner;

import com.group6.users.User;
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
            System.out.println("");
            System.out.println("Type the number of the option you want to select or leave empty ('') to cancel.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new ApplicantHomeView();
                case "2":
                    return new HDBOfficerManageView(); 
                case "":
                    return null;
                default:
                    System.out.println("Invalid option.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

}
