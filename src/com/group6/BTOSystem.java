package com.group6;

import com.group6.btoproject.BTOProjectManager;
import com.group6.users.UserManager;

/**
 * Holds the entire BTO System state.
 * See {@link com.group6.btoproject.BTOProjectManager}.
 * See {@link com.group6.users.UserManager}.
 */
public class BTOSystem {

    private final BTOProjectManager projects;
    private final UserManager users;

    public BTOSystem(BTOProjectManager projects, UserManager users) {
        this.projects = projects;
        this.users = users;
    }

    /**
     * Project manager getter.
     *
     * @return {@link #projects}
     */
    public BTOProjectManager getProjects() {
        return projects;
    }

    /**
     * User manager getter.
     *
     * @return {@link #users}
     */
    public UserManager getUsers() {
        return users;
    }

}
