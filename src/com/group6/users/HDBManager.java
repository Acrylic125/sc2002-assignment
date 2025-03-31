public class HDBManager extends User{
    public HDBManager(String name, String nric, int age, String maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
    }

    @Override
    public String getRole() {
        return "Officer";
    }
}
