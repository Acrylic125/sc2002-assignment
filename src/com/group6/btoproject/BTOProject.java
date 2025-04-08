package com.group6.btoproject;

import com.group6.users.User;
import com.group6.users.UserMaritalStatus;

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
    private final Map<BTOProjectTypeID, BTOProjectType> projectTypes = new HashMap<>();
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
     * Constructor for BTOProject.
     *
     * @param id            id of the project.
     * @param managerUserId manager user id.
     */
    public BTOProject(String id, String managerUserId) {
        this.id = id;
        this.managerUserId = managerUserId;
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
     * Name getter.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter.
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Neighbourhood getter.
     *
     * @return {@link #neighbourhood}
     */
    public String getNeighbourhood() {
        return neighbourhood;
    }

    /**
     * Neighbourhood setter.
     *
     * @param neighbourhood neighbourhood
     */
    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    /**
     * ManagerUserId getter.
     *
     * @return {@link #managerUserId}
     */
    public String getManagerUserId() {
        return managerUserId;
    }

    /**
     * ProjectTypes getter.
     *
     * @return {@link #projectTypes}
     */
    public List<BTOProjectType> getProjectTypes() {
        return projectTypes.values().stream().toList();
    }

    /**
     * Get a ProjectType based on it's id.
     *
     * @return {@link BTOProjectType}
     */
    public Optional<BTOProjectType> getProjectType(BTOProjectTypeID projectTypeId) {
        return Optional.ofNullable(projectTypes.get(projectTypeId));
    }

    /**
     * Enquiries getter.
     * Create copy to avoid direct mutations.
     *
     * @return {@link #enquiries}
     */
    public List<BTOEnquiry> getEnquiries() {
        return Collections.unmodifiableList(enquiries);
    }

    /**
     * Applications getter.
     * Create copy to avoid direct mutations.
     * Due to the nature of how BTOApplication states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #applications}
     */
    public List<BTOApplication> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    /**
     * Do not expose this method outside of this package to avoid
     * undefined states.
     * Add an application to the project.
     *
     * @param application application to add.
     */
    protected void addApplication(BTOApplication application) {
        applications.add(application);
    }

    /**
     * HdbOfficerRegistrations getter.
     * Due to the nature of how HDBOfficerRegistration states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #hdbOfficerRegistrations}
     */
    public List<HDBOfficerRegistration> getHdbOfficerRegistrations() {
        return Collections.unmodifiableList(hdbOfficerRegistrations);
    }

    /**
     * Withdrawals getter.
     * Due to the nature of how BTOApplicationWithdrawal states are inferred, we
     * avoid exposing the map which can be mutated directly.
     *
     * @return {@link #withdrawals}
     */
    public List<BTOApplicationWithdrawal> getWithdrawals() {
        return Collections.unmodifiableList(withdrawals);
    }

    /**
     * Get withdrawal by id.
     *
     * @param withdrawalId withdrawal id.
     * @return the withdrawal tied to the withdrawal id.
     */
    public Optional<BTOApplicationWithdrawal> getWithdrawal(String withdrawalId) {
        return withdrawals.stream()
                .filter(withdrawal -> withdrawal.getId().equals(withdrawalId))
                .findFirst();
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
     * @param applicantUserId applicant user id.
     * @return true if the applicant is booked for this project.
     */
    public boolean isApplicantBooked(String applicantUserId) {
        return applications.stream()
                .anyMatch((application) -> application.getApplicantUserId().equals(applicantUserId)
                        && application.getStatus() == BTOApplicationStatus.BOOKED);
    }

    /**
     *
     * @param typeId type id.
     * @return count of booked applications (Count of occupied)
     * @throws RuntimeException if the project type does not exist.
     */
    public int getBookedCountForType(BTOProjectTypeID typeId) throws RuntimeException {
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

    /**
     * Delete an enquiry from the project.
     *
     * @param enquiryId enquiry id.
     */
    public void deleteEnquiry(String enquiryId) {
        enquiries.removeIf(enquiry -> enquiry.getId().equals(enquiryId));
    }

    /**
     * Get an application based on it's id.
     *
     * @param applicationId application id. enquiry id.
     * @return the bto application tied to the application id..
     */
    public Optional<BTOApplication> getApplication(String applicationId) {
        return applications.stream().filter((application) -> application.getId().equals(applicationId)).findFirst();
    }

    /**
     * Add an officer registration to the project.
     *
     * @param registration officer registration to add.
     */
    protected void addHDBOfficerRegistration(HDBOfficerRegistration registration) {
        hdbOfficerRegistrations.add(registration);
    }

    /**
     * Get an officer registration based on it's id.
     * @param registrationId registration id.
     * @return the hdb officer registration tied to the registration id.
     */
    public Optional<HDBOfficerRegistration> getOfficerRegistration(String registrationId) {
        return hdbOfficerRegistrations.stream()
                .filter((registration) -> registration.getId().equals(registrationId))
                .findFirst();
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
     * Add a withdrawal to the project.
     *
     * @param withdrawal withdrawal to add.
     */
    protected void addWithdrawal(BTOApplicationWithdrawal withdrawal) {
        withdrawals.add(withdrawal);
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
     * Check if the application window is open.
     *
     * @return true if the application window is open.
     */
    public boolean isApplicationWindowOpen() {
        final long currentTime = System.currentTimeMillis();
        return currentTime >= applicationOpenTimestamp && currentTime <= applicationCloseTimestamp;
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

    /**
     * Check the eligibility of the user applying.
     *
     * Able to apply for a project – cannot apply for multiple projects.
     * - Singles, 35 years old and above, can ONLY apply for 2-Room
     * - Married, 21 years old and above, can apply for any flat types (2-
     * Room or 3-Room)
     *
     * @param user the applicant
     * @return error message if the user cannot apply for a project type.
     */
    public Optional<String> verifyEligibilityToApply(User user, BTOProjectTypeID typeId) {
        if (user.getMaritalStatus().equals(UserMaritalStatus.SINGLE)) {
            if (user.getAge() < 35) {
                return Optional.of("Single applicants can only apply for 2 Room BTO Project after turning 35.");
            }
            if (typeId.equals(BTOProjectTypeID.S_3_ROOM)) {
                return Optional.of("Single applicants can only apply for 2 Room flats.");
            }
        }

        // Hitting this point means married.
        if (user.getAge() < 21) {
            return Optional.of("Married applicants can only apply for any BTO Project after turning 21.");
        }
        return Optional.empty();
    }

}