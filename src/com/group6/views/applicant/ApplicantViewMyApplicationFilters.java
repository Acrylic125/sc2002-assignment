package com.group6.views.applicant;

import com.group6.btoproject.BTOApplicationStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * See {@link ApplicantViewMyAppliedProjects}
 */
public class ApplicantViewMyApplicationFilters extends ProjectsViewFilters {

    private Set<BTOApplicationStatus> statusesFilter = new HashSet<>();

    public Set<BTOApplicationStatus> getStatusesFilter() {
        return Collections.unmodifiableSet(statusesFilter);
    }

    /**
     * Filter project application statuses.
     *
     * @param statusesFilter filter application statuses
     */
    public void setStatusesFilter(Set<BTOApplicationStatus> statusesFilter) {
        this.statusesFilter = statusesFilter;
    }

    /**
     * Adds an application status to the filters.
     *
     * @param status status.
     */
    public void addStatusFilter(BTOApplicationStatus status) {
        this.statusesFilter.add(status);
    }


    /**
     * Removes an application status from the filters.
     *
     * @param status status.
     */
    public void removeStatusFilter(BTOApplicationStatus status) {
        this.statusesFilter.remove(status);
    }

}
