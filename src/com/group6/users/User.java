package com.group6.users;

/**
 * See {@link RoleBasedUser}
 */
public abstract class User {
    private final String id;
    private String name;
    private String nric;
    private int age;
    private UserMaritalStatus maritalStatus;
    private String password;

    /**
     * Constructor for User.
     *
     * @param id         id.
     * @param name       name.
     * @param nric       nric.
     * @param age        age.
     * @param maritalStatus maritalStatus.
     * @param password password.
     */
    public User(String id, String name, String nric, int age, UserMaritalStatus maritalStatus, String password) {
        this.id = id;
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
    }

    /**
     * Abstract method to get permissions.
     *
     * @return {@link UserPermissions}
     */
    public abstract UserPermissions getPermissions();

    /**
     * Id getter
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param name name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Name getter
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Nric getter.
     *
     * @return {@link #nric}
     */
    public String getNric() {
        return nric;
    }

    /**
     * Nric setter.
     *
     * @param nric nric.
     */
    public void setNric(String nric) {
        this.nric = nric;
    }

    /**
     * Age getter.
     *
     * @return {@link #age}
     */
    public int getAge() {
        return age;
    }

    /**
     * Age setter.
     *
     * @param age age.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Marital status getter.
     *
     * @return {@link #maritalStatus}
     */
    public UserMaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Marital status setter.
     *
     * @param maritalStatus martialStatus.
     */
    public void setMartialStatus(UserMaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Password getter.
     *
     * @return {@link #password}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Password setter.
     *
     * @param newPassword newPassword.
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nric='" + nric + '\'' +
                ", age=" + age +
                ", maritalStatus=" + maritalStatus +
                ", password='" + password + '\'' +
                '}';
    }
}