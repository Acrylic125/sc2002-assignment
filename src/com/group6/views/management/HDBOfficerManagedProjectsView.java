package com.group6.views.management;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;
import com.group6.views.applicant.ProjectEnquiryView;

import java.util.*;

public class HDBOfficerManagedProjectsView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private int page = 1;
    private ViewContext ctx;
    private User user;
    private List<BTOProject> managedProjects = new ArrayList<>();

    @Override
    public int getLastPage() {
        int size = managedProjects.size();
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

    @Override
    public boolean isAuthorized(User user) {
        return user.getPermissions().canManageProjects();
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        this.managedProjects = new ArrayList<>(projectManager.getManagingProjects(user.getId()));
        this.managedProjects.sort(Comparator.comparing(BTOProject::getName));

        return showOptions();
    }

    private void showManagedProjects() {
        final UserManager userManager = ctx.getBtoSystem().getUserManager();

        System.out.println(BashColors.format("My Managed Projects", BashColors.BOLD));
        if (managedProjects.isEmpty()) {
            System.out.println(BashColors.format("(No managed projects found)", BashColors.LIGHT_GRAY));
            return;
        }
        final int lastIndex = Math.min(page * PAGE_SIZE, managedProjects.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOProject project = managedProjects.get(i);

            Optional<User> managerOpt = userManager.getUser(project.getManagerUserId());
            List<User> officers = project.getManagingOfficerRegistrations().stream()
                    .map((reg) -> {
                        return userManager.getUser(reg.getOfficerUserId());
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            String managerName = managerOpt.isPresent() ? managerOpt.get().getName()
                    : BashColors.format("(Unknown)", BashColors.LIGHT_GRAY);
            String officerNames = !officers.isEmpty()
                    ? officers.stream().map(User::getName).reduce((a, b) -> {
                        if (a.isEmpty()) {
                            return b;
                        }
                        return a + ", " + b;
                    }).get()
                    : BashColors.format("(None)", BashColors.LIGHT_GRAY);

            System.out.println("Project: " + project.getName() + ", " + project.getNeighbourhood());
            System.out.println("ID: " + project.getId());
            boolean isWindowOpen = project.isApplicationWindowOpen();
            String projectOpenWindowStr = Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate());
            System.out.println("Application and Registration period: " + BashColors.format(projectOpenWindowStr,
                    isWindowOpen
                            ? BashColors.GREEN
                            : BashColors.RED));
            String officerSlotsStr = officers.size() + " / " + project.getOfficerLimit();
            if (officers.size() >= project.getOfficerLimit()) {
                officerSlotsStr = BashColors.format(officerSlotsStr, BashColors.RED);
            }
            System.out.println("Manager / Officers (" + officerSlotsStr + "): " + managerName + " / " + officerNames);
            System.out.println("");
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + managedProjects.size());
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();
        final List<String> options = new ArrayList<>();
        final UserPermissions permissions = user.getPermissions();
        if (permissions.canApproveApplications()) {
            options.add("'a' to view Applications");
        }
        if (permissions.canApproveWithdrawal()) {
            options.add("'w' to view Withdrawals");
        }
        if (permissions.canApproveOfficerRegistrations()) {
            options.add("'r' to view Officer Registrations");
        }
        if (permissions.canRespondEnquiries()) {
            options.add("'e' to view Enquiries");
        }
        if (permissions.canEditProject()) {
            options.add("'edit' to view Enquiries");
        }
        if (permissions.canDeleteProject()) {
            options.add("'delete' to view Enquiries");
        }
        options.add("'n' to go to next page");
        options.add("'p' to go to previous page");
        options.add("'page' to go to a specific page");
        options.add("leave empty ('') to go back");
        final String optionsStr = Utils.joinStringDelimiter(
                options,
                ", ",
                " or ");

        while (true) {
            showManagedProjects();
            System.out.println("Page " + page + " / " + getLastPage() + " - " + optionsStr + ":");

            String option = scanner.nextLine().trim();
            switch (option) {
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
                case "e":
                    if (permissions.canRespondEnquiries()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }
                        final BTOProject project = projectOpt.get();
                        return new ProjectEnquiryView(project, project::getEnquiries, true);
                    }
                case "w":
                    if (permissions.canApproveWithdrawal()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }

                        return new HDBOfficerWithdrawalApprovalView(projectOpt.get());
                    }
                case "r":
                    if (permissions.canApproveOfficerRegistrations()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }

                        return new OfficerRegistrationApprovalView(projectOpt.get());
                    }
                case "a":
                    if (permissions.canApproveApplications()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }

                        return new HDBOfficerApplicationApprovalView(projectOpt.get());
                    }
                case "edit":
                    if (permissions.canEditProject()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }

                        return new EditProjectView(projectOpt.get());
                    }
                case "delete":
                    if (permissions.canDeleteProject()) {
                        Optional<BTOProject> projectOpt = requestProject();
                        if (projectOpt.isEmpty()) {
                            break;
                        }
                        showDeleteProject(projectOpt.get());
                    }
                    break;
                default:
                    System.out.println(BashColors.format("Invalid option.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
            }
        }
    }

    private Optional<BTOProject> requestProject() {
        final Scanner scanner = ctx.getScanner();
        while (true) {
            System.out.println(BashColors.format(
                    "Enter ID of Project or leave empty ('') to cancel: ",
                    BashColors.BOLD));
            String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return Optional.empty();
            }

            final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();
            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println(BashColors.format(
                        "Project not found.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            BTOProject project = projectOpt.get();
            if (!project.isManagedBy(user.getId())) {
                System.out.println(BashColors.format(
                        "You are not managing this project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            return projectOpt;
        }
    }

    private void showDeleteProject(BTOProject project) {
        final Scanner scanner = ctx.getScanner();
        if (!project.getManagerUserId().equals(user.getId())) {
            System.out.println(BashColors.format(
                    "You are not the manager of this project.",
                    BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjectManager();

        while (true) {
            System.out.println(BashColors.format(
                    "Are you sure you want to delete this project?",
                    BashColors.RED, BashColors.BOLD));
            System.out.println("Type in the project id, \"" + project.getId() + "\" or leave empty ('') to cancel:");
            System.out.println(BashColors.format("Note: This cannot be reverted!", BashColors.LIGHT_GRAY));
            String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return;
            }
            if (!projectId.equals(project.getId())) {
                System.out.println(BashColors.format(
                        "Project ID does not match.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            if (Utils.tryCatch(() -> {
                projectManager.deleteProject(project.getId());
            }).getErr().isPresent()) {
                System.out.println(BashColors.format(
                        "Failed to delete project.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            System.out.println(BashColors.format("Project has been deleted.", BashColors.GREEN));
            // Refresh the managed projects list.
            this.managedProjects = projectManager.getManagingProjects(user.getId());
            return;
        }
    }

}
