package com.group6.btoproject;

/**
 * Represents a HDB Officer's request to register as an officer
 * for this project.
 * Note: Due to how the statuses are inferred and how registrations are managed
 * by {@link BTOProject}, this class should only be mutated by
 * {@link BTOProject} to avoid weird application states.
 * See {@link HDBOfficerRegistrationStatus} for possible statuses.
 */
public class HDBOfficerRegistration {

    private final String id;
    private final String officerUserId;
    private HDBOfficerRegistrationStatus status;

    /**
     * Protected, only accessible by classes in the same package.
     * We want to limit the creation of this object to {@link BTOProject}.
     * Constructor for HDBOfficerRegistration.
     *
     * @param id            id of the registration.
     * @param officerUserId officer user id.
     * @param status        status of this registration.
     */
    protected HDBOfficerRegistration(String id, String officerUserId, HDBOfficerRegistrationStatus status) {
        this.id = id;
        this.officerUserId = officerUserId;
        this.status = status;
    }

    /**
     * Id getter.
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * OfficerUserId getter.
     *
     * @return {@link #officerUserId}
     */
    public String getOfficerUserId() {
        return officerUserId;
    }

    /**
     * Status getter.
     *
     * @return {@link #status}
     */
    public HDBOfficerRegistrationStatus getStatus() {
        return status;
    }

    /**
     * Mutations to status should be done through the {@link BTOProject}.
     * Status setter.
     *
     * @param status new status
     */
    protected void setStatus(HDBOfficerRegistrationStatus status) {
        this.status = status;
    }

}
