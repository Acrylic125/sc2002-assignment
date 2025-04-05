package com.group6.users;

/**
 * Represents the marital status of a {@link User}.
 */
public enum UserMaritalStatus {

    MARRIED, SINGLE;

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

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

}
