package com.group6.btoproject;

import java.util.*;

/**
 * BTO Project class.
 * See {@link BTOProjectType}
 * See {@link BTOEnquiry},
 * See {@link BTOApplication}.
 * See {@link HDBOfficerRegistration}.
 * See {@link BTOApplicationWithdrawal}.
 */
public class BTOProject {

    private final String id;
    private String name;
    private String neighbourhood;
    // Map: <Project Type Id, Project Type>
    private final Map<String, BTOProjectType> projectTypes = new HashMap<>();
    private final List<BTOEnquiry> enquiries = new LinkedList<>();
    private final List<BTOApplication> applications = new LinkedList<>();
    private final String managerUserId;
    private final List<HDBOfficerRegistration> hdbOfficerRegistrations = new LinkedList<>();
    private final List<BTOApplicationWithdrawal> withdrawals = new LinkedList<>();
    private int officerLimit;
    // Timestamps for application window. In UTC+8
    private long applicationOpenTimestamp, applicationCloseTimestamp;

    private boolean isVisibleToPublic = true;

    /**
     * Constructor for BTOProject
     *
     * @param id            id of the project.
     * @param managerUserId manager user id.
     */
    public BTOProject(String id, String managerUserId) {
        this.id = id;
        this.managerUserId = managerUserId;
    }

    /**
     * Id getter
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * Name getter
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Neighbourhood getter
     *
     * @return {@link #neighbourhood}
     */
    public String getNeighbourhood() {
        return neighbourhood;
    }

    /**
     * Neighbourhood setter
     *
     * @param neighbourhood neighbourhood
     */
    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    /**
     * ManagerUserId getter
     *
     * @return {@link #managerUserId}
     */
    public String getManagerUserId() {
        return managerUserId;
    }

    /**
     * ProjectTypes getter
     *
     * @return {@link #projectTypes}
     */
    public List<BTOProjectType> getProjectTypes() {
        return projectTypes.values().stream().toList();
    }

    /**
     * Enquiries getter
     * Create copy to avoid direct mutations.
     * 
     * @return {@link #enquiries}
     */
    public List<BTOEnquiry> getEnquiries() {
        return enquiries.stream().toList();
    }

    /**
     * Applications getter
     * Create copy to avoid direct mutations.
     * Due to the nature of how BTOApplication states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #applications}
     */
    public List<BTOApplication> getApplications() {
        return applications.stream().toList();
    }

    /**
     * HdbOfficerRegistrations getter
     * Due to the nature of how HDBOfficerRegistration states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #hdbOfficerRegistrations}
     */
    public List<HDBOfficerRegistration> getHdbOfficerRegistrations() {
        return hdbOfficerRegistrations.stream().toList();
    }

    /**
     * Withdrawals getter
     * Due to the nature of how BTOApplicationWithdrawal states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #withdrawals}
     */
    public List<BTOApplicationWithdrawal> getWithdrawals() {
        return withdrawals.stream().toList();
    }

    /**
     * Add a project type to the project.
     *
     * @param projectType project type to add.
     */
    public void addProjectType(BTOProjectType projectType) {
        projectTypes.put(projectType.getId(), projectType);
    }

    /**
     *
     * @param applicantUserId applicant user id
     * @return true if the applicant is booked for this project.
     */
    public boolean isApplicantBooked(String applicantUserId) {
        return applications.stream()
                .anyMatch((application) -> application.getApplicantUserId().equals(applicantUserId)
                        && application.getStatus() == BTOApplicationStatus.BOOKED);
    }

    /**
     *
     * @param typeId type id
     * @return count of booked applications (Count of occupied)
     * @throws RuntimeException if the project type does not exist.
     */
    public int getBookedCountForType(String typeId) throws RuntimeException {
        final BTOProjectType projectType = projectTypes.get(typeId);
        if (projectType == null) {
            throw new RuntimeException("Project type, " + typeId + " does not exist.");
        }
        return (int) applications.stream()
                .filter((app) -> app.getStatus() == BTOApplicationStatus.BOOKED
                        && Objects.equals(app.getTypeId(), typeId))
                .count();
    }

    /**
     * Add an enquiry to the project.
     *
     * @param enquiry enquiry to add.
     */
    public void addEnquiry(BTOEnquiry enquiry) {
        enquiries.add(enquiry);
    }

    public Optional<BTOApplication> getApplication(String applicationId) {
        return applications.stream().filter((application) -> application.getId().equals(applicationId)).findFirst();
    }

