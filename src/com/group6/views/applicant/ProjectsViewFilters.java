package com.group6.views.applicant;

import com.group6.views.ViewSortType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Project view filters.
 * Filters are composited into a single object to simplify.
 * They are all treated as an AND filters.
 * See {@link ApplicantProjectsView}
 */
public class ProjectsViewFilters {

    private String searchTerm = "";
    private String location = "";
    private ViewSortType sortByName = ViewSortType.ASC;
    // Although we assume only 2 project types, we cannot guarantee this.
    // We go by exclusion since we may not know all the project types.
    private Set<String> excludeProjectTypes = new HashSet<>();
    private boolean onlyShowManagedProjects;

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
     *
     * @return {@link #excludeProjectTypes}
     */
    public Set<String> getExcludeProjectTypes() {
        return Collections.unmodifiableSet(excludeProjectTypes);
    }

    /**
     * Exclude project types setter.
     *
     * @param excludeProjectTypes exclude project types
     */
    public void setExcludeProjectTypes(Set<String> excludeProjectTypes) {
        this.excludeProjectTypes = excludeProjectTypes;
    }

    /**
     * Adds a project type to the filters.
     *
     * @param projectType project type id.
     */
    public void addProjectTypeFilter(String projectType) {
        this.excludeProjectTypes.add(projectType);
    }

    /**
     * Removes a project type from the filters.
     *
     * @param projectType project type id.
     */
    public void removeProjectTypeFilter(String projectType) {
        this.excludeProjectTypes.remove(projectType);
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

}
