package com.group6.btoproject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BTOProject {

    private final String id;
    // Map: <Project Type Id, Project Type>
    private final Map<String, BTOProjectType> projectTypes = new HashMap<>();
    // Map <Enquiry Id, Enquiry>
    private final Map<String, BTOEnquiry> enquiries = new HashMap<>();
    private final String managerUserId;
    // Map <HDB Officer Registration Id, HDB Officer Registration>
    private final Map<String, HDBOfficerRegistration> hdbOfficerRegistrations = new HashMap<>();
    private int officerLimit;
    private long applicationOpenTimestamp, applicationCloseTimestamp;

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
    public Map<String, BTOProjectType> getProjectTypes() {
        return projectTypes;
    }

    /**
     * Enquiries getter
     *
     * @return {@link #enquiries}
     */
    public Map<String, BTOEnquiry> getEnquiries() {
        return enquiries;
    }

    /**
     * HdbOfficerRegistrations getter
     * Due to the nature of how HDBOfficerRegistration states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #hdbOfficerRegistrations}
     */
    public List<HDBOfficerRegistration> getHdbOfficerRegistrations() {
        return hdbOfficerRegistrations.values().stream().toList();
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
     * OfficerLimit getter
     *
     * @return {@link #officerLimit}
     */
    public int getOfficerLimit() {
        return officerLimit;
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
     * Add an enquiry to the project.
     *
     * @param enquiry enquiry to add.
     */
    public void addEnquiry(BTOEnquiry enquiry) {
        enquiries.put(enquiry.getId(), enquiry);
    }

    /**
     * Get the user's active officer registration.
     * An active officer registration is one that is either successful or pending.
     *
     * @param officerUserId officer user id.
     * @return active officer registration.
     */
    public Optional<HDBOfficerRegistration> getActiveOfficerRegistration(String officerUserId) {
        return hdbOfficerRegistrations.values().stream()
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
     * @param officerUserId HDB officer registration to add.
     * @throws RuntimeException If there exists an officer registration that:
     *                          - is SUCCESSFUL and has the same officerUserId (i.e.
     *                          already managing this project).
     *                          - is PENDING and has the same officerUserId.
     */
    public void registerOfficer(String officerUserId) throws RuntimeException {
        getActiveOfficerRegistration(officerUserId)
                .ifPresent(registration -> {
                    final HDBOfficerRegistrationStatus status = registration.getStatus();
                    if (status == HDBOfficerRegistrationStatus.SUCCESSFUL) {
                        throw new RuntimeException("Officer is already managing this project.");
                    } else {
                        throw new RuntimeException("Officer registration is pending.");
                    }
                });
        final HDBOfficerRegistration registration = new HDBOfficerRegistration(
                UUID.randomUUID().toString(),
                officerUserId,
                HDBOfficerRegistrationStatus.PENDING);
        hdbOfficerRegistrations.put(registration.getId(), registration);
    }

    public void updateOfficerRegistrationStatus(String registrationId, HDBOfficerRegistrationStatus status)
            throws RuntimeException {
        final HDBOfficerRegistration registration = hdbOfficerRegistrations.get(registrationId);
        if (registration == null) {
            throw new RuntimeException("Registration not found.");
        }
        registration.setStatus(status);
    }

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
        return hdbOfficerRegistrations.values().stream()
                .filter(registration -> registration.getStatus() == HDBOfficerRegistrationStatus.SUCCESSFUL)
                .toList();
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

}
