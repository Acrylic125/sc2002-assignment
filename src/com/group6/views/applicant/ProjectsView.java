package com.group6.views.applicant;

import java.util.*;

import com.group6.btoproject.BTOEnquiry;
import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectType;
import com.group6.users.User;
import com.group6.users.UserManager;
import com.group6.users.UserPermissions;
import com.group6.utils.BashColors;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.PaginatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;

import static java.util.stream.Collectors.toList;

public class ProjectsView implements PaginatedView, AuthenticatedView {

    private static final int PAGE_SIZE = 3;

    private ViewContext ctx;
    private User user;
    private int page = 1;
    private List<BTOProject> filteredProjects = new ArrayList<>();

    @Override
    public int getLastPage() {
        int size = filteredProjects.size();
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
    public View render(ViewContext ctx, User user) {
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();

        this.ctx = ctx;
        this.user = user;
        final List<BTOProject> projects = projectManager.getProjects().values().stream()
                .filter((project) -> {
                    final UserPermissions permissions = user.getPermissions();
                    if (!(project.isApplicationWindowOpen() || permissions.canViewClosedProjects())) {
                        return false;
                    }
                    return project.isVisibleToPublic() || permissions.canViewNonVisibleProjects();
                })
                .toList();
        // We ASSUME filters cannot be applied UNTIL the user goes to the filters view
        // and return which will call this.
        //
        // We just do it once here to avoid unnecessary filtering.
        this.filteredProjects = filters.applyFilters(projects, user);

        return this.showOptions();
    }

    private void showProjects() {
        List<BTOProject> projects = this.filteredProjects;
        System.out.println(BashColors.format("Projects", BashColors.BOLD));
        if (projects.isEmpty()) {
            System.out.println(BashColors.format("(No Projects Found)", BashColors.LIGHT_GRAY));
            System.out.println("");
            return;
        }

        final UserManager userManager = ctx.getBtoSystem().getUsers();

        final int lastIndex = Math.min(page * PAGE_SIZE, projects.size());
        final int firstIndex = (page - 1) * PAGE_SIZE;
        // Render the projects in the page.
        for (int i = firstIndex; i < lastIndex; i++) {
            final BTOProject project = projects.get(i);
            final List<BTOProjectType> types = new ArrayList<>(project.getProjectTypes());

            Optional<User> managerOpt = userManager.getUser(project.getManagerUserId());
            List<User> officers = project.getManagingOfficerRegistrations().stream()
                    .map((reg) -> {
                        return userManager.getUser(reg.getOfficerUserId());
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            String managerName = managerOpt.isPresent() ? managerOpt.get().getName() : "(Unknown)";
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
            System.out.println("Types (No. Units Available / Total No. Units / Price):");
            if (types.isEmpty()) {
                System.out.println("  (No types available)");
            } else {
                types.sort(Comparator.comparing(a -> a.getId().getName()));

                for (BTOProjectType type : types) {
                    System.out.println(
                            "  " + type.getId().getName() + ": " +
                                    project.getBookedCountForType(type.getId()) + " / " + type.getMaxQuantity()
                                    + " / $" + Utils.formatMoney(type.getPrice()));
                }
            }
            boolean isWindowOpen = project.isApplicationWindowOpen();
            String projectOpenWindowStr = Utils.formatToDDMMYYYY(project.getApplicationOpenDate())
                    + " to " + Utils.formatToDDMMYYYY(project.getApplicationCloseDate());
            System.out.println("Application period: " + BashColors.format(projectOpenWindowStr, isWindowOpen
                    ? BashColors.GREEN
                    : BashColors.RED));

            String officerSlotsStr = officers.size() + " / " + project.getOfficerLimit();
            System.out.println("Manager / Officers (" + officerSlotsStr + "): " + managerName + " / " + officerNames);
            if (user.getPermissions().canViewNonVisibleProjects()) {
                boolean isVisibleToPublic = project.isVisibleToPublic();
                System.out.println("Visible to public: " + BashColors.format(isVisibleToPublic ? "YES" : "NO",
                        isVisibleToPublic ? BashColors.GREEN : BashColors.RED));
            }
            System.out.println("");
        }
        System.out.println("Showing " + (lastIndex - firstIndex) + " of " + projects.size());
    }

    private View showOptions() {
        final Scanner scanner = ctx.getScanner();

        Utils.joinStringDelimiter(new ArrayList<>(), ", ", " or ");
        final List<String> options = new ArrayList<>();
        final UserPermissions permissions = user.getPermissions();
        if (permissions.canRespondEnquiries() || permissions.canApply()) {
            options.add("'e' to enquire");
        }
        if (permissions.canApply()) {
            options.add("'a' to apply");
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
            showProjects();
            System.out.println("Page " + page + " / " + getLastPage() + " - " + optionsStr + ":");

            String option = scanner.nextLine().trim();
            if (option.equals("f")) {
                return new ProjectsViewFiltersView();
            }
            if (option.equals("n")) {
                this.requestNextPage(scanner);
                break;
            }
            if (option.equals("p")) {
                this.requestPrevPage(scanner);
                break;
            }
            if (option.equals("page")) {
                this.requestPage(scanner);
                break;
            }
            if (option.isEmpty()) {
                return null;
            }
            if (option.equals("a") && permissions.canApply()) {
                return new ApplicantApplyProjectView();
            }
            if (option.equals("e") && (
                    permissions.canRespondEnquiries() || permissions.canApply())) {
                final Optional<BTOProject> projectOpt = this.showRequestProject();
                if (projectOpt.isEmpty()) {
                    break;
                }
                final BTOProject project = projectOpt.get();
                final int enquiryLevel = user.getPermissions().getRespondEnquiresLevel();
                final boolean canRespondToProject = (enquiryLevel == 1 && project.isManagingOfficer(user.getId())) || enquiryLevel == 2;
                final List<BTOEnquiry> enquiries = project.getEnquiries().stream()
                        .filter((enquiry) ->
                                canRespondToProject || enquiry.getSenderMessage().getSenderUserId().equals(user.getId()))
                        .toList();
                return new ApplicantProjectEnquiryView(project, enquiries, canRespondToProject);
            }
            System.out.println(BashColors.format("Invalid option.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
        }
        return null;
    }

    private Optional<BTOProject> showRequestProject() {
        final Scanner scanner = ctx.getScanner();
        final BTOProjectManager projectManager = ctx.getBtoSystem().getProjects();

        while (true) {
            System.out.println(BashColors.format(
                    "Type in the project id you or leave empty ('') to cancel:", BashColors.BOLD));
            final String projectId = scanner.nextLine().trim();
            if (projectId.isEmpty()) {
                return Optional.empty();
            }

            final Optional<BTOProject> projectOpt = projectManager.getProject(projectId);
            if (projectOpt.isEmpty()) {
                System.out.println(
                        BashColors.format("Project not found, please type in a valid project id.", BashColors.BOLD));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            return projectOpt;
        }
    }

}
