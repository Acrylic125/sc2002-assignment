package com.group6.btoproject;

import java.util.*;

/**
 * Represents a central point to manage/access projects of a
 * {@link com.group6.BTOSystem}
 *
 * See {@link BTOProject}.
 * See {@link com.group6.BTOSystem}.
 */
public class BTOProjectManager {

    // Map<String Id, BTOProject>
    private final Map<String, BTOProject> projects = new HashMap<>();

    /**
     * Constructor for BTOProjectManager.
     */
    public BTOProjectManager() {
    }

    /**
     * Projects getter.
     *
     * @return {@link #projects}
     */
    public Map<String, BTOProject> getProjects() {
        return projects;
    }

    /**
     * Get a project by id.
     *
     * @param id id of the project.
     * @return project with the id.
     */
    public Optional<BTOProject> getProject(String id) {
        return Optional.ofNullable(projects.get(id));
    }

    /**
     * Add a project to the manager.
     *
     * @param project project to be added.
     */
    public void addProject(BTOProject project) throws RuntimeException {
        if (projects.values().stream()
                .anyMatch((_project) -> project.getName().equals(_project.getName()))) {
            throw new RuntimeException("Project with name already exists.");
        }
        projects.put(project.getId(), project);
    }

    /**
     * Get all active projects for a user.
     * Although a user SHOULD only have one active application for a project,
     * there is no guarantee that this is the case on the code level.
     *
     * @param userId id of the user.
     * @return list of active projects for the user.
     */
    public List<BTOProject> getActiveProjectsForUser(String userId) {
        return projects.values().stream()
                .filter(p -> p.getActiveApplication(userId).isPresent())
                .toList();
    }

    public static class BTOFullApplication {
        private final BTOProject project;
        private final BTOApplication application;
        private final BTOApplicationWithdrawal withdrawal;

        public BTOFullApplication(BTOProject project, BTOApplication application, BTOApplicationWithdrawal withdrawal) {
            this.project = project;
            this.application = application;
            this.withdrawal = withdrawal;
        }

        public BTOProject getProject() {
            return project;
        }

        public BTOApplication getApplication() {
            return application;
        }

        public Optional<BTOApplicationWithdrawal> getWithdrawal() {
            return Optional.ofNullable(withdrawal);
        }
    }

    /**
     * Get all project applications applied by a user.
     *
     * @param userId id of the user.
     * @return list of projects applied by the user.
     */
    public List<BTOFullApplication> getAllApplicationsForUser(String userId) {
        LinkedList<BTOFullApplication> result = new LinkedList<>();
        projects.values().forEach(project -> {
            project.getApplications().forEach(application -> {
                if (application.getApplicantUserId().equals(userId)) {
                    Optional<BTOApplicationWithdrawal> wotjdrawaOpt = project.getActiveWithdrawal(application.getId());
                    result.add(new BTOFullApplication(project, application, wotjdrawaOpt.orElse(null)));
                }
            });
        });
        return result;
    }

    /**
     * Get all booked applications for a user.
     *
     * @param userId id of the user.
     * @return list of booked applications for the user.
     */
    public List<BTOFullApplication> getBookedApplicationsForUser(String userId) {
        return getAllApplicationsForUser(userId).stream()
                .filter(application -> application.getApplication().getStatus() == BTOApplicationStatus.BOOKED)
                .toList();
    }

    /**
     * Get all project types.
     *
     * @return list of all project types.
     */
    public List<BTOProjectTypeID> getAllProjectTypes() {
        return Arrays.stream(BTOProjectTypeID.values())
                .toList();
    }

