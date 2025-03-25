package com.group6.views.applicant;

import com.group6.btoproject.BTOApplicationStatus;
import com.group6.views.ViewSortType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Project view filters.
 * Filters are composited into a single object to simplify.
 * They are all treated as an AND filters.
 * See {@link ApplicantProjectsView}
 */
public class ProjectsViewFilters {

    private String searchTerm;
    private String location;
    private ViewSortType sortByName = ViewSortType.ASC;
    private Set<String> filterProjectTypes;
    private boolean onlyShowManagedProjects;

    /**
     * Search term getter.
     *
     * @return {@link #searchTerm}
     */
    public String getSearchTerm() {
        return searchTerm;
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
        return location;
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
     * Filter project types getter.
     *
     * @return {@link #filterProjectTypes}
     */
    public Set<String> getFilterProjectTypes() {
        return Collections.unmodifiableSet(filterProjectTypes);
    }

    /**
     * Filter project types setter.
     *
     * @param filterProjectTypes filter project types
     */
    public void setFilterProjectTypes(Set<String> filterProjectTypes) {
        this.filterProjectTypes = filterProjectTypes;
    }

    /**
     * Adds a project type to the filters.
     *
     * @param projectType project type id.
     */
    public void addProjectTypeFilter(String projectType) {
        this.filterProjectTypes.add(projectType);
    }

    /**
     * Removes a project type from the filters.
     *
     * @param projectType project type id.
     */
    public void removeProjectTypeFilter(String projectType) {
        this.filterProjectTypes.remove(projectType);
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
