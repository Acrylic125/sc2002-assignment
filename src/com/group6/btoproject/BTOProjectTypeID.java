package com.group6.btoproject;

/**
 * Represents the id for a {@link BTOProjectType}.
 * (i.e. 2 Room, 3 Room).
 */
public enum BTOProjectTypeID {
    S_2_ROOM("2 Room"), S_3_ROOM("3 Room");

    private final String name;

    /**
     * Constructor for BTOProjectTypeID.
     *
     * @param name name of the project type.
     */
    BTOProjectTypeID(String name) {
        this.name = name;
    }

    /**
     * Name Getter.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }
}