    /**
     * Add an application to the project.
     *
     * @param projectId       project to add the application to.
     * @param applicantUserId application to add.
     * @param typeId          type id of the application.
     * @throws RuntimeException If there exists an application that:
     *                          - is PENDING and has the same applicantUserId.
     *                          - is SUCCESSFUL and has the same applicantUserId.
     *                          - is BOOKED and has the same applicantUserId.
     */
    public void requestApply(String projectId, String applicantUserId, BTOProjectTypeID typeId)
            throws RuntimeException {
        BTOProject project = projects.get(projectId);
        if (project == null) {
            throw new RuntimeException("Project not found.");
        }

        if (!project.isApplicationWindowOpen()) {
            throw new RuntimeException("Application window is closed.");
        }

        if (project.isApplicantBooked(applicantUserId)) {
            throw new RuntimeException(
                    "Applicant is already booked for this project.");
        }

        if (!getBookedApplicationsForUser(applicantUserId).isEmpty()) {
            throw new RuntimeException("Applicant can only be booked for 1 project.");
        }

        project.getActiveApplication(applicantUserId)
                .ifPresent(application -> {
                    final BTOApplicationStatus status = application.getStatus();
                    if (status == BTOApplicationStatus.PENDING) {
                        throw new RuntimeException("There is already an Application pending.");
                    } else if (status == BTOApplicationStatus.SUCCESSFUL) {
                        throw new RuntimeException("There is already a successful Application.");
                    } else {
                        throw new RuntimeException("There is already a booked Application.");
                    }
                });

        final Optional<BTOProjectType> projectTypeOpt = project.getProjectType(typeId);
        if (projectTypeOpt.isEmpty()) {
            throw new RuntimeException("Project type, " + typeId.getName() + " does not exist.");
        }
        final BTOProjectType projectType = projectTypeOpt.get();
        if (projectType.getMaxQuantity() <= 0) {
            throw new RuntimeException("Project type, " + typeId.getName() + " has no availability.");
        }

        if (project.isManagingOfficer(applicantUserId)) {
            throw new RuntimeException("Project registered officers cannot apply for this project.");
        }

        final BTOApplication application = new BTOApplication(
                UUID.randomUUID().toString(),
                applicantUserId,
                typeId,
                BTOApplicationStatus.PENDING);
        project.addApplication(application);
    }

    /**
     * Get all valid application state transitions.
     * 
     * @param currentStatus current status of the application.
     * @return set of valid application state transitions.
     */
    public Set<BTOApplicationStatus> getValidApplicationStateTransitions(BTOApplicationStatus currentStatus) {
        if (currentStatus == BTOApplicationStatus.PENDING) {
            return Set.of(
                    BTOApplicationStatus.SUCCESSFUL,
                    BTOApplicationStatus.UNSUCCESSFUL);
        } else if (currentStatus == BTOApplicationStatus.SUCCESSFUL) {
            return Set.of(
                    BTOApplicationStatus.BOOKED,
                    BTOApplicationStatus.UNSUCCESSFUL);
        } else if (currentStatus == BTOApplicationStatus.BOOKED) {
            return Set.of(BTOApplicationStatus.UNSUCCESSFUL);
        }
        return Set.of();
    }

