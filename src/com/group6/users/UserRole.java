package com.group6.users;

public enum UserRole {
    APPLICANT(
            "Applicant",
            new UserPermissions()
                    .setApply(true)),
    OFFICER(
            "Officer",
            new UserPermissions()
                    .setApply(true)
                    .setRegisterForProject(true)
                    .setApproveApplications(true)
                    .setApproveWithdrawal(true)
                    .setRespondEnquiries(1)
                    .setViewNonVisibleProjects(true)
                    .setViewClosedProjects(true)),
    MANAGER(
            "Manager",
            new UserPermissions()
                    .setApproveApplications(true)
                    .setApproveWithdrawal(true)
                    .setApproveOfficerRegistrations(true)
                    .setRespondEnquiries(2)
                    .setCreateProject(true)
                    .setEditProject(true)
                    .setDeleteProject(true)
                    .setViewNonVisibleProjects(true)
                    .setViewClosedProjects(true)
                    .setGenerateApplicantsReport(true));

    private final String name;
    private final UserPermissions permissions;

    UserRole(String name, UserPermissions permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    /**
     * User role name getter.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * User role permissions getter.
     *
     * @return {@link #permissions}
     */
    public UserPermissions getPermissions() {
        return permissions;
    }
}
