package com.group6.users;

/**
 * Represents the marital status of a {@link User}.
 */
public enum UserMaritalStatus {

    MARRIED("Married"), SINGLE("Single");

    private final String name;

    /**
     * Constructor for UserMaritalStatus.
     *
     * @param name name of the marital status.
     */
    UserMaritalStatus(String name) {
        this.name = name;
    }

    /**
     * Name getter.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Converts a string to a UserMaritalStatus, case-insensitively.
     *
     * @param value The input string.
     * @return The corresponding enum value, or null if invalid.
     */
    public static UserMaritalStatus fromString(String value) {
        try {
            return UserMaritalStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Converts the enum value to a string.
     *
     * @return The string representation of the enum value.
     */
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

}
