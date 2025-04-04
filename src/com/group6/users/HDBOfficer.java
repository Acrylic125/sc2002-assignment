public class HDBOfficer extends User{
    /**
     * Constructor for Applicant.
     *
     * @param id id.
     * @param nric nric.
     * @param age age
     * @param maritalStatus marital Status
     * @param password password.
     */
    public HDBOfficer(String id, String nric, int age, String maritalStatus, String password){
        super(id, nric, age, maritalStatus, password);
    }

    /**
 * Returns the role of the user.
 *
 * @return A string representing the role, which is "Officer".
 */
    public UserRole getRole(){ return UserRole.OFFICER;}
}
