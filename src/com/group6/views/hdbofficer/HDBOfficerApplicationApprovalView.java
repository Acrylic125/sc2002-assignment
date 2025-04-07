package com.group6.views.hdbofficer;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import com.group6.btoproject.BTOApplication;
import com.group6.btoproject.BTOApplicationStatus;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.ViewContext;
import com.group6.views.View;

public class HDBOfficerApplicationApprovalView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private final BTOProject project;

    private List<BTOApplication> applications;
    private ViewContext ctx;
    private int page = 1;

    public HDBOfficerApplicationApprovalView(BTOProject project) {
        this.project = project;
        this.applications = project.getApplications();
    }

    @Override
    public int getLastPage() {
        int size = applications.size();
        if (size % PAGE_SIZE == 0) {
            return size / PAGE_SIZE;
        }
        return size / PAGE_SIZE + 1;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return page;
    }

    private String stringifyApplicationStatus(BTOApplicationStatus status) {
        switch (status) {
            case PENDING:
                return BashColors.format("Pending", BashColors.YELLOW);
            case BOOKED:
                return BashColors.format("Booked", BashColors.BLUE);
            case SUCCESSFUL:
                return BashColors.format("Successful", BashColors.GREEN);
            case UNSUCCESSFUL:
                return BashColors.format("Unsuccessful", BashColors.RED);
        }
        return BashColors.format("(Unknown)", BashColors.LIGHT_GRAY);
    }

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canApproveApplications();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        return showOptions();
    }

    private void showApplications() {
        final UserManager userManager = ctx.getBtoSystem().getUsers();
        System.out.println(BashColors.format(
                "Applications for " + project.getName() + ", " + project.getNeighbourhood(), BashColors.BOLD));
        System.out.println("Application ID | Applicant | Status");
        final int lastIndex = Math.min(page * PAGE_SIZE, applications.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            BTOApplication application = applications.get(i);
            Optional<User> userOpt = userManager.getUser(application.getApplicantUserId());
            String userStr = userOpt.map(User::getName).orElse(BashColors.format("(Unknown)", BashColors.LIGHT_GRAY));
            String statusStr = stringifyApplicationStatus(application.getStatus());
            System.out.println(application.getId() + " | " + userStr + " ("
                    + BashColors.format(application.getApplicantUserId(), BashColors.LIGHT_GRAY) + ")" + " | "
                    + statusStr);
        }
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            showApplications();
            System.out.println("Page " + page + " / " + getLastPage() +
                    " - Type 's' to modify status, 'n' to go to next page, 'p' to go to previous page, 'page' to go to a specific page,  or leave empty ('') to go back:");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "s":
                    showRequestStatusChange();
                    break;
                case "n":
                    this.requestNextPage(scanner);
                    break;
                case "p":
                    this.requestPrevPage(scanner);
                    break;
                case "page":
                    this.requestPage(scanner);
                    break;
                case "":
                    return null;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private void showRequestStatusChange() {
        Optional<BTOApplication> applicationOpt = requestApplication();
        if (applicationOpt.isEmpty()) {
            return;
        }

        BTOApplication application = applicationOpt.get();
        Optional<BTOApplicationStatus> statusOpt = requestApplicationStatus(application);
        if (statusOpt.isEmpty()) {
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        final Scanner scanner = ctx.getScanner();

        Optional<Throwable> transitionErrOpt = Utils.tryCatch(() -> {
            projectManager.transitionApplicationStatus(project.getId(), application.getId(),
                    statusOpt.get());
        }).getErr();
        if (transitionErrOpt.isPresent()) {
            System.out.println(BashColors.format("Failed to update application status.", BashColors.RED));
            System.out.println(BashColors.format(
                    transitionErrOpt.get().getMessage(), BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }
        System.out.println(BashColors.format("Successfully updated application status.", BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private Optional<BTOApplication> requestApplication() {
        final Scanner scanner = ctx.getScanner();

        while (true) {
            System.out.println(BashColors.format(
                    "Enter ID of Application you want to modify or leave empty ('') to cancel: ", BashColors.BOLD));
            String appId = scanner.nextLine().trim();
            if (appId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOApplication> applicationOpt = project.getApplication(appId);
            if (applicationOpt.isEmpty()) {
                System.out.println(BashColors.format("Application not found.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return applicationOpt;
        }
    }

    private Optional<BTOApplicationStatus> requestApplicationStatus(BTOApplication application) {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        final BTOApplicationStatus[] allStatuses = BTOApplicationStatus.values();
        final Set<BTOApplicationStatus> validStatuses = projectManager
                .getValidApplicationStateTransitions(application.getStatus());

        while (true) {
            System.out.println(BashColors.format("What status to change to?", BashColors.BOLD));
            System.out.println("Available statuses:");
            for (BTOApplicationStatus status : allStatuses) {
                boolean isStatusTransitionValid = validStatuses.contains(status);

                if ((status == BTOApplicationStatus.SUCCESSFUL || status == BTOApplicationStatus.BOOKED)
                        && !project.isApplicationWindowOpen()) {
                    System.out.println("  "
                            + BashColors.format(status.toString() + " (Application window closed)", BashColors.RED));
                    continue;
                }
                if (!isStatusTransitionValid) {
                    System.out.println("  " + BashColors.format(status.toString(), BashColors.RED));
                    continue;
                }
                System.out.println("  " + status.toString());
            }

            System.out.println("Enter status (e.g. PENDING, SUCCESSFUL) to modify to or leave empty ('') to cancel: ");
            String _status = scanner.nextLine().trim().toUpperCase();
            if (_status.isEmpty()) {
                return Optional.empty();
            }

            TryCatchResult<BTOApplicationStatus, Throwable> statusRes = Utils
                    .tryCatch(() -> BTOApplicationStatus.valueOf(_status));
            if (statusRes.getErr().isPresent()) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            BTOApplicationStatus status = statusRes.getData().get();
            if (!validStatuses.contains(status)
                    || ((status == BTOApplicationStatus.SUCCESSFUL || status == BTOApplicationStatus.BOOKED)
                            && !project.isApplicationWindowOpen())) {
                System.out.println(BashColors.format("Invalid status.", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return Optional.of(status);
        }
    }

    // private Optional<String>

    // public View appApprovalView(){
    // BTOProjectManager manager = ctx.getBtoSystem().getProjects();
    //
    // while(true){
    // System.out.println("Enter ID of Project you want to check applications of:
    // ");
    // String projectid = sc.nextLine().trim();
    //
    // System.out.println("Enter ID of Application you want to modify: ");
    // String appid = sc.nextLine().trim();
    //
    // if(projectid.equals("N")){
    // return null;
    // }
    //
    // final Optional<BTOProject> projectOpt = manager.getProject(projectid);
    // if (projectOpt.isEmpty()) {
    // System.out.println("Project not found, enter again. Enter N under Project ID
    // to exit.");
    // continue;
    // }
    //
    // final BTOProject project = projectOpt.get();
    // final Optional<BTOApplication> applicationOpt =
    // project.getApplication(appid);
    // if (applicationOpt.isEmpty()) {
    // System.out.println("Application not found.");
    // continue;
    // }
    //
    // final BTOApplication application = applicationOpt.get();
    // final String status = application.getStatus().toString();
    //
    // while(true){
    // if(status.equals("SUCCESSFULL") ){
    // System.out.println("Available statuses: BOOKED or UNSUCCESSFUL, type here:");
    // String newstatus = sc.nextLine();
    //
    // if(newstatus.equals("SUCCESSFUL") || newstatus.equals("PENDING")){
    // System.out.println("Invalid update status, enter again");
    // continue;
    // }
    // else{
    // System.out.println("Status Successfully changed to " + newstatus);
    // if(newstatus == "BOOKED"){
    // manager.transitionApplicationStatus(projectid, appid,
    // BTOApplicationStatus.BOOKED);
    // }
    // else if(newstatus == "UNSUCCESSFUL"){
    // manager.transitionApplicationStatus(projectid, appid,
    // BTOApplicationStatus.UNSUCCESSFUL);
    // }
    // }
    // }
    // else if(status.equals("UNSUCCESSFUL")){
    // System.out.println("No changes can be made to this status, Exiting now");
    // break;
    // }
    // else if(status.equals("PENDING")){
    // System.out.println("Available statuses: SUCCESSFUL or UNSUCCESSFUL");
    // String newstatus = sc.nextLine();
    //
    // if(newstatus.equals("PENDING") || newstatus.equals("BOOKED")){
    // System.out.println("Invalid update status, enter again");
    // continue;
    // }
    // else{
    // System.out.println("Status Successfully changed to " + newstatus);
    // if(newstatus == "SUCCESSFULL"){
    // manager.transitionApplicationStatus(projectid, appid,
    // BTOApplicationStatus.SUCCESSFUL);
    // }
    // else if(newstatus == "UNSUCCESSFUL"){
    // manager.transitionApplicationStatus(projectid, appid,
    // BTOApplicationStatus.UNSUCCESSFUL);
    // }
    // }
    // }
    // else if(status.equals("BOOKED")){
    // System.out.println("No changes can be made to this status, Exiting now");
    // break;
    // }
    //
    //
    // }
    //
    //
    // return null;
    // }
    //
    //
    // }
}
