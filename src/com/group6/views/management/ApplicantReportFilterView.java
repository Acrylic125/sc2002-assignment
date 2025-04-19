package com.group6.views.management;

import com.group6.BTOSystem;
import com.group6.btoproject.BTOProjectTypeID;
import com.group6.users.User;
import com.group6.users.UserMaritalStatus;
import com.group6.utils.BashColors;
import com.group6.utils.TryCatchResult;
import com.group6.utils.Utils;
import com.group6.views.AuthenticatedView;
import com.group6.views.View;
import com.group6.views.ViewContext;
import com.group6.views.ViewSortType;
import com.group6.views.applicant.ProjectsViewFiltersView;

import java.util.*;

/**
 * An extended view of {@link ProjectsViewFiltersView}.
 * Similar layout, just adding on.
 */
public class ApplicantReportFilterView implements AuthenticatedView {

    protected ViewContext ctx;
    protected User user;

    protected List<BTOProjectTypeID> projectTypes = new ArrayList<>();

    protected ApplicantReportFilters getFilters() {
        return ctx.getApplicantReportFilters();
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
     * Stringify the marital status.
     *
     * @param maritalStatus the marital status
     * @return the stringified marital status
     */
    protected String stringifyMaritalStatus(Set<UserMaritalStatus> maritalStatus) {
        if (maritalStatus.isEmpty()) {
            return BashColors.format("(All)", BashColors.LIGHT_GRAY);
        }
        final StringBuilder sb = new StringBuilder();
        if (maritalStatus.contains(UserMaritalStatus.MARRIED)) {
            sb.append(UserMaritalStatus.MARRIED.getName());
        }
        if (maritalStatus.contains(UserMaritalStatus.SINGLE)) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(UserMaritalStatus.SINGLE.getName());
        }
        return sb.toString();
    }

