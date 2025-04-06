package com.group6.views.hdbofficer;

import java.util.Optional;
import java.util.Scanner;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOApplicationStatus;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.views.AuthenticatedView;
import com.group6.views.ViewContext;
import com.group6.views.View;

public class HDBOfficerAppApprovalView implements AuthenticatedView {
    private ViewContext ctx;
    private Scanner sc = ctx.getScanner();

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canApproveApplications();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        return appApprovalView();
    }

    public View appApprovalView(){
        BTOProjectManager manager = ctx.getBtoSystem().getProjects();

        while(true){
            System.out.println("Enter ID of Project you want to check applications of: ");
            String projectid = sc.nextLine().trim();
    
            System.out.println("Enter ID of Application you want to modify: ");
            String appid = sc.nextLine().trim();

            if(projectid.equals("N")){
                return null;
            }
    
            final Optional<BTOProject> projectOpt = manager.getProject(projectid);
            if (projectOpt.isEmpty()) {
                System.out.println("Project not found, enter again. Enter N under Project ID to exit.");
                continue;
            }
    
            final BTOProject project = projectOpt.get();
            final Optional<BTOApplication> applicationOpt = project.getApplication(appid);
            if (applicationOpt.isEmpty()) {
                System.out.println("Application not found.");
                continue;
            }
            
            final BTOApplication application = applicationOpt.get();
            final String status = application.getStatus().toString();

            while(true){
                if(status.equals("SUCCESSFULL") ){
                    System.out.println("Available statuses: BOOKED or UNSUCCESSFUL, type here:");
                    String newstatus = sc.nextLine();
                    
                    if(newstatus.equals("SUCCESSFUL") || newstatus.equals("PENDING")){
                        System.out.println("Invalid update status, enter again");
                        continue;
                    }
                    else{
                        System.out.println("Status Successfully changed to " + newstatus);
                        if(newstatus == "BOOKED"){
                            manager.transitionApplicationStatus(projectid, appid, BTOApplicationStatus.BOOKED);
                        }
                        else if(newstatus == "UNSUCCESSFUL"){
                            manager.transitionApplicationStatus(projectid, appid, BTOApplicationStatus.UNSUCCESSFUL);
                        }
                    }
                }
                else if(status.equals("UNSUCCESSFUL")){
                    System.out.println("No changes can be made to this status, Exiting now");
                    break;
                }
                else if(status.equals("PENDING")){
                    System.out.println("Available statuses: SUCCESSFUL or UNSUCCESSFUL");
                    String newstatus = sc.nextLine();

                    if(newstatus.equals("PENDING") || newstatus.equals("BOOKED")){
                        System.out.println("Invalid update status, enter again");
                        continue;
                    }
                    else{
                        System.out.println("Status Successfully changed to " + newstatus);
                        if(newstatus == "SUCCESSFULL"){
                            manager.transitionApplicationStatus(projectid, appid, BTOApplicationStatus.SUCCESSFUL);
                        }
                        else if(newstatus == "UNSUCCESSFUL"){
                            manager.transitionApplicationStatus(projectid, appid, BTOApplicationStatus.UNSUCCESSFUL);
                        }
                    }
                }
                else if(status.equals("BOOKED")){
                    System.out.println("No changes can be made to this status, Exiting now");
                    break;
                }


            }
            
    
            return null;
        }

        
    }
}
