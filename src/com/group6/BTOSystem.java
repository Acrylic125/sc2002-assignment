package com.group6;

import com.group6.btoproject.BTOProjectManager;
import com.group6.users.UserManager;

/**
 * Holds the entire BTO System state.
 * See {@link com.group6.btoproject.BTOProjectManager}.
 * See {@link com.group6.users.UserManager}.
 */
public class BTOSystem {

    private final BTOProjectManager projectManager;
    private final UserManager userManager;

    public BTOSystem(BTOProjectManager projectManager, UserManager userManager) {
        this.projectManager = projectManager;
        this.userManager = userManager;
    }

    /**
     * Project manager getter.
     *
     * @return {@link #projectManager}
     */
    public BTOProjectManager getProjectManager() {
        return projectManager;
    }

    /**
     * User manager getter.
     *
     * @return {@link #userManager}
     */
    public UserManager getUserManager() {
        return userManager;
    }

}