    /**
     * Get the user's active application.
     * An active application is one that is either pending, successful or booked.
     *
     * @param applicantUserId applicant user id.
     * @return active application.
     */
    public Optional<BTOApplication> getActiveApplication(String applicantUserId) {
        return applications.stream()
                .filter(application -> {
                    if (application.getApplicantUserId().equals(applicantUserId)) {
                        final BTOApplicationStatus status = application.getStatus();
                        return status == BTOApplicationStatus.PENDING
                                || status == BTOApplicationStatus.SUCCESSFUL
                                || status == BTOApplicationStatus.BOOKED;
                    }
                    return false;
                })
                .findFirst();
    }

    /**
     * Add an application to the project.
     *
     * @param applicantUserId application to add.
     * @param typeId          type id of the application.
     * @throws RuntimeException If there exists an application that:
     *                          - is PENDING and has the same applicantUserId.
     *                          - is SUCCESSFUL and has the same applicantUserId.
     *                          - is BOOKED and has the same applicantUserId.
     */
    public void requestApply(String applicantUserId, String typeId) throws RuntimeException {
        getActiveApplication(applicantUserId)
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

        final BTOProjectType projectType = projectTypes.get(typeId);
        if (projectType == null) {
            throw new RuntimeException("Project type, " + typeId + " does not exist.");
        }
        if (projectType.getMaxQuantity() <= 0) {
            throw new RuntimeException("Project type, " + typeId + " has no availability.");
        }

        if (isManagingOfficer(applicantUserId)) {
            throw new RuntimeException("Project registered officers cannot apply for this project.");
        }

        final BTOApplication application = new BTOApplication(
                UUID.randomUUID().toString(),
                applicantUserId,
                typeId,
                BTOApplicationStatus.PENDING);
        applications.add(application);
    }

    /**
     * Transition the status of an application.
     *
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
    public void transitionApplicationStatus(String applicationId, BTOApplicationStatus status) throws RuntimeException {
        final Optional<BTOApplication> applicationOpt = getApplication(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found.");
        }
        final BTOApplication application = applicationOpt.get();

        final BTOApplicationStatus currentStatus = application.getStatus();
        if (currentStatus == BTOApplicationStatus.PENDING) {
            if (!(status == BTOApplicationStatus.SUCCESSFUL
                    || status == BTOApplicationStatus.UNSUCCESSFUL)) {
                throw new RuntimeException("Invalid transition from PENDING.");
            }
        } else if (currentStatus == BTOApplicationStatus.SUCCESSFUL) {
            if (!(status == BTOApplicationStatus.BOOKED
                    || status == BTOApplicationStatus.UNSUCCESSFUL)) {
                throw new RuntimeException("Invalid transition from SUCCESSFUL.");
            }
        } else if (currentStatus == BTOApplicationStatus.BOOKED) {
            if (status != BTOApplicationStatus.UNSUCCESSFUL) {
                throw new RuntimeException("Invalid transition from BOOKED.");
            }
        }

        if (status == BTOApplicationStatus.BOOKED || status == BTOApplicationStatus.SUCCESSFUL) {
            final BTOProjectType projectType = projectTypes.get(application.getTypeId());
            if (projectType == null) {
                throw new RuntimeException("Project type, " + application.getTypeId() + " does not exist.");
            }
            int bookedCountForType = getBookedCountForType(application.getTypeId());
            if (bookedCountForType >= projectType.getMaxQuantity()) {
                throw new RuntimeException("Project type, " + application.getTypeId() + " has no availability.");
            }

            if (isManagingOfficer(application.getApplicantUserId())) {
                throw new RuntimeException("Project registered officers cannot apply for this project.");
            }
        }

        application.setStatus(status);
    }

    /**
     * Get the user's active officer registration.
     * An active officer registration is one that is either successful or pending.
     *
     * @param officerUserId officer user id.
     * @return active officer registration.
     */
    public Optional<HDBOfficerRegistration> getActiveOfficerRegistration(String officerUserId) {
        return hdbOfficerRegistrations.stream()
                .filter(registration -> {
                    if (registration.getOfficerUserId().equals(officerUserId)) {
                        final HDBOfficerRegistrationStatus status = registration.getStatus();
                        return status == HDBOfficerRegistrationStatus.SUCCESSFUL
                                || status == HDBOfficerRegistrationStatus.PENDING;
                    }
                    return false;
                })
                .findFirst();
    }

