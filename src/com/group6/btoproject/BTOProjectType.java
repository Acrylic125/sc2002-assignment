package com.group6.btoproject;

/**
 * Represents a {@link  BTOProject} types.
 * (i.e. 2 Room, 3 ROom)
 */
public final class BTOProjectType {

    private final String id;
    private final double price;
    private final int maxQuantity;

    /**
     * Constructor for BTOProjectType
     *
     * @param id     id of the project type.
     * @param price    price of the project type.
     * @param maxQuantity quantity of the project type.
     */
    public BTOProjectType(String id, double price, int maxQuantity) {
        this.id = id;
        this.price = price;
        this.maxQuantity = maxQuantity;
    }

    /**
     * Name getter
     *
     * @return {@link #id}
     */
    public String getId() {
        return id;
    }

    /**
     * Price getter
     *
     * @return {@link #price}
     */
    public double getPrice() {
        return price;
    }

    /**
     * Quantity getter
     *
     * @return {@link #maxQuantity}
     */
    public int getMaxQuantity() {
        return maxQuantity;
    }

}
