package com.group6.users;

/**
 * See {@link Applicant}
 * See {@link HDBOfficer}
 * See {@link HDBManager}
 */
public abstract class User {

    private String username;
    private String password;

    /**
     * Constructor for User
     *
     * @param username username
     * @param password password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for {{@link #username}}.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username username.
     */
    public void setUsername(String username) {
        this.username = username;
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
