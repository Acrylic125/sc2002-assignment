package com.group6.btoproject;

import java.util.HashMap;
import java.util.LinkedList;
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

    public static class BTOFullApplication {
        private final BTOProject project;
        private final BTOApplication application;
        private final BTOApplicationWithdrawal withdrawal;

        public BTOFullApplication(BTOProject project, BTOApplication application, BTOApplicationWithdrawal withdrawal) {
            this.project = project;
            this.application = application;
            this.withdrawal = withdrawal;
        }

        public BTOProject getProject() {
            return project;
        }

        public BTOApplication getApplication() {
            return application;
        }

        public Optional<BTOApplicationWithdrawal> getWithdrawal() {
            return Optional.ofNullable(withdrawal);
        }
    }

    /**
     * Get all project applications applied by a user.
     *
     * @param userId id of the user.
     * @return list of projects applied by the user.
     */
    public List<BTOFullApplication> getAllApplicationsForUser(String userId) {
        LinkedList<BTOFullApplication> result = new LinkedList<>();
        projects.values().forEach(project -> {
            project.getApplications().forEach(application -> {
                if (application.getApplicantUserId().equals(userId)) {
                    Optional<BTOApplicationWithdrawal> wotjdrawaOpt = project.getActiveWithdrawal(application.getId());
                    result.add(new BTOFullApplication(project, application, wotjdrawaOpt.orElse(null)));
                }
            });
        });
        return result;
    }

}
