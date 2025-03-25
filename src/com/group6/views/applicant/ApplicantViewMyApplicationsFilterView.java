package com.group6.views.applicant;

import com.group6.btoproject.BTOApplicationStatus;

import com.group6.users.HDBManager;
import com.group6.users.HDBOfficer;

import java.util.*;

/**
 * An extended view of {@link ProjectsViewFiltersView}.
 * Similar layout, just adding on.
 */
public class ApplicantViewMyApplicationsFilterView extends ProjectsViewFiltersView {

    @Override
    protected ProjectsViewFilters getProjectFilters() {
        return getApplicationFilters().getProjectsViewFilters();
    }

    protected ApplicantViewMyApplicationFilters getApplicationFilters() {
        return ctx.getViewApplicationFilters();
    }

    protected String stringifyApplicationStatuses(Collection<BTOApplicationStatus> statuses) {
        String value = "";
        for (BTOApplicationStatus status : statuses) {
            if (value.isEmpty()) {
                value = status.toString();
            } else {
                value = value + ", " + status.toString();
            }
        }
        if (value.isEmpty()) {
            return "(All)";
        }
        return value;
    }

    @Override
    protected void showOptions() {
        final ApplicantViewMyApplicationFilters applicationFilters = getApplicationFilters();
        final ProjectsViewFilters projectFilters = getProjectFilters();
        while (true) {
            String searchTermValue = stringifySearchTerm(projectFilters.getSearchTerm());
            String locationValue = stringifyLocation(projectFilters.getLocation());
            String projectTypesValue = stringifyProjectTypesAvailability(projectFilters.getFilterAvailableProjectTypes());
            String sortTypeValue = stringifySortName(projectFilters.getSortByName());
            String applicationStatusValue = stringifyApplicationStatuses(applicationFilters.getApplicationStatusesFilter());

            boolean canFilterByManagedProjects = user instanceof HDBOfficer || user instanceof HDBManager;

            System.out.println("Filters");
            System.out.println("ID | Filter | Value");
            System.out.println("1 | Search Term | " + searchTermValue);
            System.out.println("2 | Location | " + locationValue);
            System.out.println("3 | Project Types with Availability | " + projectTypesValue);
            System.out.println("4 | Name Sort Type | " + sortTypeValue);
            System.out.println("5 | Application Status | " + applicationStatusValue);
            if (canFilterByManagedProjects) {
                System.out.println("6 | Only Show Managed Projects | " + projectFilters.isOnlyShowManagedProjects());
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
                    showFilterApplicationStatuses();
                    break;
                case "6":
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

    protected void showFilterApplicationStatuses() {
        final Scanner scanner = ctx.getScanner();
        final ApplicantViewMyApplicationFilters filters = this.getApplicationFilters();

        final BTOApplicationStatus[] statuses = BTOApplicationStatus.values();
        final String applicationStatusesValue = stringifyApplicationStatuses(filters.getApplicationStatusesFilter());
        while (true) {
            System.out.println("Filter Application Statuses");
            System.out.println("Currently filtering: " + applicationStatusesValue);
            System.out.println("Application Statuses:");
            for (BTOApplicationStatus value : statuses) {
                System.out.println("  " + value.toString().toUpperCase());
            }
            System.out.println("");
            System.out.println("Type the application status type you want to filter, or leave empty ('') to not set one.");
            System.out.println("* You may specify multiple by leaving a ',' between entries(e.g. \"PENDING, BOOKED\") which will filter for projects with EITHER pending or booked application statuses.");

            String statusFilterRaw = scanner.nextLine().trim();
            if (statusFilterRaw.isEmpty()) {
                break;
            }
            String[] statusFilter = statusFilterRaw.split(",");

            boolean areAllValid = true;
            final Set<BTOApplicationStatus> newStatusFilters = new HashSet<>();
            for (String _status : statusFilter) {
                final String status = _status.trim().toUpperCase();
                Optional<BTOApplicationStatus> statusOpt = Arrays.stream(statuses)
                        .filter((s) -> s.toString().toUpperCase().equals(status))
                        .findFirst();
                if (statusOpt.isEmpty()) {
                    areAllValid = false;
                    System.out.println("Invalid application status: " + status);
                } else {
                    newStatusFilters.add(statusOpt.get());
                }
            }
            if (!areAllValid) {
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            filters.setApplicationStatusesFilter(newStatusFilters);
            break;
        }
        System.out.println("Application Statuses set to: " + stringifyApplicationStatuses(filters.getApplicationStatusesFilter()));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

}
