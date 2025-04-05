package com.group6.users;

/**
 * See {@link Applicant}.
 * See {@link HDBOfficer}.
 * See {@link HDBManager}.
 */

public abstract class User {
    private String name;
    private String nric;
    private int age;
    private UserMaritalStatus maritalStatus;
    private String password;

    /**
     * Constructor for User.
     *
     * @param name       name.
     * @param nric     nric.
     * @param age        age.
     * @param maritalStatus maritalStatus.
     * @param password password.
     */
    public User(String name, String nric, int age, UserMaritalStatus maritalStatus, String password){
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
    }

    /**
     * UserRole getter
     */
    public abstract UserRole getRole();

    /**
     * Name getter
     *
     * @return {@link #name}
     */
    public String getName(){return name;}

    /**
     * Nric getter.
     *
     * @return {@link #nric}
     */
    public String getNric(){return nric;}

    /**
     * Age getter.
     *
     * @return age
     */
    public int getAge(){return age;}

    /**
     * Marital status getter.
     *
     * @return {@link #martialStatus}
     */
    public UserMaritalStatus getMaritalStatus(){return maritalStatus;}

    /**
     * Password getter.
     *
     * @return {@link #password}
     */
    public String getPassword(){return password;}

    /**
     * Password setter.
     *
     * @param password newPassword.
     */
    public void setPassword(String newPassword){this.password = newPassword;}

    /**
    * Save users to file.
    * 
    * @return {@link #name, nric, age, maritalStatus, password}
    */
    public String tofileString(){
        return name + ", " + nric + ", " + age + ", " + maritalStatus.toString() + ", " + password;
    }

}
