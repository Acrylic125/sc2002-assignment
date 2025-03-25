package com.group6.views.applicant;

import com.group6.btoproject.BTOApplicationStatus;
import com.group6.btoproject.BTOApplicationWithdrawalStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * See {@link ApplicantViewMyApplicationsView}
 */
public class ApplicantViewMyApplicationFilters {

    private Set<BTOApplicationStatus> applicationStatusesFilter = new HashSet<>();
    private Set<BTOApplicationWithdrawalStatus> withdrawalStatusesFilter = new HashSet<>();
    private boolean shouldIncludeNAWithdrawals = true;
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
     * Withdrawal statuses filters getter.
     *
     * @return {{@link #withdrawalStatusesFilter}}
     */
    public Set<BTOApplicationWithdrawalStatus> getWithdrawalStatusesFilter() {
        return withdrawalStatusesFilter;
    }

    /**
     * Filter project withdrawal statuses.
     *
     * @param withdrawalStatusesFilter filter withdrawal statuses.
     */
    public void setWithdrawalStatusesFilter(Set<BTOApplicationWithdrawalStatus> withdrawalStatusesFilter) {
        this.withdrawalStatusesFilter = withdrawalStatusesFilter;
    }

    /**
     * Should include N/A withdrawals getter.
     *
     * @return {@link #shouldIncludeNAWithdrawals}
     */
    public boolean isShouldIncludeNAWithdrawals() {
        return shouldIncludeNAWithdrawals;
    }

    /**
     * Should include N/A withdrawals setter.
     *
     * @param shouldIncludeNAWithdrawals filter applications without withdrawals.
     */
    public void setShouldIncludeNAWithdrawals(boolean shouldIncludeNAWithdrawals) {
        this.shouldIncludeNAWithdrawals = shouldIncludeNAWithdrawals;
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

}
