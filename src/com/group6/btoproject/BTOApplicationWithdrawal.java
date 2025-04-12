package com.group6.btoproject;

import java.io.Serializable;

/**
 * Represents an applicant's request to withdraw from a BTO Application.
 * See {@link BTOApplicationWithdrawalStatus} for possible statuses.
 */
public class BTOApplicationWithdrawal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String applicationId;
    private BTOApplicationWithdrawalStatus status;
    private final long requestedOn;

    /**
     * Constructor for BTOApplicationWithdrawal.
     *
     * @param id            id of the withdrawal.
     * @param applicationId application id tied to this withdrawal request.
     * @param status        status of this withdrawal request.
     * @param requestedOn   timestamp of when the withdrawal was requested.
     */
    protected BTOApplicationWithdrawal(String id, String applicationId, BTOApplicationWithdrawalStatus status, long requestedOn) {
        this.id = id;
        this.applicationId = applicationId;
        this.status = status;
        this.requestedOn = requestedOn;
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
     * ApplicationId getter.
     *
     * @return {@link #applicationId}
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Status getter.
     *
     * @return {@link #status}
     */
    public BTOApplicationWithdrawalStatus getStatus() {
        return status;
    }

    /**
     * Status setter.
     *
     * @param status new status
     */
    protected void setStatus(BTOApplicationWithdrawalStatus status) {
        this.status = status;
    }

    /**
     * RequestedOn getter.
     *
     * @return {@link #requestedOn}
     */
    public long getRequestedOn() {
        return requestedOn;
    }
}
