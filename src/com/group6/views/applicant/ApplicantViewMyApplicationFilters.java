package com.group6.views.applicant;

import com.group6.btoproject.*;
import com.group6.users.User;
import com.group6.views.ViewSortType;

import java.util.*;

/**
 * See {@link ApplicantViewMyApplicationsView}
 */
public class ApplicantViewMyApplicationFilters {

    private Set<BTOApplicationStatus> applicationStatusesFilter = new HashSet<>();
    private ProjectsViewFilters projectsViewFilters = new ProjectsViewFilters();

    /**
     * Application statuses filters getter.
     *
     * @return {{@link #applicationStatusesFilter}}
     */
    public Set<BTOApplicationStatus> getApplicationStatusesFilter() {
        return Collections.unmodifiableSet(applicationStatusesFilter);
    }

    /**
     * Filter project application statuses.
     *
     * @param applicationStatusesFilter filter application statuses.
     */
    public void setApplicationStatusesFilter(Set<BTOApplicationStatus> applicationStatusesFilter) {
        this.applicationStatusesFilter = applicationStatusesFilter;
    }

    /**
     * Project view filters getter.
     *
     * @return {{@link #projectsViewFilters}}
     */
    public ProjectsViewFilters getProjectsViewFilters() {
        return projectsViewFilters;
    }

    /**
     * Project view filters setter.
     *
     * @param projectsViewFilters project view filters.
     */
    public void setProjectsViewFilters(ProjectsViewFilters projectsViewFilters) {
        this.projectsViewFilters = projectsViewFilters;
    }

    /**
     * Checks if an application, given the user filtering, can pass.
     *
     * @param application the application being filtered.
     * @param user the user filtering.
     * @return true if application passes the filters.
     */
    public boolean canFilterApplication(BTOProjectManager.BTOFullApplication application, User user) {
        if (!this.projectsViewFilters.canFilterProject(application.getProject(), user)) {
            return false;
        }

        // Filter by application status.
        if (!applicationStatusesFilter.isEmpty()) {
            return applicationStatusesFilter.contains(application.getApplication().getStatus());
        }

        return true;
    }

    /**
     * Applies the filters INCLUDING SORTING, to a list of applications, given the user.
     *
     * @param applications the list of applications.
     * @param user the user doing the filtering.
     * @return a list of application filtered and sorted.
     */
    public List<BTOProjectManager.BTOFullApplication> applyFilters(List<BTOProjectManager.BTOFullApplication> applications, User user) {
        List<BTOProjectManager.BTOFullApplication> filteredProjects = applications.stream()
                .filter((project) -> canFilterApplication(project, user))
                .toList();
        ViewSortType sortByName = this.getProjectsViewFilters().getSortByName();
        if (sortByName == ViewSortType.NONE) {
            return filteredProjects;
        }
        filteredProjects = new ArrayList<>(filteredProjects);
        if (sortByName == ViewSortType.ASC) {
            filteredProjects.sort(Comparator.comparing(a -> a.getProject().getName()));
        } else {
            filteredProjects.sort((a, b) ->
                    b.getProject().getName().compareTo(a.getProject().getName()));
        }
        return filteredProjects;
    }

}
