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
    public HDBManager(String id, String nric, int age, UserMaritalStatus maritalStatus, String password) {
        super(id, nric, age, maritalStatus, password);
    }

    /**
    * Return the role of the user.
    *
    * @return A string representing a role, which is "Manager".
    */
    @Override
    public UserRole getRole() {
        return UserRole.MANAGER;
    }
}
