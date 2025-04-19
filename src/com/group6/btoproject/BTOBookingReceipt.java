package com.group6.btoproject;

import java.io.Serializable;

/**
 * Represents a booking receipt generated upon a booking.
 */
public class BTOBookingReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String applicationId;
    private final String projectId;
    private final String userId;

    private String applicantName;
    private String projectName;
    private String projectNeighbourhood;
    private String nric;
    private BTOProjectTypeID typeID;
    private long dateOfBooking;
    private double price;

    /**
     * Constructor for BTOBookingReceipt.
     *
     * @param id The id of the booking receipt.
     * @param applicationId The id of the application associated with this booking receipt.
     * @param projectId The id of the project associated with this booking receipt.
     * @param userId The id of the user associated with this booking receipt.
     */
    protected BTOBookingReceipt(String id, String applicationId, String projectId, String userId) {
        this.id = id;
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.userId = userId;
    }

    /**
     * Id Getter.
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * ProjectId Getter.
     *
     * @return {@link #projectId}
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * ProjectName Getter.
     *
     * @return {@link #projectName}
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * ProjectNeighbourhood Getter.
     *
     * @param projectName projectName
     */
    protected void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * ProjectNeighbourhood Getter.
     *
     * @return {@link #projectNeighbourhood}
     */
    public String getProjectNeighbourhood() {
        return projectNeighbourhood;
    }

    /**
     * ProjectNeighbourhood Setter.
     *
     * @param projectNeighbourhood projectNeighbourhood
     */
    protected void setProjectNeighbourhood(String projectNeighbourhood) {
        this.projectNeighbourhood = projectNeighbourhood;
    }

    /**
     * UserId Getter.
     *
     * @return {@link #userId}
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * UserId Getter.
     *
     * @return {@link #userId}
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ApplicantName Getter.
     *
     * @return {@link #applicantName}
     */
    public String getApplicantName() {
        return applicantName;
    }

    /**
     * ApplicantName Setter.
     *
     * @param applicantName applicantName
     */
    protected void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    /**
     * NRIC Getter.
     *
     * @return {@link #nric}
     */
    public String getNric() {
        return nric;
    }

    /**
     * NRIC Setter.
     *
     * @param nric nric
     */
    protected void setNric(String nric) {
        this.nric = nric;
    }

    /**
     * TypeID Getter.
     *
     * @return {@link #typeID}
     */
    public BTOProjectTypeID getTypeID() {
        return typeID;
    }

    /**
     * TypeID Setter.
     *
     * @param typeID typeID
     */
    protected void setTypeID(BTOProjectTypeID typeID) {
        this.typeID = typeID;
    }

    /**
     * DateOfBooking Getter.
     *
     * @return {@link #dateOfBooking}
     */
    public long getDateOfBooking() {
        return dateOfBooking;
    }

    /**
     * DateOfBooking Setter.
     *
     * @param dateOfBooking dateOfBooking
     */
    protected void setDateOfBooking(long dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    /**
     * Price Getter.
     *
     * @return {@link #price}
     */
    public double getPrice() {
        return price;
    }

    /**
     * Price Setter.
     *
     * @param price price
     */
    protected void setPrice(double price) {
        this.price = price;
    }

}
