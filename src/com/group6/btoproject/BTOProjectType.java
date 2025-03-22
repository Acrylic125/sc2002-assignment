package com.group6.btoproject;

/**
 * Represents a {@link  BTOProject} types.
 * (i.e. 2 Room, 3 ROom)
 */
public final class BTOProjectType {

    private final String id;
    private final double price;
    private final int quantity;

    /**
     * Constructor for BTOProjectType
     *
     * @param id     id of the project type.
     * @param price    price of the project type.
     * @param quantity quantity of the project type.
     */
    public BTOProjectType(String id, double price, int quantity) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
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
     * @return {@link #quantity}
     */
    public int getQuantity() {
        return quantity;
    }

}
