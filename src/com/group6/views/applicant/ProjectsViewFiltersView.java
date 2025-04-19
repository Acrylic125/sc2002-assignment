package com.group6.views.applicant;

import java.util.*;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProjectTypeID;
import com.group6.users.User;
import com.group6.utils.BashColors;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;
import com.group6.views.ViewSortType;

/**
 * View for the applicant to filter the projects view.
 */
public class ProjectsViewFiltersView implements AuthenticatedView {

    protected ViewContext ctx;
    protected User user;

    protected List<BTOProjectTypeID> projectTypes = new ArrayList<>();

    /**
     * @return the filters for the projects view
     */
    protected ProjectsViewFilters getProjectFilters() {
        return ctx.getViewAllProjectsFilters();
    }

    /**
     * Stringify the sort name.
     *
     * @param sortType the sort type
     * @return the stringified sort name
     */
    protected String stringifySortName(ViewSortType sortType) {
        switch (sortType) {
            case ASC:
                return "A-Z";
            case DSC:
                return "Z-A";
            default:
                return BashColors.format("None", BashColors.LIGHT_GRAY);
        }
    }

    /**
     * Stringify the search term.
     *
     * @param searchTerm the search term
     * @return the stringified search term
     */
    protected String stringifySearchTerm(String searchTerm) {
        if (searchTerm.isEmpty()) {
            return BashColors.format("(Empty)", BashColors.LIGHT_GRAY);
        }
        return searchTerm;
    }

    /**
     * Stringify the location.
     *
     * @param location the location
     * @return the stringified location
     */
    protected String stringifyLocation(String location) {
        if (location.isEmpty()) {
            return BashColors.format("(Empty)", BashColors.LIGHT_GRAY);
        }
        return location;
    }

    /**
     * Stringify the project types availability.
     *
     * @param projectTypes the project types
     * @return the stringified project types availability
     */
    protected String stringifyProjectTypesAvailability(Collection<BTOProjectTypeID> projectTypes) {
        String value = this.projectTypes.stream()
                .filter(projectTypes::contains)
                .map(BTOProjectTypeID::getName)
                .reduce("", (a, b) -> {
                    if (a.isEmpty()) {
                        return b;
                    }
                    return a + ", " + b;
                });
        if (value.isEmpty()) {
            return BashColors.format("(Empty)", BashColors.LIGHT_GRAY);
        }
        return value;
    }

    /**
     * Stringify the show only managed projects.
     *
     * @param showOnlyManagedProjects the show only managed projects
     * @return the stringified show only managed projects
     */
    protected String stringifyShowOnlyManagedProjects(boolean showOnlyManagedProjects) {
        if (showOnlyManagedProjects) {
            return BashColors.format("YES", BashColors.GREEN);
        }
        return BashColors.format("NO", BashColors.RED);
    }

