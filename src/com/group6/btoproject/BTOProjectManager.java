package com.group6.btoproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a central point to manage/access projects of a
 * {@link com.group6.BTOSystem}
 *
 * See {@link BTOProject}.
 * See {@link com.group6.BTOSystem}.
 */
public class BTOProjectManager {

    // Map<String Id, BTOProject>
    private final Map<String, BTOProject> projects = new HashMap<>();

    /**
     * Constructor for BTOProjectManager.
     */
    public BTOProjectManager() {
    }

    /**
     * Projects getter.
     *
     * @return {@link #projects}
     */
    public Map<String, BTOProject> getProjects() {
        return projects;
    }

    /**
     * Get a project by id.
     *
     * @param id id of the project.
     * @return project with the id.
     */
    public Optional<BTOProject> getProject(String id) {
        return Optional.ofNullable(projects.get(id));
    }

    /**
     * Add a project to the manager.
     *
     * @param project project to be added.
     */
    public void addProject(BTOProject project) {
        projects.put(project.getId(), project);
    }

    /**
     * Get all active projects for a user.
     * Although a user SHOULD only have one active application for a project,
     * there is no guarantee that this is the case on the code level.
     *
     * @param userId id of the user.
     * @return list of active projects for the user.
     */
    public List<BTOProject> getActiveProjectsForUser(String userId) {
        return projects.values().stream()
                .filter(p -> p.getActiveApplication(userId).isPresent())
                .toList();
    }

}
