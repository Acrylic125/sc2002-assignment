package com.group6.users;

/**
 * A HDBManager is a {@link User}, but neither is it an {@link Applicant} NOR is it {@link HDBOfficer}
 */
public class HDBManager extends User {
    /**
     * Constructor for HDBManager
     *
     * @param username username
     * @param password password
     */
    public HDBManager(String username, String password) {
        super(username, password);
    }
}
