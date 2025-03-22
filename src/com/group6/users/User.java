package com.group6.users;

/**
 * See {@link Applicant}
 * See {@link HDBOfficer}
 * See {@link HDBManager}
 */
public abstract class User {

    private final String id;
    private String nric;
    private String password;

    /**
     * Constructor for User
     *
     * @param id id
     * @param nric username
     * @param password password
     */
    public User(String id, String nric, String password) {
        this.id = id;
        this.nric = nric;
        this.password = password;
    }

    /**
     * Getter for {{@link #id}}.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for {{@link #nric}}.
     *
     * @return nric
     */
    public String getNRIC() {
        return nric;
    }

    /**
     *
     * @param nric nric.
     */
    public void setNric(String nric) {
        this.nric = nric;
    }

    /**
     * Getter for {{@link #password}}.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
