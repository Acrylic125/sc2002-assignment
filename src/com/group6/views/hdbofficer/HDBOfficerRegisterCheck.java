package com.group6.views.hdbofficer;

import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.HDBOfficer;
import com.group6.users.User;
import com.group6.views.*;
import com.group6.views.applicant.ApplicantHomeView;
import com.group6.views.applicant.ApplicantViewMyApplicationsView;

public class HDBOfficerRegisterCheck implements AuthenticatedView {
    
    private ViewContext ctx;
    private User user;

    @Override
    public boolean isAuthorized(User user) {
        return user instanceof HDBOfficer;
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;
        return registerCheck();
    }

    private View registerCheck() {
        final Scanner scanner = ctx.getScanner();
    
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        // make sure projectOpt actually exists:
        while(true){
            System.out.print("Enter the Project ID of the project that you want to apply to: ");
            final String projectId = scanner.nextLine().trim();
            
            try {
                // try registration using requestRegisterOfficer
                projectManager.requestRegisterOfficer(projectId, user.getId());
                System.out.println("Registration successful!");
                return null; // return null if registration is successful
            } catch (RuntimeException e) {
                // continue if any error:
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again with a valid Project ID.");
            }
        }
        
    }

}
