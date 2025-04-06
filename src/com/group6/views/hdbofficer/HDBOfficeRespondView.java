package com.group6.views.hdbofficer;

import java.util.List;

import com.group6.btoproject.BTOEnquiry;
import com.group6.btoproject.BTOEnquiryMessage;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.views.AuthenticatedView;
import com.group6.views.ViewContext;
import com.group6.views.View;

public class HDBOfficeRespondView implements AuthenticatedView{
    private ViewContext ctx;
    private User user;
    private final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

    // Get all projects managed by the officer
    private List<BTOProject> managedProjects = projectManager.getOfficerManagingProjects(user.getId());

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canRespondEnquiries();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        return respondView();
    }

    public View respondView(){
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        // Get all projects managed by the officer
        List<BTOProject> managedProjects = projectManager.getOfficerManagingProjects(user.getId());

        if (managedProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return null;
        }

        System.out.println("Enquiries for projects you are managing:");
        for (BTOProject project : managedProjects) {
            System.out.println("Project Name: " + project.getName());
            System.out.println("Project ID: " + project.getId());
            System.out.println("Enquiries:");
            List<BTOEnquiry> enquiries = project.getEnquiries();
            if (enquiries.isEmpty()) {
                System.out.println("  No enquiries for this project.");
            } else {
                for (BTOEnquiry enquiry : enquiries) {
                    System.out.println("  Enquiry ID: " + enquiry.getId());
                    System.out.println("  Enquiry Message: " + enquiry.getSenderMessage());
                    System.out.println("  Submitted By: " + enquiry.getId());
                    System.out.println("  --------------------------------");
                }
            }
            System.out.println("--------------------------------");
        }
        System.out.println("Would you like to respond to any enquiry? [y/n]");
        String input = ctx.getScanner().nextLine().trim();
        switch(input){
            case "y":
                responseLogic();
                return null;
            case "n":
                return null;
            default:
                System.out.println("Invalid input, returning to previous task");
                return null;
        }
    }

    private void responseLogic(){
        System.out.println("Enter the ID of Enquiry you would like to answer: ");
        String enquiryid = ctx.getScanner().nextLine().trim();
        if(enquiryid.equals("")){
            System.out.println("No id input, trying again. Press n if you want to exit");
            responseLogic();
        }
        if (enquiryid.equals("N")) {
            return;
        }
        else{
            BTOEnquiry selectedEnquiry = null;
            for (BTOProject project : managedProjects) {
                for (BTOEnquiry enquiry : project.getEnquiries()) {
                    if (enquiry.getId().equals(enquiryid)) {
                        selectedEnquiry = enquiry;
                        break;
                    }
                }
                if (selectedEnquiry != null) {
                    break;
                }
            }

            if (selectedEnquiry == null) {
                System.out.println("Invalid Enquiry ID. Please try again.");
                responseLogic();; // Restart the process
            }

            // Prompt officer to input their response
            System.out.println("Enter your response to the enquiry:");
            String response = ctx.getScanner().nextLine().trim();

            BTOEnquiryMessage responseMessage = new BTOEnquiryMessage(enquiryid, response);

            // Update the enquiry with the officer's response
            selectedEnquiry.setResponseMessage(responseMessage);
            System.out.println("Response submitted successfully!");
        }
    }
}
