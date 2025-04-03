public class HDBManager extends User{
    /**
     * Constructor for Applicant.
     *
     * @param id id.
     * @param nric nric.
     * @param age age
     * @param maritalStatus marital Status
     * @param password password.
     */
    public HDBManager(String id, String nric, int age, String maritalStatus, String password) {
        super(id, nric, age, maritalStatus, password);
    }

    @Override
    public UserRole getRole() {
        return UserRole.MANAGER;
    }
}
