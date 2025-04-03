
public class Applicant extends User {

    /**
     * Constructor for Applicant.
     *
     * @param id id.
     * @param nric nric.
     * @param age age
     * @param maritalStatus marital Status
     * @param password password.
     */
    public Applicant(String id, String nric, int age, String maritalStatus, String password) {
        super(id, nric, age, maritalStatus, password);
    }

    public UserRole getRole(){ return UserRole.APPLICANT;}
}