    /**
     * Add an HDB officer registration to the project.
     *
     * @param userId HDB officer registration to add.
     * @throws RuntimeException If there exists an officer registration that:
     *                          - is SUCCESSFUL and has the same officerUserId (i.e.
     *                          already managing this project).
     *                          - is PENDING and has the same officerUserId.
     */
    public void requestRegisterOfficer(String userId) throws RuntimeException {
        getActiveOfficerRegistration(userId)
                .ifPresent(registration -> {
                    final HDBOfficerRegistrationStatus status = registration.getStatus();
                    if (status == HDBOfficerRegistrationStatus.SUCCESSFUL) {
                        throw new RuntimeException("Officer is already managing this project.");
                    } else {
                        throw new RuntimeException("Officer registration is pending.");
                    }
                });

        if (isApplicantBooked(userId)) {
            throw new RuntimeException(
                    "Project Applicants with approved bookings may not register to manage this project.");
        }

        final HDBOfficerRegistration registration = new HDBOfficerRegistration(
                UUID.randomUUID().toString(),
                userId,
                HDBOfficerRegistrationStatus.PENDING);
        hdbOfficerRegistrations.add(registration);
    }

    /**
     * Transition the status of an officer registration.
     *
     * @param userId user id.
     * @param status new status.
     * @throws RuntimeException If the registration:
     *                          - Is not found.
     *                          - Does NOT follow transition from PENDING TO
     *                          SUCCESSFUL/UNSUCCESSFUL.
     */
    public void transitionOfficerRegistrationStatus(String userId, HDBOfficerRegistrationStatus status)
            throws RuntimeException {
        final Optional<HDBOfficerRegistration> registrationOpt = getActiveOfficerRegistration(userId);
        if (registrationOpt.isEmpty()) {
            throw new RuntimeException("Registration not found.");
        }

        final HDBOfficerRegistration registration = registrationOpt.get();
        if (registration.getStatus() == HDBOfficerRegistrationStatus.PENDING) {
            if (!(status == HDBOfficerRegistrationStatus.SUCCESSFUL
                    || status == HDBOfficerRegistrationStatus.UNSUCCESSFUL)) {
                throw new RuntimeException("Invalid transition from PENDING.");
            }
        } else {
            throw new RuntimeException("Invalid transition from " + registration.getStatus() + " to " + status + ".");
        }

        if (status == HDBOfficerRegistrationStatus.SUCCESSFUL) {
            if (getManagingOfficerRegistrations().size() >= officerLimit) {
                throw new RuntimeException("Officer limit of " + officerLimit + " for this project has been reached.");
            }
            if (isApplicantBooked(userId)) {
                throw new RuntimeException(
                        "Project Applicants with approved bookings may not register to manage this project.");
            }
        }

        registration.setStatus(status);
    }

    /**
     * Determine if the user is a successfully registered officer.
     * 
     * @param officerUserId officer user id.
     * @return true if the user is a successfully registered officer.
     */
    public boolean isManagingOfficer(String officerUserId) {
        return getManagingOfficerRegistrations().stream()
                .anyMatch(registration -> registration.getOfficerUserId().equals(officerUserId));
    }

    /**
     * Get all managing officer registrations that are successful.
     *
     * @return list of managing officer registrations that are successful.
     */
    public List<HDBOfficerRegistration> getManagingOfficerRegistrations() {
        return hdbOfficerRegistrations.stream()
                .filter(registration -> registration.getStatus() == HDBOfficerRegistrationStatus.SUCCESSFUL)
                .toList();
    }

    /**
     * Get the user's active withdrawal.
     * An active withdrawal is one that is pending.
     *
     * @param applicationId application id.
     * @return active withdrawal.
     */
    public Optional<BTOApplicationWithdrawal> getActiveWithdrawal(String applicationId) {
        return withdrawals.stream()
                .filter(withdrawal -> {
                    if (withdrawal.getApplicationId().equals(applicationId)) {
                        final BTOApplicationWithdrawalStatus status = withdrawal.getStatus();
                        return status == BTOApplicationWithdrawalStatus.PENDING;
                    }
                    return false;
                })
                .findFirst();
    }

