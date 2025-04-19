package com.group6.users;

/**
 * Represents the permissions of a {@link User} in the BTO system.
 */
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

    /**
     * Constructor for UserPermissions.
     */
    protected UserPermissions() {}

    /**
     * @return Can apply for BTO projects.
     */
    public boolean canApply() {
        return apply;
    }

    /**
     * @param apply Can apply for BTO projects.
     * @return this
     */
    protected UserPermissions setApply(boolean apply) {
        this.apply = apply;
        return this;
    }

    /**
     * @return Can register to manage BTO projects.
     */
    public boolean canRegisterForProject() {
        return registerForProject;
    }

    /**
     * @param registerForProject Can register to manage BTO projects.
     * @return this
     */
    protected UserPermissions setRegisterForProject(boolean registerForProject) {
        this.registerForProject = registerForProject;
        return this;
    }

    /**
     * @return Can approve BTO applications.
     */
    public boolean canApproveApplications() {
        return approveApplications;
    }

    /**
     * @param approveApplications Can approve BTO applications.
     * @return this
     */
    protected UserPermissions setApproveApplications(boolean approveApplications) {
        this.approveApplications = approveApplications;
        return this;
    }

    /**
     * @return Can approve BTO application withdrawals.
     */
    public boolean canApproveWithdrawal() {
        return approveWithdrawal;
    }

    /**
     * @param approveWithdrawal Can approve BTO application withdrawals.
     * @return this
     */
    protected UserPermissions setApproveWithdrawal(boolean approveWithdrawal) {
        this.approveWithdrawal = approveWithdrawal;
        return this;
    }

    /**
     * @return Can approve officer registrations.
     */
    public boolean canApproveOfficerRegistrations() {
        return approveOfficerRegistrations;
    }

    /**
     * @param approveOfficerRegistrations Can approve officer registrations.
     * @return this
     */
    protected UserPermissions setApproveOfficerRegistrations(boolean approveOfficerRegistrations) {
        this.approveOfficerRegistrations = approveOfficerRegistrations;
        return this;
    }

    /**
     * @return Can create BTO projects.
     */
    public boolean canCreateProject() {
        return createProject;
    }

    /**
     * @param createProject Can create BTO projects.
     * @return this
     */
    protected UserPermissions setCreateProject(boolean createProject) {
        this.createProject = createProject;
        return this;
    }

    /**
     * @return Can edit BTO projects.
     */
    public boolean canEditProject() {
        return editProject;
    }

    /**
     * @param editProject Can edit BTO projects.
     * @return this
     */
    protected UserPermissions setEditProject(boolean editProject) {
        this.editProject = editProject;
        return this;
    }

    /**
     * @return Can delete BTO projects.
     */
    public boolean canDeleteProject() {
        return deleteProject;
    }

    /**
     * @param deleteProject Can delete BTO projects.
     * @return this
     */
    protected UserPermissions setDeleteProject(boolean deleteProject) {
        this.deleteProject = deleteProject;
        return this;
    }

    /**
     * @return Permission level to respond to BTO enquiries. at various levels. Refer to {@link #respondEnquiries}.
     */
    public int getRespondEnquiresLevel() {
        return respondEnquiries;
    }

    /**
     * @return Can respond to BTO enquiries. at various levels. Refer to {@link #respondEnquiries}.
     */
    public boolean canRespondEnquiries() {
        return respondEnquiries > 0;
    }

    /**
     * @param respondEnquiriesLevel Permission level to respond to BTO enquiries. at various levels. Refer to {@link #respondEnquiries}.
     * @return this
     */
    public UserPermissions setRespondEnquiries(int respondEnquiriesLevel) {
        this.respondEnquiries = respondEnquiriesLevel;
        return this;
    }

    /**
     * @return Can view non-visible BTO projects.
     */
    public boolean canViewNonVisibleProjects() {
        return viewNonVisibleProjects;
    }

    /**
     * @param viewNonVisibleProjects Can view non-visible BTO projects.
     * @return this
     */
    public UserPermissions setViewNonVisibleProjects(boolean viewNonVisibleProjects) {
        this.viewNonVisibleProjects = viewNonVisibleProjects;
        return this;
    }

    /**
     * @return Can view closed BTO projects.
     */
    public boolean canViewClosedProjects() {
        return viewClosedProjects;
    }

    /**
     * @param viewClosedProjects Can view closed BTO projects.
     * @return this
     */
    public UserPermissions setViewClosedProjects(boolean viewClosedProjects) {
        this.viewClosedProjects = viewClosedProjects;
        return this;
    }

    /**
     * @return Can generate applicants report.
     */
    public boolean canGenerateApplicantsReport() {
        return generateApplicantsReport;
    }

    /**
     * @param generateApplicantsReport Can generate applicants report.
     * @return this
     */
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
