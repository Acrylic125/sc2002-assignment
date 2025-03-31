public class HDBOfficer extends User{
    public HDBOfficer(String id, String nric, int age, String maritalStatus, String password){
        super(id, nric, age, maritalStatus, password);
    }
    public String getRole(){ return "Officer";}
}