    /**
     * Add an application withdrawal to the project.
     *
     * @param applicationId applicant id.
     * @throws RuntimeException If the application is not found.
     */
    public void requestWithdrawApplication(String applicationId) throws RuntimeException {
        final Optional<BTOApplication> applicationOpt = getApplication(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found.");
        }
        final BTOApplication application = applicationOpt.get();

        // Check if the application is active.
        final Optional<BTOApplication> activeApplicationOpt = getActiveApplication(application.getApplicantUserId());
        if (activeApplicationOpt.isEmpty()) {
            throw new RuntimeException(
                    "You do not have any active applications. You can only withdraw your current application.");
        }
        final BTOApplication activeApplication = activeApplicationOpt.get();
        if (!activeApplication.getId().equals(applicationId)) {
            throw new RuntimeException("You can only withdraw your current application.");
        }

        final Optional<BTOApplicationWithdrawal> withdrawalOpt = getActiveWithdrawal(applicationId);
        if (withdrawalOpt.isPresent()) {
            throw new RuntimeException("There is already a pending withdrawal request.");
        }

        final BTOApplicationWithdrawal withdrawal = new BTOApplicationWithdrawal(
                UUID.randomUUID().toString(),
                applicationOpt.get().getId(),
                BTOApplicationWithdrawalStatus.PENDING);
        withdrawals.add(withdrawal);
    }

    /**
     * Transition an application withdrawal status.
     *
     * @param applicationId application id.
     * @param status        new status.
     * @throws RuntimeException If the application
     *                          - Is not found.
     *                          - There is no pending withdrawal request.
     */
    public void transitionWithdrawApplicationStatus(String applicationId, BTOApplicationWithdrawalStatus status)
            throws RuntimeException {
        final Optional<BTOApplication> applicationOpt = getApplication(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new RuntimeException("Application not found.");
        }
        final BTOApplication application = applicationOpt.get();

        // Check if the application is active.
        final Optional<BTOApplication> activeApplicationOpt = getActiveApplication(application.getApplicantUserId());
        if (activeApplicationOpt.isEmpty()) {
            throw new RuntimeException("No active applications.");
        }
        final BTOApplication activeApplication = activeApplicationOpt.get();
        if (!activeApplication.getId().equals(applicationId)) {
            throw new RuntimeException("No active applications.");
        }

        final Optional<BTOApplicationWithdrawal> withdrawalOpt = getActiveWithdrawal(applicationId);
        if (withdrawalOpt.isEmpty()) {
            throw new RuntimeException("There is no pending withdrawal request.");
        }
        if (withdrawalOpt.get().getStatus() != BTOApplicationWithdrawalStatus.PENDING) {
            throw new RuntimeException("There is no pending withdrawal request.");
        }

        final BTOApplicationWithdrawal withdrawal = withdrawalOpt.get();
        if (status == BTOApplicationWithdrawalStatus.SUCCESSFUL) {
            withdrawal.setStatus(BTOApplicationWithdrawalStatus.SUCCESSFUL);
            application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
        } else if (status == BTOApplicationWithdrawalStatus.UNSUCCESSFUL) {
            withdrawal.setStatus(BTOApplicationWithdrawalStatus.UNSUCCESSFUL);
        } else {
            throw new RuntimeException("Invalid status.");
        }
    }

    /**
     * ApplicationOpenTimestamp getter
     *
     * @return {@link #applicationOpenTimestamp}
     */
    public Date getApplicationOpenDate() {
        return new Date(applicationOpenTimestamp);
    }

    /**
     * ApplicationCloseTimestamp getter
     *
     * @return {@link #applicationCloseTimestamp}
     */
    public Date getApplicationCloseDate() {
        return new Date(applicationCloseTimestamp);
    }

    /**
     * Set the application open date.
     *
     * @param applicationOpenDate  application open date.
     * @param applicationCloseDate application close date.
     * @throws RuntimeException If the application open date is after the
     *                          application close date.
     */
    public void setApplicationWindow(Date applicationOpenDate, Date applicationCloseDate) throws RuntimeException {
        if (applicationOpenDate.after(applicationCloseDate)) {
            throw new IllegalArgumentException("Application open date cannot be after application close date.");
        }
        this.applicationOpenTimestamp = applicationOpenDate.getTime();
        this.applicationCloseTimestamp = applicationCloseDate.getTime();
    }

    /**
     * OfficerLimit getter
     *
     * @return {@link #officerLimit}
     */
    public int getOfficerLimit() {
        return officerLimit;
    }

    /**
     * Set the officer limit.
     *
     * @param officerLimit officer limit.
     */
    public void setOfficerLimit(int officerLimit) throws RuntimeException {
        if (officerLimit < 0) {
            throw new IllegalArgumentException("Officer limit cannot be negative.");
        }
        this.officerLimit = officerLimit;
    }

    /**
     * isVisibleToPublic getter
     *
     * @return true if this project is visible to the public
     */
    public boolean isVisibleToPublic() {
        return isVisibleToPublic;
    }

    /**
     *
     * @param visibleToPublic visible to public
     */
    public void setVisibleToPublic(boolean visibleToPublic) {
        isVisibleToPublic = visibleToPublic;
    }
}