    /**
     * View renderer.
     *
     * @param ctx  view context
     * @param user authenticated user
     * @return next view
     */
    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final BTOSystem btoSystem = ctx.getBtoSystem();
        this.projectTypes = new ArrayList<>(btoSystem.getProjectManager().getAllProjectTypes());
        this.projectTypes.sort(Comparator.comparing(BTOProjectTypeID::getName));
        showOptions();
        return null;
    }

    /**
     * Show the options for the projects view filters.
     */
    protected void showOptions() {
        final ProjectsViewFilters filters = getProjectFilters();
        while (true) {
            String searchTermValue = stringifySearchTerm(filters.getSearchTerm());
            String locationValue = stringifyLocation(filters.getLocation());
            String projectTypesValue = stringifyProjectTypesAvailability(filters.getFilterAvailableProjectTypes());
            String sortTypeValue = stringifySortName(filters.getSortByName());
            String showOnlyManagedProjectsValue = stringifyShowOnlyManagedProjects(filters.isOnlyShowManagedProjects());

            boolean canFilterByManagedProjects = user.getPermissions().canManageProjects();

            System.out.println(BashColors.format("Filters", BashColors.BOLD));
            System.out.println("ID | Filter | Value");
            System.out.println("1 | Search Term | " + searchTermValue);
            System.out.println("2 | Location | " + locationValue);
            System.out.println("3 | Project Types with Availability | " + projectTypesValue);
            System.out.println("4 | Name Sort Type | " + sortTypeValue);
            if (canFilterByManagedProjects) {
                System.out.println("5 | Only Show Managed Projects | " + showOnlyManagedProjectsValue);
            }
            System.out.println("");
            System.out.println("Type the ID of the filter you want to change, or leave empty ('') to go back.");

            Scanner scanner = ctx.getScanner();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "":
                    return;
                case "1":
                    showFilterBySearchTerm();
                    break;
                case "2":
                    showFilterByLocation();
                    break;
                case "3":
                    showFilterByProjectType();
                    break;
                case "4":
                    showNameSort();
                    break;
                case "5":
                    if (canFilterByManagedProjects) {
                        showFilterByManagedProjects();
                        break;
                    }
                default:
                    System.out.println(BashColors.format("Invalid input.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    return;
            }
        }
    }

    /**
     * Show the filter by search term.
     */
    protected void showFilterBySearchTerm() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = this.getProjectFilters();
        System.out.println(
                BashColors.format("Type the search term you want to filter, or leave empty (i.e. '') to not set one.  ",
                        BashColors.BOLD));
        String searchTerm = scanner.nextLine().trim();
        filters.setSearchTerm(searchTerm);
        System.out
                .println(BashColors.format("Search term set to: " + stringifySearchTerm(searchTerm), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the filter by location.
     */
    protected void showFilterByLocation() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = this.getProjectFilters();
        System.out.println(BashColors.format(
                "Type the location you want to filter, or leave empty (i.e. '') to not set one.  ", BashColors.BOLD));
        String location = scanner.nextLine().trim();
        filters.setLocation(location);
        System.out.println(BashColors.format("Location set to: " + stringifyLocation(location), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the filter by project type.
     */
    protected void showFilterByProjectType() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = this.getProjectFilters();
        if (projectTypes.isEmpty()) {
            System.out.println(BashColors.format("No project types available.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        String projectTypesFilterRaw = "";
        final String currentProjectTypesFilterValue = stringifyProjectTypesAvailability(
                filters.getFilterAvailableProjectTypes());
        while (true) {
            System.out.println(BashColors.format("Project Types with Availability", BashColors.BOLD));
            System.out.println("Currently filtering by: " + currentProjectTypesFilterValue);
            System.out.println("Project Type ID:");
            for (BTOProjectTypeID projectType : projectTypes) {
                System.out.println("  " + projectType.getName());
            }
            System.out.println("");
            System.out.println("Type the project type you want to filter, or leave empty ('') to not set one.");
            System.out.println(BashColors.format(
                    "NOTE: You may specify multiple by leaving a ',' between entries(e.g. \"2 Room, 3 Room\") which will filter for projects with EITHER 2 Room or 3 Room availability.",
                    BashColors.LIGHT_GRAY));

            projectTypesFilterRaw = scanner.nextLine().trim();
            if (projectTypesFilterRaw.isEmpty()) {
                filters.setFilterAvailableProjectTypes(new HashSet<>());
                break;
            }
            String[] projectTypesFilter = projectTypesFilterRaw.split(",");

            boolean areAllValid = true;
            Set<BTOProjectTypeID> newProjectTypeFilters = new HashSet<>();
            final Map<String, BTOProjectTypeID> projectNameToTypeIDMap = new HashMap<>(this.projectTypes.size());
            for (BTOProjectTypeID projectType : this.projectTypes) {
                projectNameToTypeIDMap.put(projectType.getName().toLowerCase(), projectType);
            }

            for (String _projectType : projectTypesFilter) {
                _projectType = _projectType.trim().toLowerCase();
                BTOProjectTypeID projectType = projectNameToTypeIDMap.get(_projectType);

                if (projectType == null) {
                    areAllValid = false;
                    System.out.println(BashColors.format("Invalid project type: " + _projectType, BashColors.RED));
                } else {
                    newProjectTypeFilters.add(projectType);
                }
            }
            if (!areAllValid) {
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            filters.setFilterAvailableProjectTypes(newProjectTypeFilters);
            break;
        }

        System.out.println(BashColors.format("Project Types Availability set to: "
                + stringifyProjectTypesAvailability(filters.getFilterAvailableProjectTypes()), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the name sort.
     */
    protected void showNameSort() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = this.getProjectFilters();

        while (true) {
            String sortTypeValue = stringifySortName(filters.getSortByName());

            System.out.println(BashColors.format("Name Sort Type", BashColors.BOLD));
            System.out.println("Currently sorting by: " + sortTypeValue);
            System.out.println("Sort Types:");
            System.out.println("  A-Z");
            System.out.println("  Z-A");
            System.out.println("  None");
            System.out.println();
            System.out.println("Type the sort type you want to filter.");
            String sort = scanner.nextLine().trim().toUpperCase();

            switch (sort) {
                case "A-Z":
                    filters.setSortByName(ViewSortType.ASC);
                    break;
                case "Z-A":
                    filters.setSortByName(ViewSortType.DSC);
                    break;
                case "NONE":
                    filters.setSortByName(ViewSortType.NONE);
                    break;
                default:
                    System.out.println(BashColors.format("Invalid sort type: " + sort, BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
            }
            break;
        }
        System.out.println(BashColors.format("Project Types set to: " + stringifySortName(filters.getSortByName()),
                BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the filter by managed projects.
     */
    protected void showFilterByManagedProjects() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = this.getProjectFilters();

        while (true) {
            System.out.println(BashColors.format("Only Show Managed Projects", BashColors.BOLD));
            System.out.println("Currently only showing managed projects: "
                    + stringifyShowOnlyManagedProjects(filters.isOnlyShowManagedProjects()));
            System.out.println("Type 'y' to only show managed projects, 'n' to show all projects.");
            String showManagedProjects = scanner.nextLine().trim();
            if (!showManagedProjects.equals("y") && !showManagedProjects.equals("n")) {
                System.out.println(BashColors.format("Invalid input: " + showManagedProjects, BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            filters.setOnlyShowManagedProjects(showManagedProjects.equals("y"));

            System.out.println(BashColors.format(
                    "Only Show Managed Projects set to: " + filters.isOnlyShowManagedProjects(), BashColors.GREEN));
            break;
        }
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

}
