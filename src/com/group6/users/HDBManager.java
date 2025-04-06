package com.group6.users;

/**
 * A HDBManager is a {@link User}, but neither is it an {@link Applicant} NOR is
 * it {@link HDBOfficer}.
 */
public class HDBManager extends User {
    /**
     * Constructor for HDBManager.
     *
     * @param id       id.
     * @param nric     nric.
     * @param password password.
     */
    public HDBManager(String id, String nric, String password) {
        super(id, nric, password);
    }

    public String getUserType() { // identifier for HDBManager
        return "HDBManager";
    }
}
