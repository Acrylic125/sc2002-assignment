package com.group6.views.management;

import com.group6.btoproject.BTOProjectManager;
import com.group6.btoproject.BTOProjectTypeID;
import com.group6.users.User;
import com.group6.users.UserMaritalStatus;
import com.group6.views.ViewSortType;
import com.group6.views.applicant.ApplicantViewMyApplicationsView;

import java.util.*;

/**
 * See {@link ApplicantViewMyApplicationsView}
 */
public class ApplicantReportFilters {

    private String nameSearchTerm = "";
    private int ageMin = -1, ageMax = -1;
    private Set<UserMaritalStatus> filterMaritalStatuses = new HashSet<>();
    private Set<BTOProjectTypeID> filterFlatTypes = new HashSet<>();

    private ViewSortType sortByName = ViewSortType.ASC;

    /**
     * Marital status getter.
     *
     * @return {@link #filterMaritalStatuses}
     */
    public Set<UserMaritalStatus> getFilterMaritalStatuses() {
        return filterMaritalStatuses;
    }

    /**
     * Marital status setter.
     *
     * @param filterMaritalStatuses marital status
     */
    public void setFilterMaritalStatuses(Set<UserMaritalStatus> filterMaritalStatuses) {
        this.filterMaritalStatuses = filterMaritalStatuses;
    }

    /**
     * Name search term getter.
     *
     * @return {@link #nameSearchTerm}
     */
    public String getNameSearchTerm() {
        return nameSearchTerm;
    }

    /**
     * Name search term setter.
     *
     * @param nameSearchTerm name search term
     */
    public void setNameSearchTerm(String nameSearchTerm) {
        this.nameSearchTerm = nameSearchTerm;
    }

    /**
     * Age min getter.
     *
     * @return {@link #ageMin}
     */
    public int getAgeMin() {
        return ageMin;
    }

    /**
     * Age min setter.
     *
     * @param ageMin age min
     */
    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    /**
     * Age max getter.
     *
     * @return {@link #ageMax}
     */
    public int getAgeMax() {
        return ageMax;
    }

    /**
     * Age max setter.
     *
     * @param ageMax age max
     */
    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    /**
     * Flat types getter.
     *
     * @return {@link #filterFlatTypes}
     */
    public Set<BTOProjectTypeID> getFilterFlatTypes() {
        return filterFlatTypes;
    }

    /**
     * Flat types setter.
     *
     * @param filterFlatTypes flat types
     */
    public void setFilterFlatTypes(Set<BTOProjectTypeID> filterFlatTypes) {
        this.filterFlatTypes = filterFlatTypes;
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
     * Checks if the applicant can be filtered.
     *
     * @param projectManager project manager
     * @param applicant      applicant
     * @param user           user
     * @return true if the applicant can be filtered, false otherwise
     */
    public boolean canFilterApplicant(BTOProjectManager projectManager, User applicant, User user) {
        if (!filterMaritalStatuses.isEmpty() && !filterMaritalStatuses.contains(applicant.getMaritalStatus())) {
            return false;
        }

        if (ageMin != -1 && applicant.getAge() < ageMin) {
            return false;
        }
        if (ageMax != -1 && applicant.getAge() > ageMax) {
            return false;
        }

        if (!nameSearchTerm.isEmpty() && !applicant.getName().toLowerCase().contains(nameSearchTerm.toLowerCase())) {
            return false;
        }

        final List<BTOProjectManager.BTOFullApplication> applications = projectManager
                .getBookedApplicationsForUser(applicant.getId());

        if (!filterFlatTypes.isEmpty()) {
            boolean hasFlatType = false;
            for (BTOProjectManager.BTOFullApplication application : applications) {
                if (filterFlatTypes.contains(application.getApplication().getTypeId())) {
                    hasFlatType = true;
                    break;
                }
            }
            if (!hasFlatType) {
                return false;
            }
        }

        return true;
    }

    /**
     * Applies the filters to the list of applicants.
     *
     * @param projectManager project manager
     * @param applicants     applicants
     * @param user           user
     * @return filtered list of applicants
     */
    public List<User> applyFilters(BTOProjectManager projectManager, List<User> applicants, User user) {
        List<User> filteredProjects = applicants.stream()
                .filter((project) -> canFilterApplicant(projectManager, project, user))
                .toList();
        ViewSortType sortByName = getSortByName();
        if (sortByName == ViewSortType.NONE) {
            return filteredProjects;
        }
        filteredProjects = new ArrayList<>(filteredProjects);
        if (sortByName == ViewSortType.ASC) {
            filteredProjects.sort(Comparator.comparing(a -> a.getName()));
        } else {
            filteredProjects.sort((a, b) -> b.getName().compareTo(a.getName()));
        }
        return filteredProjects;
    }

}
