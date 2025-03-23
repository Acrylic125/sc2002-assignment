package com.group6.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * User manager to manage/access users for the {@link com.group6.BTOSystem}
 *
 * See {@link User}.
 * See {@link com.group6.BTOSystem}.
 */
public class UserManager {

    // Map<User Id, User>
    private final Map<String, User> users = new HashMap<>();

    /**
     * Constructor for UserManager.
     */
    public UserManager() {
    }

    /**
     * Users getter.
     *
     * @return {@link #users}
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Get a user by id.
     *
     * @param id id of the project.
     * @return user with the id.
     */
    public Optional<User> getUser(String id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Add a user to the manager.
     *
     * @param user user to be added.
     */
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

}
