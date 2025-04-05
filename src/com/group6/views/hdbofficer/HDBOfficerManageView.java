package com.group6.views.hdbofficer;

import java.util.Scanner;

import com.group6.users.Applicant;
import com.group6.users.User;
import com.group6.users.HDBOfficer;
import com.group6.views.AuthenticatedView;
import com.group6.views.*;
import com.group6.views.applicant.*;

public class HDBOfficerManageView implements AuthenticatedView{
    private ViewContext ctx;

    @Override
    public boolean isAuthorized(User user) {
        return user instanceof HDBOfficer;
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;

        return showOptions();
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println("HDB Manage Home Menu");
            System.out.println("1. Register to Manage Project");
            System.out.println("2. View Registrations");
            System.out.println("3. Manage Approved Projects (Approve Applications, Answer Enqueries)");
            System.out.println("");
            System.out.println("Type the number of the option you want to select or leave empty ('') to cancel.");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    return new HDBOfficerProjectsView(); //call requestOfficer in BTO Project Manager from a BTOProjectManager object
                case "2":
                    return new ApplicantViewMyApplicationsView(); 
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