    /**
     * Transition the status of an application.
     *
     * @param projectId     project id.
     * @param applicationId application id.
     * @param status        new status.
     * @throws RuntimeException If the application:
     *                          - Is not found.
     *                          - Is PENDING and the new status is not
     *                          SUCCESSFUL/UNSUCCESSFUL.
     *                          - Is SUCCESSFUL and the new status is not
     *                          BOOKED/UNSUCCESSFUL.
     *                          - Is BOOKED and the new status is not UNSUCCESSFUL.
     */
    public void transitionApplicationStatus(String projectId, String applicationId, BTOApplicationStatus status)
            throws RuntimeException {
        final Optional<BTOProject> projectOpt = getProject(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found.");
        }

        final BTOProject project = projectOpt.get();

        final Optional<BTOApplication> applicationOpt = project.getApplication(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found.");
        }
        final BTOApplication application = applicationOpt.get();

        final BTOApplicationStatus currentStatus = application.getStatus();
        final Set<BTOApplicationStatus> validStatuses = getValidApplicationStateTransitions(currentStatus);
        if (!validStatuses.contains(status)) {
            throw new RuntimeException("Invalid transition from " + currentStatus + " to " + status
                    + ". Only allowed to transition from " + currentStatus + " to " + validStatuses + ".");
        }

        if (status == BTOApplicationStatus.BOOKED || status == BTOApplicationStatus.SUCCESSFUL) {
            final Optional<BTOProjectType> projectTypeOpt = project.getProjectType(application.getTypeId());
            if (projectTypeOpt.isEmpty()) {
                throw new RuntimeException("Project type, " + application.getTypeId() + " does not exist.");
            }

            final BTOProjectType projectType = projectTypeOpt.get();
            int bookedCountForType = project.getBookedCountForType(application.getTypeId());
            if (bookedCountForType >= projectType.getMaxQuantity()) {
                throw new RuntimeException("Project type, " + application.getTypeId() + " has no availability.");
            }

            if (!project.isApplicationWindowOpen()) {
                throw new RuntimeException("Application window is closed.");
            }

            if (project.isManagingOfficer(application.getApplicantUserId())) {
                throw new RuntimeException("Project registered officers cannot apply for this project.");
            }

            if (status == BTOApplicationStatus.BOOKED) {
                if (!getBookedApplicationsForUser(application.getApplicantUserId()).isEmpty()) {
                    throw new RuntimeException("Applicant can only be booked for 1 project.");
                }
            }
        }

        application.setStatus(status);
    }

    /**
     * Add an HDB officer registration to the project.
     *
     * @param projectId project id.
     * @param userId    HDB officer registration to add.
     * @throws RuntimeException If there exists an officer registration that:
     *                          - is SUCCESSFUL and has the same officerUserId (i.e.
     *                          already managing this project).
     *                          - is PENDING and has the same officerUserId.
     */
    public void requestRegisterOfficer(String projectId, String userId) throws RuntimeException {
        final Optional<BTOProject> projectOpt = getProject(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found.");
        }

        final BTOProject project = projectOpt.get();
        project.getActiveOfficerRegistration(userId)
                .ifPresent(registration -> {
                    final HDBOfficerRegistrationStatus status = registration.getStatus();
                    if (status == HDBOfficerRegistrationStatus.SUCCESSFUL) {
                        throw new RuntimeException("Officer is already managing this project.");
                    } else {
                        throw new RuntimeException("Officer registration is pending.");
                    }
                });

        if (project.isApplicantBooked(userId)) {
            throw new RuntimeException(
                    "Project Applicants with approved bookings may not register to manage this project.");
        }

        final List<BTOProject> managedByOfficer = this.getOfficerManagingProjects(userId);
        for (BTOProject managedProject : managedByOfficer) {
            if (managedProject.isApplicationWindowOpen()) {
                throw new RuntimeException(
                        "Officer is already managing another project that's currently open.");
            }
        }

        final HDBOfficerRegistration registration = new HDBOfficerRegistration(
                UUID.randomUUID().toString(),
                userId,
                HDBOfficerRegistrationStatus.PENDING);
        project.addHDBOfficerRegistration(registration);
    }

    /**
     * Get all valid registration state transitions.
     *
     * @param currentStatus current status of the registration.
     * @return set of valid application state transitions.
     */
    public Set<HDBOfficerRegistrationStatus> getValidOfficerRegistrationStateTransitions(HDBOfficerRegistrationStatus currentStatus) {
        if (currentStatus == HDBOfficerRegistrationStatus.PENDING) {
            return Set.of(
                    HDBOfficerRegistrationStatus.SUCCESSFUL,
                    HDBOfficerRegistrationStatus.UNSUCCESSFUL);
        } else if (currentStatus == HDBOfficerRegistrationStatus.SUCCESSFUL) {
            return Set.of(HDBOfficerRegistrationStatus.UNSUCCESSFUL);
        }
        return Set.of();
    }

