package com.group6.users;

public class RoleBasedUser extends User {

    private UserRole userRole;

    /**
     * Constructor for User.
     *
     * @param id            id.
     * @param name          name.
     * @param nric          nric.
     * @param age           age.
     * @param maritalStatus maritalStatus.
     * @param password      password.
     */
    public RoleBasedUser(UserRole userRole, String id, String name, String nric, int age, UserMaritalStatus maritalStatus, String password) {
        super(id, name, nric, age, maritalStatus, password);
        this.userRole = userRole;
    }

    @Override
    public UserPermissions getPermissions() {
        return userRole.getPermissions();
    }

    /**
     * User role getter.
     *
     * @return {@link #userRole}
     */
    public UserRole getRole() {
        return userRole;
    }

    /**
     *
     * @param userRole userRole.
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "RoleBasedUser{" +
                "userRole=" + userRole +
                "} " + super.toString();
    }
}
