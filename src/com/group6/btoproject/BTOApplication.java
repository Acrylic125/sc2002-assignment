package com.group6.btoproject;

import java.io.Serializable;

/**
 * Represents an applicant's BTO application.
 * See {@link BTOApplicationStatus} for possible statuses.
 */
public class BTOApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String applicantUserId;
    private final BTOProjectTypeID typeId;
    private BTOApplicationStatus status;

    /**
     * Constructor for BTOApplication.
     *
     * @param id              id of the application.
     * @param applicantUserId applicant user id.
     * @param typeId          type id of the application.
     * @param status          status of the application.
     */
    protected BTOApplication(String id, String applicantUserId, BTOProjectTypeID typeId, BTOApplicationStatus status) {
        this.id = id;
        this.applicantUserId = applicantUserId;
        this.typeId = typeId;
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
     * ApplicantUserId getter.
     *
     * @return {@link #applicantUserId}
     */
    public String getApplicantUserId() {
        return applicantUserId;
    }

    /**
     * TypeId getter.
     *
     * @return {@link #typeId}
     */
    public BTOProjectTypeID getTypeId() {
        return typeId;
    }

    /**
     * Status getter.
     *
     * @return {@link #status}
     */
    public BTOApplicationStatus getStatus() {
        return status;
    }

    /**
     * Status setter.
     *
     * @param status new status
     */
    protected void setStatus(BTOApplicationStatus status) {
        this.status = status;
    }

}
