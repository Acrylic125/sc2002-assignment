package com.group6.btoproject;

/**
 * Represents a booking receipt generated upon a booking.
 */
public class BTOBookingReceipt {

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

    protected BTOBookingReceipt(String id, String applicationId, String projectId, String userId) {
        this.id = id;
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    protected void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectNeighbourhood() {
        return projectNeighbourhood;
    }

    protected void setProjectNeighbourhood(String projectNeighbourhood) {
        this.projectNeighbourhood = projectNeighbourhood;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    protected void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getNric() {
        return nric;
    }

    protected void setNric(String nric) {
        this.nric = nric;
    }

    public BTOProjectTypeID getTypeID() {
        return typeID;
    }

    protected void setTypeID(BTOProjectTypeID typeID) {
        this.typeID = typeID;
    }

    public long getDateOfBooking() {
        return dateOfBooking;
    }

    protected void setDateOfBooking(long dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    public double getPrice() {
        return price;
    }

    protected void setPrice(double price) {
        this.price = price;
    }
}
