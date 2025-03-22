package com.group6.btoproject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BTOProjectManager {

    // Map<String Id, BTOProject>
    private final Map<String, BTOProject> projects = new HashMap<>();

    /**
     * Constructor for BTOProjectManager
     */
    public BTOProjectManager() {
    }

    /**
     * Projects getter
     *
     * @return {@link #projects}
     */
    public Map<String, BTOProject> getProjects() {
        return projects;
    }

    /**
     * Get a project by id
     *
     * @param id id of the project.
     * @return project with the id.
     */
    public Optional<BTOProject> getProject(String id) {
        return Optional.ofNullable(projects.get(id));
    }

    /**
     * Add a project to the manager
     *
     * @param project project to be added.
     */
    public void addProject(BTOProject project) {
        projects.put(project.getId(), project);
    }

}
