package com.group6.users;

import com.group6.utils.Storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages user authentication and retrieval.
 * <p>
 * This class is responsible for loading, storing, and managing users.
 * It interacts with {@code UserStorage} to retrieve and validate users.
 * </p>
 */
public class UserManager {
    private Map<String, User> users = new HashMap<>();
    private final Storage<User> userStorage; // Inject UserStorage instance

    /**
     * Constructs a UserManager instance with a given UserStorage.
     * <p>
     * This constructor initializes the user map by loading users from storage.
     * </p>
     *
     * @param userStorage The storage handler responsible for user data retrieval.
     */
    public UserManager(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Sets the users map.
     *
     * @param users The map of users to set.
     */
    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    /**
     * Sets the users map.
     *
     * @param users The users to set.
     */
    public void setUsers(List<User> users) {
        users.forEach((user) -> {
            this.users.put(user.getId(), user);
        });
    }

    /**
     * Retrieves all users currently managed by the system.
     *
     * @return A map of users keyed by their NRIC.
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
     * Finds a user by NRIC.
     * <p>
     * This method searches for a user based on their unique NRIC identifier.
     * </p>
     *
     * @param nric The NRIC to look up.
     * @return An {@code Optional<User>} containing the user if found, or empty otherwise.
     */
    public Optional<User> getUserByNRIC(String nric) {
        return users.values().stream()
                .filter((user) -> user.getNric().equals(nric))
                .findFirst();
    }

    /**
     * Validates if the given NRIC and password match a registered user.
     * <p>
     * This method checks if the provided credentials match an existing user
     * stored in the system.
     * </p>
     *
     * @param nric     The NRIC entered by the user.
     * @param password The password entered by the user.
     * @return {@code true} if the credentials are valid, {@code false} otherwise.
     */
    public boolean isValidLogin(String nric, String password) {
        return getUserByNRIC(nric)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    /**
     * Registers a new user and saves it to storage.
     *
     * @param user The user to register.
     * @return true if registration is successful, false if NRIC is already taken.
     */
    public boolean registerUser(User user) {
        if (users.containsKey(user.getNric())) {
            return false; // NRIC already exists
        }
        users.put(user.getId(), user);
        return true;
    }

    /**
     * Saves the user to storage.
     *
     * @return {@link #userStorage}
     */
    public Storage<User> getUserStorage() {
        return userStorage;
    }
}