    /**
     * Transition the status of an officer registration.
     *
     * @param projectId project id.
     * @param userId    user id.
     * @param status    new status.
     * @throws RuntimeException If the registration:
     *                          - Is not found.
     *                          - Does NOT follow transition from PENDING TO
     *                          SUCCESSFUL/UNSUCCESSFUL.
     */
    public void transitionOfficerRegistrationStatus(String projectId, String userId,
            HDBOfficerRegistrationStatus status)
            throws RuntimeException {
        final Optional<BTOProject> projectOpt = getProject(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found.");
        }

        final BTOProject project = projectOpt.get();
        final Optional<HDBOfficerRegistration> registrationOpt = project.getActiveOfficerRegistration(userId);
        if (registrationOpt.isEmpty()) {
            throw new RuntimeException("Registration not found.");
        }

        final HDBOfficerRegistration registration = registrationOpt.get();
        final HDBOfficerRegistrationStatus currentStatus = registration.getStatus();
        final Set<HDBOfficerRegistrationStatus> validStatuses = getValidOfficerRegistrationStateTransitions(currentStatus);
        if (!validStatuses.contains(status)) {
            throw new RuntimeException("Invalid transition from " + currentStatus + " to " + status
                    + ". Only allowed to transition from " + currentStatus + " to " + validStatuses + ".");
        }

        if (status == HDBOfficerRegistrationStatus.SUCCESSFUL) {
            final int officerLimit = project.getOfficerLimit();
            if (project.getManagingOfficerRegistrations().size() >= officerLimit) {
                throw new RuntimeException("Officer limit of " + officerLimit + " for this project has been reached.");
            }
            if (project.isApplicantBooked(userId)) {
                throw new RuntimeException(
                        "Project Applicants with approved bookings may not register to manage this project.");
            }

            final List<BTOProject> managedByOfficer = this.getOfficerManagingProjects(userId);
            for (BTOProject managedProject : managedByOfficer) {
                if (managedProject.isApplicationWindowOpen()) {
                    throw new RuntimeException(
                            "Officer is already managing another project that's currently open.");
                }
            }
        }

        registration.setStatus(status);
    }

    /**
     * Add an application withdrawal to the project.
     *
     * @param projectId     project id.
     * @param applicationId applicant id.
     * @throws RuntimeException If the application is not found.
     */
    public void requestWithdrawApplication(String projectId, String applicationId) throws RuntimeException {
        final Optional<BTOProject> projectOpt = getProject(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found.");
        }

        final BTOProject project = projectOpt.get();

        final Optional<BTOApplication> applicationOpt = project.getApplication(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found.");
        }
        final BTOApplication application = applicationOpt.get();

        // Check if the application is active.
        final Optional<BTOApplication> activeApplicationOpt = project
                .getActiveApplication(application.getApplicantUserId());
        if (activeApplicationOpt.isEmpty()) {
            throw new RuntimeException(
                    "You do not have any active applications. You can only withdraw your current application.");
        }
        final BTOApplication activeApplication = activeApplicationOpt.get();
        if (!activeApplication.getId().equals(applicationId)) {
            throw new RuntimeException("You can only withdraw your current application.");
        }

        final Optional<BTOApplicationWithdrawal> withdrawalOpt = project.getActiveWithdrawal(applicationId);
        if (withdrawalOpt.isPresent()) {
            throw new RuntimeException("There is already a pending withdrawal request.");
        }

        final BTOApplicationWithdrawal withdrawal = new BTOApplicationWithdrawal(
                UUID.randomUUID().toString(),
                applicationOpt.get().getId(),
                BTOApplicationWithdrawalStatus.PENDING);
        project.addWithdrawal(withdrawal);
    }

