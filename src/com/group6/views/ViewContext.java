package com.group6.views;

import com.group6.BTOSystem;
import com.group6.users.User;
import com.group6.views.applicant.ApplicantViewMyApplicationFilters;
import com.group6.views.applicant.ProjectEnquiryView;
import com.group6.views.applicant.ProjectsView;
import com.group6.views.applicant.ProjectsViewFilters;
import com.group6.views.management.ApplicantReportFilters;

import java.util.Optional;
import java.util.Scanner;
import java.util.Stack;

/**
 * Represents the context of a {@link View}.
 */
public final class ViewContext {

    private final BTOSystem btoSystem;
    private final Scanner scanner;
    private final Stack<View> viewStack = new Stack<>();
    private User user;
    private final ProjectsViewFilters viewAllProjectsFilters = new ProjectsViewFilters();
    private final ApplicantViewMyApplicationFilters viewMyApplicationFilters = new ApplicantViewMyApplicationFilters();
    private final ApplicantReportFilters applicantReportFilters = new ApplicantReportFilters();

    /**
     *
     * @param btoSystem bto system.
     * @param scanner   scanner for input.
     */
    public ViewContext(BTOSystem btoSystem, Scanner scanner) {
        this.btoSystem = btoSystem;
        this.scanner = scanner;
    }

    /**
     * BTO System getter.
     *
     * @return {@link #btoSystem}
     */
    public BTOSystem getBtoSystem() {
        return btoSystem;
    }

    /**
     * Getter for scanner. Avoid recreating Scanner instances, use this shared
     * instance.
     *
     * @return {@link #scanner}
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Clear view stack.
     */
    public void clearViewStack() {
        viewStack.clear();
    }

    /**
     *
     * @return The top view.
     * @throws IllegalStateException If there are no more views.
     */
    public View getCurrentView() throws IllegalStateException {
        if (viewStack.isEmpty()) {
            throw new IllegalStateException("View stack is empty.");
        }
        return viewStack.peek();
    }

    /**
     * Start from a view.
     * Within a view, you can navigate to other views by returning in
     * {@link View#render(ViewContext)}.
     * Return null to go back (i.e. cancel).
     * Return a new view instance to go to that view.
     * Return the view (i.e. this) to stay within the view.
     * See {@link ProjectsView} and
     * {@link ProjectEnquiryView} for an
     * example.
     * 
     * @param view root view.
     */
    public void startFromView(View view) throws RuntimeException {
        if (!viewStack.isEmpty()) {
            throw new IllegalStateException(
                    "View stack is not empty. Can only start from a view if it was not started before.");
        }
        viewStack.push(view);
        while (!viewStack.isEmpty()) {
            View current = viewStack.peek();
            View next = current.render(this);
            if (current == next) {
                // View did not change.
                continue;
            }
            if (next == null) {
                viewStack.pop();
            } else {
                viewStack.push(next);
            }
        }
    }

    /**
     * User getter.
     *
     * @return {@link #user} can be null (i.e. not logged in).
     */
    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    /**
     * User setter.
     *
     * @param user user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Projects view filters getter.
     *
     * @return {@link #viewAllProjectsFilters}
     */
    public ProjectsViewFilters getViewAllProjectsFilters() {
        return viewAllProjectsFilters;
    }

    /**
     * Application view filters getter.
     *
     * @return {@link #viewMyApplicationFilters}
     */
    public ApplicantViewMyApplicationFilters getViewApplicationFilters() {
        return viewMyApplicationFilters;
    }

    /**
     * Applicant report filters getter.
     *
     * @return {@link #applicantReportFilters}
     */
    public ApplicantViewMyApplicationFilters getViewMyApplicationFilters() {
        return viewMyApplicationFilters;
    }

    /**
     * Applicant report filters getter.
     *
     * @return {@link #applicantReportFilters}
     */
    public ApplicantReportFilters getApplicantReportFilters() {
        return applicantReportFilters;
    }

}
