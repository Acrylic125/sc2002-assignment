package com.group6.btoproject;

/**
 * Represents an applicant's request to withdraw from a BTO Application.
 * See {@link BTOApplicationWithdrawalStatus} for possible statuses.
 */
public class BTOApplicationWithdrawal {

    private final String id;
    private final String applicationId;
    private BTOApplicationWithdrawalStatus status;

    /**
     * Constructor for BTOApplicationWithdrawal.
     *
     * @param id            id of the withdrawal.
     * @param applicationId application id tied to this withdrawal request.
     * @param status        status of this withdrawal request.
     */
    protected BTOApplicationWithdrawal(String id, String applicationId, BTOApplicationWithdrawalStatus status) {
        this.id = id;
        this.applicationId = applicationId;
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

}
