package com.group6.users;

public class UserPermissions {
    
    private boolean apply;
    private boolean registerForProject;
    private boolean approveApplications;
    private boolean approveWithdrawal;
    private boolean approveOfficerRegistrations;
    private boolean createProject;
    private boolean editProject;
    private boolean deleteProject;
    // Level 0 -> Cannot respond
    // Level 1 -> Can respond to managing projects enquiries
    // Level 2 -> Can respond to all enquiries
    private int respondEnquiries;
    private boolean viewNonVisibleProjects;
    private boolean viewClosedProjects;
    private boolean generateApplicantsReport;

    protected UserPermissions() {}

    public boolean canApply() {
        return apply;
    }

    protected UserPermissions setApply(boolean apply) {
        this.apply = apply;
        return this;
    }

    public boolean canRegisterForProject() {
        return registerForProject;
    }

    protected UserPermissions setRegisterForProject(boolean registerForProject) {
        this.registerForProject = registerForProject;
        return this;
    }

    public boolean canApproveApplications() {
        return approveApplications;
    }

    protected UserPermissions setApproveApplications(boolean approveApplications) {
        this.approveApplications = approveApplications;
        return this;
    }

    public boolean canApproveWithdrawal() {
        return approveWithdrawal;
    }

    protected UserPermissions setApproveWithdrawal(boolean approveWithdrawal) {
        this.approveWithdrawal = approveWithdrawal;
        return this;
    }

    public boolean canApproveOfficerRegistrations() {
        return approveOfficerRegistrations;
    }

    protected UserPermissions setApproveOfficerRegistrations(boolean approveOfficerRegistrations) {
        this.approveOfficerRegistrations = approveOfficerRegistrations;
        return this;
    }

    public boolean canCreateProject() {
        return createProject;
    }

    protected UserPermissions setCreateProject(boolean createProject) {
        this.createProject = createProject;
        return this;
    }

    public boolean canEditProject() {
        return editProject;
    }

    protected UserPermissions setEditProject(boolean editProject) {
        this.editProject = editProject;
        return this;
    }

    public boolean canDeleteProject() {
        return deleteProject;
    }

    protected UserPermissions setDeleteProject(boolean deleteProject) {
        this.deleteProject = deleteProject;
        return this;
    }

    public int getRespondEnquiresLevel() {
        return respondEnquiries;
    }

    public boolean canRespondEnquiries() {
        return respondEnquiries > 0;
    }

    public UserPermissions setRespondEnquiries(int respondEnquiriesLevel) {
        this.respondEnquiries = respondEnquiriesLevel;
        return this;
    }

    public boolean canViewNonVisibleProjects() {
        return viewNonVisibleProjects;
    }

    public UserPermissions setViewNonVisibleProjects(boolean viewNonVisibleProjects) {
        this.viewNonVisibleProjects = viewNonVisibleProjects;
        return this;
    }

    public boolean canViewClosedProjects() {
        return viewClosedProjects;
    }

    public UserPermissions setViewClosedProjects(boolean viewClosedProjects) {
        this.viewClosedProjects = viewClosedProjects;
        return this;
    }

    public boolean canGenerateApplicantsReport() {
        return generateApplicantsReport;
    }

    public UserPermissions setGenerateApplicantsReport(boolean generateApplicantsReport) {
        this.generateApplicantsReport = generateApplicantsReport;
        return this;
    }

    /**
     * General check to determine whether the user with this permission is classified
     * as being able to manage projects.
     *
     * @return See checks below.
     */
    public boolean canManageProjects() {
        return createProject || editProject || deleteProject || registerForProject ||
                canRespondEnquiries() || approveApplications || approveWithdrawal || approveOfficerRegistrations;
    }

}
