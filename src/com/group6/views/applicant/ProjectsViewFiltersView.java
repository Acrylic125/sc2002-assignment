package com.group6.views.applicant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.group6.BTOSystem;
import com.group6.users.HDBManager;
import com.group6.users.HDBOfficer;
import com.group6.users.User;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;
import com.group6.views.ViewSortType;

public class ProjectsViewFiltersView implements AuthenticatedView {

    private ViewContext ctx;
    private User user;

    private List<String> projectTypes = new ArrayList<>();

    private String stringifySortName(ViewSortType sortType) {
        switch (sortType) {
            case ASC:
                return "A-Z";
            case DSC:
                return "Z-A";
            default:
                return "None";
        }
    }

    @Override
    public View render(ViewContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        final BTOSystem btoSystem = ctx.getBtoSystem();
        this.projectTypes = btoSystem.getProjects().getAllProjectTypes();
        this.projectTypes.sort(String::compareTo);

        showOptions();

        return null;
    }

    private void showOptions() {
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();
        while (true) {
            String searchTermValue = filters.getSearchTerm();
            if (searchTermValue.isEmpty()) {
                searchTermValue = "(Empty)";
            }

            String locationValue = filters.getLocation();
            if (locationValue.isEmpty()) {
                locationValue = "(Empty)";
            }

            String projectTypesValue = this.projectTypes.stream()
                    .filter((filter) -> filters.getFilterAvailableProjectTypes().contains(filter))
                    .reduce("", (a, b) -> {
                        if (a.isEmpty()) {
                            return b;
                        }
                        return a + ", " + b;
                    });
            if (projectTypesValue.isEmpty()) {
                projectTypesValue = "(Empty)";
            }

            String sortTypeValue = stringifySortName(filters.getSortByName());

            boolean canFilterByManagedProjects = user instanceof HDBOfficer || user instanceof HDBManager;

            System.out.println("Filters");
            System.out.println("ID | Filter | Value");
            System.out.println("1 | Search Term | " + searchTermValue);
            System.out.println("2 | Location | " + locationValue);
            System.out.println("3 | Project Types with Availability | " + projectTypesValue);
            System.out.println("4 | Name Sort Type | " + sortTypeValue);
            if (canFilterByManagedProjects) {
                System.out.println("5 | Only Show Managed Projects | " + filters.isOnlyShowManagedProjects());
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
                    System.out.println("Invalid input.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    return;
            }
        }
    }

    private void showFilterBySearchTerm() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();
        System.out.println("Type the search term you want to filter, or leave empty (i.e. '') to not set one.  ");
        String searchTerm = scanner.nextLine().trim();
        filters.setSearchTerm(searchTerm);
        System.out.println("Search term set to: " + searchTerm);
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private void showFilterByLocation() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();
        System.out.println("Type the location you want to filter, or leave empty (i.e. '') to not set one.  ");
        String location = scanner.nextLine().trim();
        filters.setLocation(location);
        System.out.println("Location set to: " + location);
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private void showFilterByProjectType() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();
        if (projectTypes.isEmpty()) {
            System.out.println("No project types available.");
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        String projectTypesFilterRaw = "";
        while (true) {
            System.out.println("Project Types with Availability");
            System.out.println("Project Type ID | Filtering");
            for (String projectType : projectTypes) {
                System.out.println(projectType + " | " + (filters.getFilterAvailableProjectTypes().contains(projectType)));
            }
            System.out.println("");
            System.out.println("Type the flat type you want to filter, or leave empty ('') to not set one.");
            System.out.println("* You may specify multiple by leaving a ',' between entries(e.g. \"2 Room, 3 Room\") which will filter for projects with EITHER 2 Room or 3 Room availability.");

            projectTypesFilterRaw = scanner.nextLine().trim();
            if (projectTypesFilterRaw.isEmpty()) {
                break;
            }
            String[] projectTypesFilter = projectTypesFilterRaw.split(",");

            boolean areAllValid = true;
            Set<String> excludeProjectTypes = new HashSet<>();
            for (String projectType : projectTypesFilter) {
                projectType = projectType.trim();
                if (!projectTypes.contains(projectType)) {
                    areAllValid = false;
                    System.out.println("Invalid project type: " + projectType);
                } else {
                    excludeProjectTypes.add(projectType);
                }
            }
            if (!areAllValid) {
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            filters.setFilterAvailableProjectTypes(excludeProjectTypes);
            break;
        }

        System.out.println("Project Types set to: " + (projectTypesFilterRaw.isEmpty() ? "(Empty)" : projectTypesFilterRaw));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private void showNameSort() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();

        while (true) {
            String sortTypeValue = stringifySortName(filters.getSortByName());

            System.out.println("Sort by name");
            System.out.println("Currently sorting by: " + sortTypeValue);
            System.out.println("Sort Types:");
            System.out.println("  A-Z");
            System.out.println("  Z-A");
            System.out.println("  None");
            System.out.println("");
            System.out.println("Type the sort type you want to filter.");
            String sort = scanner.nextLine();

            switch (sort) {
                case "A-Z":
                    filters.setSortByName(ViewSortType.ASC);
                    break;
                case "Z-A":
                    filters.setSortByName(ViewSortType.DSC);
                    break;
                case "None":
                    filters.setSortByName(ViewSortType.NONE);
                    break;
                default:
                    System.out.println("Invalid input.");
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
            }
            break;
        }
        System.out.println("Project Types set to: " + stringifySortName(filters.getSortByName()));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    private void showFilterByManagedProjects() {
        final Scanner scanner = ctx.getScanner();
        final ProjectsViewFilters filters = ctx.getViewAllProjectsFilters();

        while (true) {
            System.out.println("Only Show Managed Projects");
            System.out.println("Currently only showing managed projects: " + filters.isOnlyShowManagedProjects());
            System.out.println("Type 'y' to only show managed projects, 'n' to show all projects.");
            String showManagedProjects = scanner.nextLine().trim();
            if (!showManagedProjects.equals("y") && !showManagedProjects.equals("n")) {
                System.out.println("Invalid input.");
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            filters.setOnlyShowManagedProjects(showManagedProjects.equals("y"));

            System.out.println("Only Show Managed Projects set to: " + filters.isOnlyShowManagedProjects());
            break;
        }
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

}