    /**
     * Stringify the age range.
     *
     * @param min the minimum age
     * @param max the maximum age
     * @return the stringified age range
     */
    protected String stringifyAgeRange(int min, int max) {
        if (min == -1 && max == -1) {
            return BashColors.format("(All)", BashColors.LIGHT_GRAY);
        }
        if (min == -1) {
            return BashColors.format("<= ", BashColors.LIGHT_GRAY) + max;
        }
        if (max == -1) {
            return BashColors.format(">= ", BashColors.LIGHT_GRAY) + min;
        }
        return min + BashColors.format(" - ", BashColors.LIGHT_GRAY) + max
                + BashColors.format(" (Inclusive)", BashColors.LIGHT_GRAY);
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
     * Show the options for the filters.
     */
    protected void showOptions() {
        final ApplicantReportFilters filters = getFilters();
        while (true) {
            String searchTermValue = stringifySearchTerm(filters.getNameSearchTerm());
            String ageRangeValue = stringifyAgeRange(filters.getAgeMin(), filters.getAgeMax());
            String maritalStatusValue = stringifyMaritalStatus(filters.getFilterMaritalStatuses());
            String projectTypesValue = stringifyProjectTypesAvailability(filters.getFilterFlatTypes());
            String sortTypeValue = stringifySortName(filters.getSortByName());

            System.out.println(BashColors.format("Filters", BashColors.BOLD));
            System.out.println("ID | Filter | Value");
            System.out.println("1 | Search Term (Name) | " + searchTermValue);
            System.out.println("2 | Age Range | " + ageRangeValue);
            System.out.println("3 | Marital Status | " + maritalStatusValue);
            System.out.println("4 | Project Types | " + projectTypesValue);
            System.out.println("5 | Name Sort Type | " + sortTypeValue);
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
                    showFilterAgeRange();
                    break;
                case "3":
                    showFilterByMaritalStatuses();
                    break;
                case "4":
                    showFilterByProjectType();
                    break;
                case "5":
                    showNameSort();
                    break;
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
        final ApplicantReportFilters filters = this.getFilters();
        System.out.println(
                BashColors.format("Type the search term you want to filter, or leave empty (i.e. '') to not set one.  ",
                        BashColors.BOLD));
        String searchTerm = scanner.nextLine().trim();
        filters.setNameSearchTerm(searchTerm);
        System.out
                .println(BashColors.format("Search term set to: " + stringifySearchTerm(searchTerm), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the filter by age range.
     */
    protected void showFilterAgeRange() {
        final Scanner scanner = ctx.getScanner();
        final ApplicantReportFilters filters = this.getFilters();
        while (true) {
            System.out.println(BashColors.format(
                    "Type the age range (in comma separated format) you want to filter, or leave empty (i.e. '') to not set one.  ",
                    BashColors.BOLD));
            System.out.println(BashColors.format(
                    "NOTE: You can imit specifying one of the bounds, (e.g. ', 100' = <=100, '100, ' = >=100, '20,100' = 20 to 100 (Inclusive))",
                    BashColors.LIGHT_GRAY));
            String ageRange = scanner.nextLine() + " ";
            if (ageRange.trim().isEmpty()) {
                filters.setAgeMin(-1);
                filters.setAgeMax(-1);
                break;
            }
            String[] ageRangeSplit = ageRange.split(",");
            if (ageRangeSplit.length != 2) {
                System.out.println(BashColors
                        .format("Invalid age range. Please enter a comma separated age range", BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            String minStr = ageRangeSplit[0].trim();
            String maxStr = ageRangeSplit[1].trim();

            int min = -1, max = -1;
            if (!minStr.isEmpty()) {
                TryCatchResult<Integer, Throwable> minResult = Utils.tryCatch(() -> {
                    return Integer.parseInt(minStr);
                });
                if (minResult.getErr().isPresent()) {
                    System.out.println(
                            BashColors.format("Invalid age min. Please enter a valid integer.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }
                min = minResult.getData().get();
                if (min < 0) {
                    System.out.println(
                            BashColors.format("Invalid age min. Range bound must be positive.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }
            }
            if (!maxStr.isEmpty()) {
                TryCatchResult<Integer, Throwable> maxResult = Utils.tryCatch(() -> {
                    return Integer.parseInt(maxStr.trim());
                });
                if (maxResult.getErr().isPresent()) {
                    System.out.println(
                            BashColors.format("Invalid age max. Please enter a valid integer.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }
                max = maxResult.getData().get();
                if (max < 0) {
                    System.out.println(
                            BashColors.format("Invalid age max. Range bound must be positive.", BashColors.RED));
                    System.out.println("Type anything to continue.");
                    scanner.nextLine();
                    continue;
                }
            }
            if (min != -1 && max != -1 && min > max) {
                System.out.println(BashColors.format("Invalid age range. Expected min <= max.",
                        BashColors.RED));
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }

            filters.setAgeMin(min);
            filters.setAgeMax(max);
            System.out.println(BashColors.format("Age Range set to: ", BashColors.GREEN) + stringifyAgeRange(min, max));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }
    }

    /**
     * Show the filter by marital statuses.
     */
    protected void showFilterByMaritalStatuses() {
        final Scanner scanner = ctx.getScanner();
        final ApplicantReportFilters filters = this.getFilters();

        String maritalStatusFilterRaw = "";
        final String currentMaritalStatuses = stringifyMaritalStatus(
                filters.getFilterMaritalStatuses());
        while (true) {
            System.out.println(BashColors.format("Marital Status", BashColors.BOLD));
            System.out.println("Currently filtering by: " + currentMaritalStatuses);
            System.out.println("Project Type ID:");
            for (UserMaritalStatus status : UserMaritalStatus.values()) {
                System.out.println("  " + status.getName());
            }
            System.out.println("");
            System.out.println("Type the marital status you want to filter, or leave empty ('') to not set one.");
            System.out.println(BashColors.format(
                    "NOTE: You may specify multiple by leaving a ',' between entries(e.g. \"Single, Married\") which will filter for applicants with EITHER a marital status of Single OR Married.",
                    BashColors.LIGHT_GRAY));

            maritalStatusFilterRaw = scanner.nextLine().trim();
            if (maritalStatusFilterRaw.isEmpty()) {
                filters.setFilterFlatTypes(new HashSet<>());
                break;
            }
            String[] maritalStatusFilter = maritalStatusFilterRaw.split(",");

            boolean areAllValid = true;
            Set<UserMaritalStatus> newMaritalStatusFilters = new HashSet<>();

            for (String _maritalStatus : maritalStatusFilter) {
                _maritalStatus = _maritalStatus.trim().toLowerCase();

                UserMaritalStatus maritalStatus = null;
                for (UserMaritalStatus status : UserMaritalStatus.values()) {
                    if (status.getName().toLowerCase().equals(_maritalStatus)) {
                        maritalStatus = status;
                        break;
                    }
                }

                if (maritalStatus == null) {
                    areAllValid = false;
                    System.out.println(BashColors.format("Invalid marital status: " + _maritalStatus, BashColors.RED));
                } else {
                    newMaritalStatusFilters.add(maritalStatus);
                }
            }
            if (!areAllValid) {
                System.out.println("Type anything to continue.");
                scanner.nextLine();
                continue;
            }
            filters.setFilterMaritalStatuses(newMaritalStatusFilters);
            break;
        }

        System.out.println(BashColors.format("Marital status set to: "
                + stringifyMaritalStatus(filters.getFilterMaritalStatuses()), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the filter by project type.
     */
    protected void showFilterByProjectType() {
        final Scanner scanner = ctx.getScanner();
        final ApplicantReportFilters filters = this.getFilters();
        if (projectTypes.isEmpty()) {
            System.out.println(BashColors.format("No project types available.", BashColors.RED));
            System.out.println("Type anything to continue.");
            scanner.nextLine();
            return;
        }

        String projectTypesFilterRaw = "";
        final String currentProjectTypesFilterValue = stringifyProjectTypesAvailability(
                filters.getFilterFlatTypes());
        while (true) {
            System.out.println(BashColors.format("Project Types with Availability", BashColors.BOLD));
            System.out.println("Currently filtering by: " + currentProjectTypesFilterValue);
            System.out.println("Marital Status:");
            for (BTOProjectTypeID projectType : projectTypes) {
                System.out.println("  " + projectType.getName());
            }
            System.out.println("");
            System.out.println("Type the project type you want to filter, or leave empty ('') to not set one.");
            System.out.println(BashColors.format(
                    "NOTE: You may specify multiple by leaving a ',' between entries(e.g. \"2 Room, 3 Room\") which will filter for applicants with EITHER 2 Room OR 3 Room booking.",
                    BashColors.LIGHT_GRAY));

            projectTypesFilterRaw = scanner.nextLine().trim();
            if (projectTypesFilterRaw.isEmpty()) {
                filters.setFilterFlatTypes(new HashSet<>());
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
            filters.setFilterFlatTypes(newProjectTypeFilters);
            break;
        }

        System.out.println(BashColors.format("Project Types Availability set to: "
                + stringifyProjectTypesAvailability(filters.getFilterFlatTypes()), BashColors.GREEN));
        System.out.println("Type anything to continue.");
        scanner.nextLine();
    }

    /**
     * Show the name sort.
     */
    protected void showNameSort() {
        final Scanner scanner = ctx.getScanner();
        final ApplicantReportFilters filters = this.getFilters();

        while (true) {
            String sortTypeValue = stringifySortName(filters.getSortByName());

            System.out.println(BashColors.format("Name Sort Type", BashColors.BOLD));
            System.out.println("Currently sorting by: " + sortTypeValue);
            System.out.println("Sort Types:");
            System.out.println("  A-Z");
            System.out.println("  Z-A");
            System.out.println("  None");
            System.out.println("");
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

}
