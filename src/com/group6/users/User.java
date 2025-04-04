package com.group6.users;

/**
 * See {@link Applicant}.
 * See {@link HDBOfficer}.
 * See {@link HDBManager}.
 */
public abstract class User {

    private final String id;
    private String nric;
    private String password;
    private String name;
    private int age;
    private UserMartialStatus martialStatus = UserMartialStatus.SINGLE; // sets default to SINGLE

    /**
     * Constructor for User.
     *
     * @param id       id.
     * @param nric     username.
     * @param password password.
     */
    public User(String id, String nric, String password) {
        this.id = id;
        this.nric = nric;
        this.password = password;
    }

    /**
     * Id getter
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * Nric getter.
     *
     * @return {@link #nric}
     */
    public String getNRIC() {
        return nric;
    }

    /**
     * Nric setter.
     *
     * @param nric nric.
     */
    public void setNric(String nric) {
        this.nric = nric;
    }

    /**
     * Password getter.
     *
     * @return {@link #password}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Password setter.
     *
     * @param password password.
     */
    public void setPassword(String password) {
        this.password = password;
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
     *
     * @param name name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Age getter.
     *
     * @return age
     */
    public int getAge() {
        return age;
    }

    /**
     * Age setter.
     *
     * @param age age.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Marital status getter.
     *
     * @return {@link #martialStatus}
     */
    public UserMartialStatus getMartialStatus() {
        return martialStatus;
    }

    /**
     * Marital status setter.
     *
     * @param martialStatus martialStatus.
     */
    public void setMartialStatus(UserMartialStatus martialStatus) {
        this.martialStatus = martialStatus;
    }

}
