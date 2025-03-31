public abstract class User {
    private String name;
    private String nric;
    private int age;
    private String maritalStatus;
    private String password;

    public User(String name, String nric, int age, String maritalStatus, String password){
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
    }

    public abstract UserRole getRole();

    public String getName(){return name;}
    public String getNric(){return nric;}
    public int getAge(){return age;}
    public String getMaritalStatus(){return maritalStatus;}
    public String getPassword(){return password;}

    public void setPassword(String newPassword){this.password = newPassword;}

    public String tofileString(){
        return name + ", " + nric + ", " + age + ", " + maritalStatus + ", " + password;
    }

}
