package com.group6;

import com.group6.btoproject.BTOProjectManager;
import com.group6.users.UserManager;

/**
 * Holds the entire BTO System state
 *
 * See {@link com.group6.btoproject.BTOProjectManager}
 * See {@link com.group6.users.UserManager}
 */
public class BTOSystem {

    private final BTOProjectManager projects = new BTOProjectManager();
    private final UserManager users = new UserManager();

    /**
     * Getter for Project Manager
     *
     * @return Project Manager
     */
    public BTOProjectManager getProjects() {
        return projects;
    }

    /**
     * Getter for UserManager
     *
     * @return User Manager
     */
    public UserManager getUsers() {
        return users;
    }
}
