package com.group6.views.applicant;

import com.group6.btoproject.BTOProject;
import com.group6.btoproject.BTOProjectType;
import com.group6.btoproject.BTOProjectTypeID;
import com.group6.users.User;
import com.group6.views.ViewSortType;

import java.util.*;

/**
 * Project view filters.
 * Filters are composited into a single object to simplify.
 * They are all treated as an AND filters.
 * See {@link ProjectsView}
 */
public class ProjectsViewFilters {

    private String searchTerm = "";
    private String location = "";
    private ViewSortType sortByName = ViewSortType.ASC;
    // Filters only the projects with AT LEAST 1 SLOT AVAILABLE
    // for booking.
    private Set<BTOProjectTypeID> filterAvailableProjectTypes = new HashSet<>();
    // Only meant to be used by HDBOfficers. It is toggled off by default, no point
    // creating a whole new filter object just for this 1 use case.
    private boolean onlyShowManagedProjects = false;

    /**
     * Search term getter.
     * Empty string implies any search.
     *
     * @return {@link #searchTerm}
     */
    public String getSearchTerm() {
        return searchTerm == null ? "" : searchTerm;
    }

    /**
     * Search term setter.
     *
     * @param searchTerm search term
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Location getter.
     *
     * @return {@link #location}
     */
    public String getLocation() {
        return location == null ? "" : location;
    }

    /**
     * Location setter.
     *
     * @param location location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sort by name getter.
     *
     * @return {@link #sortByName}
     */
    public ViewSortType getSortByName() {
        return sortByName;
    }

    /**
     * Sort by name setter.
     *
     * @param sortByName sort by name
     */
    public void setSortByName(ViewSortType sortByName) {
        this.sortByName = sortByName;
    }

    /**
     * Exclude project types getter.
     * Empty implies that there is no need to filter.
     * If there are multiple, it means that the project will be shown AS LONG AS
     * AT LEAST 1 project type filtered is available for booking.
     *
     * @return {@link #filterAvailableProjectTypes}
     */
    public Set<BTOProjectTypeID> getFilterAvailableProjectTypes() {
        return Collections.unmodifiableSet(filterAvailableProjectTypes);
    }

    /**
     * Only include projects with available slots for project type, setter.
     *
     * @param filterAvailableProjectTypes project types
     */
    public void setFilterAvailableProjectTypes(Set<BTOProjectTypeID> filterAvailableProjectTypes) {
        this.filterAvailableProjectTypes = filterAvailableProjectTypes;
    }

    /**
     * Only show managed projects getter.
     *
     * @return {@link #onlyShowManagedProjects}
     */
    public boolean isOnlyShowManagedProjects() {
        return onlyShowManagedProjects;
    }

    /**
     * Only show managed projects setter.
     *
     * @param onlyShowManagedProjects only show managed projects
     */
    public void setOnlyShowManagedProjects(boolean onlyShowManagedProjects) {
        this.onlyShowManagedProjects = onlyShowManagedProjects;
    }

    /**
     * Checks if a project, given the user filtering, can pass.
     *
     * @param project the project being filtered.
     * @param user the user filtering.
     * @return true if project passes the filters.
     */
    public boolean canFilterProject(BTOProject project, User user) {
        final String searchTerm = getSearchTerm().toLowerCase();
        final String location = getLocation();

        // Filter by search term.
        if (!searchTerm.isEmpty() && !project.getName().toLowerCase().contains(searchTerm)) {
            return false;
        }

        // Filter by location.
        if (!location.isEmpty()
                && !project.getNeighbourhood().equals(location)) {
            return false;
        }

        // Filter by project types with availability.
        Set<BTOProjectTypeID> availableProjectTypesFilter = getFilterAvailableProjectTypes();
        if (!availableProjectTypesFilter.isEmpty()) {
            if (availableProjectTypesFilter.stream().noneMatch((projectTypeId) -> {
                Optional<BTOProjectType> projectTypeOpt = project.getProjectTypes().stream()
                        .filter((_projectType) -> _projectType.getId().equals(projectTypeId))
                        .findFirst();
                if (projectTypeOpt.isEmpty()) {
                    return false;
                }
                try {
                    return project.getBookedCountForType(projectTypeId) < projectTypeOpt.get().getMaxQuantity();
                } catch (RuntimeException ex) {
                    return false;
                }
            })) {
                return false;
            }
        }

        // Filter by managing project.
        return !isOnlyShowManagedProjects() || project.isManagedBy(user.getId());
   }

    /**
     * Applies the filters INCLUDING SORTING, to a list of projects, given the user.
     *
     * @param projects the list of projects.
     * @param user the user doing the filtering.
     * @return a list of projects filtered and sorted.
     */
    public List<BTOProject> applyFilters(List<BTOProject> projects, User user) {
        List<BTOProject> filteredProjects = projects.stream()
                .filter((project) -> canFilterProject(project, user))
                .toList();
        ViewSortType sortByName = getSortByName();
        if (sortByName == ViewSortType.NONE) {
            return filteredProjects;
        }
        filteredProjects = new ArrayList<>(filteredProjects);
        if (sortByName == ViewSortType.ASC) {
            filteredProjects.sort(Comparator.comparing(BTOProject::getName));
        } else {
            filteredProjects.sort((a, b) -> b.getName().compareTo(a.getName()));
        }
        return filteredProjects;
    }

}
