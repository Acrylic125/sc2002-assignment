package com.group6.users;

public enum UserRole {
    APPLICANT(
            new UserPermissions()
                    .setApply(true)
    ), OFFICER(
            new UserPermissions()
                    .setApply(true)
                    .setApproveApplications(true)
                    .setApproveWithdrawal(true)
                    .setApproveOfficerRegistrations(true)
                    .setRespondEnquiries(true)
                    .setViewNonVisibleProjects(true)
                    .setViewClosedProjects(true)
    ), MANAGER(
            new UserPermissions()
                    .setApproveApplications(true)
                    .setApproveWithdrawal(true)
                    .setApproveOfficerRegistrations(true)
                    .setRespondEnquiries(true)
                    .setCreateProject(true)
                    .setEditProject(true)
                    .setDeleteProject(true)
                    .setViewNonVisibleProjects(true)
                    .setViewClosedProjects(true)
    );

    private final UserPermissions permissions;

    UserRole(UserPermissions permissions) {
        this.permissions = permissions;
    }

    public UserPermissions getPermissions() {
        return permissions;
    }
}