    /**
     * Get all valid withdrawal state transitions.
     *
     * @param currentStatus current status of the withdrawal.
     * @return set of valid application state transitions.
     */
    public Set<BTOApplicationWithdrawalStatus> getValidWithdrawalStateTransitions(BTOApplicationWithdrawalStatus currentStatus) {
        if (currentStatus == BTOApplicationWithdrawalStatus.PENDING) {
            return Set.of(
                    BTOApplicationWithdrawalStatus.SUCCESSFUL,
                    BTOApplicationWithdrawalStatus.UNSUCCESSFUL);
        }
        return Set.of();
    }

    /**
     * Transition an application withdrawal status.
     *
     * @param projectId     project id.
     * @param applicationId application id.
     * @param status        new status.
     * @throws RuntimeException If the application
     *                          - Is not found.
     *                          - There is no pending withdrawal request.
     */
    public void transitionWithdrawApplicationStatus(String projectId, String applicationId,
            BTOApplicationWithdrawalStatus status)
            throws RuntimeException {
        final Optional<BTOProject> projectOpt = getProject(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found.");
        }
        final BTOProject project = projectOpt.get();

        final Optional<BTOApplication> applicationOpt = project.getApplication(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found.");
        }
        final BTOApplication application = applicationOpt.get();

        // Check if the application is active.
        final Optional<BTOApplication> activeApplicationOpt = project
                .getActiveApplication(application.getApplicantUserId());
        if (activeApplicationOpt.isEmpty()) {
            throw new RuntimeException("No active applications.");
        }
        final BTOApplication activeApplication = activeApplicationOpt.get();
        if (!activeApplication.getId().equals(applicationId)) {
            throw new RuntimeException("No active applications.");
        }

        final Optional<BTOApplicationWithdrawal> withdrawalOpt = project.getActiveWithdrawal(applicationId);
        if (withdrawalOpt.isEmpty()) {
            throw new RuntimeException("There is no pending withdrawal request.");
        }
        final BTOApplicationWithdrawal withdrawal = withdrawalOpt.get();
        final BTOApplicationWithdrawalStatus currentStatus = withdrawal.getStatus();
        final Set<BTOApplicationWithdrawalStatus> validStatuses = getValidWithdrawalStateTransitions(currentStatus);
        if (!validStatuses.contains(status)) {
            throw new RuntimeException("Invalid transition from " + currentStatus + " to " + status
                    + ". Only allowed to transition from " + currentStatus + " to " + validStatuses + ".");
        }

        if (status == BTOApplicationWithdrawalStatus.SUCCESSFUL) {
            withdrawal.setStatus(BTOApplicationWithdrawalStatus.SUCCESSFUL);
            application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
        } else if (status == BTOApplicationWithdrawalStatus.UNSUCCESSFUL) {
            withdrawal.setStatus(BTOApplicationWithdrawalStatus.UNSUCCESSFUL);
        } else {
            throw new RuntimeException("Invalid status.");
        }
    }

    public static class BTOFullOfficerRegistration {
        private final BTOProject project;
        private final HDBOfficerRegistration registration;

        public BTOFullOfficerRegistration(BTOProject project, HDBOfficerRegistration registration) {
            this.project = project;
            this.registration = registration;
        }

        public BTOProject getProject() {
            return project;
        }

        public HDBOfficerRegistration getRegistration() {
            return registration;
        }
    }

    public List<BTOFullOfficerRegistration> getAllOfficerRegistrations(String officerUserId) {
        return projects.values().stream()
                .map(project -> {
                    Optional<HDBOfficerRegistration> registrationOpt = project
                            .getActiveOfficerRegistration(officerUserId);
                    return registrationOpt.map(
                            hdbOfficerRegistration -> new BTOFullOfficerRegistration(project, hdbOfficerRegistration))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<BTOProject> getOfficerManagingProjects(String officerUserId) {
        return projects.values().stream()
                .filter(project -> project.isManagingOfficer(officerUserId))
                .toList();
    }
}
