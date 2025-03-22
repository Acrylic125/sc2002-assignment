package com.group6.users;

/**
 * A HDBManager is a {@link User}
 */
public class Applicant extends User {

    /**
     * Constructor for Applicant
     *
     * @param id id
     * @param nric nric
     * @param password password
     */
    public Applicant(String id, String nric, String password) {
        super(id, nric, password);
    }
}
