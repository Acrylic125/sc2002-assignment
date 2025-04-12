package com.group6.users;

public enum UserRole {
    APPLICANT(
            new UserPermissions()
                    .setApply(true)),
    OFFICER(
            new UserPermissions()
                    .setApply(true)
                    .setRegisterForProject(true)
                    .setApproveApplications(true)
                    .setApproveWithdrawal(true)
                    .setRespondEnquiries(1)
                    .setViewNonVisibleProjects(true)
                    .setViewClosedProjects(true)),
    MANAGER(
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

    private final UserPermissions permissions;

    UserRole(UserPermissions permissions) {
        this.permissions = permissions;
    }

    public UserPermissions getPermissions() {
        return permissions;
    }
}
