package com.group6.users;

import com.group6.utils.BashColors;
import com.group6.utils.Storage;

import java.io.*;
import java.util.*;

/**
 * Handles the storage and retrieval of user data from text files.
 * <p>
 * This class loads users from role-specific files and provides methods
 * to save user data persistently. It supports Applicants, Officers, and
 * Managers.
 * </p>
 */
public class UserStorage implements Storage<User> {
    private final String applicantsFilepath;
    private final String officersFilepath;
    private final String managersFilepath;

    /**
     * Constructs a UserStorage instance with specified file paths.
     *
     * @param applicantsFilepath The file path for storing applicant data.
     * @param officersFilepath   The file path for storing officer data.
     * @param managersFilepath   The file path for storing manager data.
     */
    public UserStorage(String applicantsFilepath, String officersFilepath, String managersFilepath) {
        this.applicantsFilepath = applicantsFilepath;
        this.officersFilepath = officersFilepath;
        this.managersFilepath = managersFilepath;
    }

    /**
     * Loads all users from the respective files.
     *
     * @return A map of users keyed by NRIC.
     */
    @Override
    public List<User> loadAll() {
        List<User> users = new LinkedList<>();
        loadUsersFromFile(users, applicantsFilepath, UserRole.APPLICANT);
        loadUsersFromFile(users, officersFilepath, UserRole.OFFICER);
        loadUsersFromFile(users, managersFilepath, UserRole.MANAGER);
        return users;
    }

    /**
     * Reads user data from the specified file and adds them to the provided map.
     *
     * @param users    The map where loaded users will be stored.
     * @param filename The file from which users will be loaded.
     * @param role     The role of the users being loaded (Applicant, Officer, or
     *                 Manager).
     */
    private void loadUsersFromFile(List<User> users, String filename, UserRole role) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 6) {
                    System.out.println(BashColors.format(
                            "⚠️ Skipping malformed line " + lineNumber + " in " + filename + ": Invalid data",
                            BashColors.YELLOW));
                    lineNumber++;
                    continue;
                }

                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String nric = parts[2].trim();
                    int age = Integer.parseInt(parts[3].trim());
                    UserMaritalStatus maritalStatus = UserMaritalStatus.valueOf(parts[4].trim().toUpperCase());
                    String password = parts[5].trim();

                    User user = new RoleBasedUser(
                            role, id, name, nric, age, maritalStatus, password);
                    users.add(user);
                } catch (IllegalArgumentException e) {
                    System.out.println(BashColors.format(
                            "Skipping line " + lineNumber + " in " + filename + ": Invalid data", BashColors.YELLOW));
                    System.out.println(BashColors.format("  " + e.getMessage(), BashColors.YELLOW));
                }

                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println(BashColors.format("[Users] Error reading from file: " + filename, BashColors.RED));
            System.out.println(BashColors.format("  " + e.getMessage(), BashColors.RED));
        }
    }

    /**
     * Saves all users to their respective files.
     *
     * @param users The map of users to be saved, keyed by NRIC.
     */
    @Override
    public void saveAll(List<User> users) {
        Map<UserRole, Collection<User>> userRoleUsersMap = new HashMap<>();
        users.forEach(((_user) -> {
            if (!(_user instanceof RoleBasedUser)) {
                System.out.println(BashColors.format(
                        "⚠️ Skipping user, " + _user.getId() + ". Unhandled user type for saving.", BashColors.YELLOW));
                return;
            }
            RoleBasedUser user = (RoleBasedUser) _user;
            Collection<User> usersFromRole = userRoleUsersMap.computeIfAbsent(user.getRole(), k -> new LinkedList<>());
            usersFromRole.add(user); // Add by reference, no need to add back to map.
        }));

        userRoleUsersMap.forEach(((userRole, _users) -> saveUsersToFile(_users, getFilenameForROle(userRole))));
    }

    private String getFilenameForROle(UserRole userRole) {
        return switch (userRole) {
            case APPLICANT -> applicantsFilepath;
            case OFFICER -> officersFilepath;
            case MANAGER -> managersFilepath;
        };
    }

    /**
     * Saves users to the specified file based on their role.
     *
     * @param users    The map of users to be saved.
     * @param filename The file where the user data should be stored.
     */
    private void saveUsersToFile(Collection<User> users, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (User user : users) {
                writer.write(toFileString(user));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(BashColors.format("Error writing to file: " + filename, BashColors.RED));
        }
    }

    private String toFileString(User user) {
        return user.getId() + "," + user.getName() + "," + user.getNric() + "," + user.getAge() + ","
                + user.getMaritalStatus().toString() + "," + user.getPassword();
    }

    /**
     * Saves a single user to the appropriate role-specific file.
     *
     * @param _user The user to save.
     */
    @Override
    public void save(User _user) {
        if (_user == null) {
            return;
        }
        if (!(_user instanceof RoleBasedUser)) {
            System.out.println(BashColors.format(
                    "Skipping user, " + _user.getId() + ". Unhandled user type for saving.", BashColors.YELLOW));
            return;
        }
        RoleBasedUser user = (RoleBasedUser) _user;
        String filename = getFilenameForROle(user.getRole());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(toFileString(user));
            writer.newLine();
        } catch (IOException e) {
            System.out.println(BashColors.format("Error writing to file: " + filename, BashColors.RED));
        }
    }
}
