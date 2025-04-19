package com.group6.utils;

import java.util.List;

/**
 * Generic storage interface for loading and saving data.
 *
 * @param <T> The type of data to be stored.
 */
public interface Storage<T> {

    /**
     * Loads all data from the storage.
     *
     * @return A list of all data items.
     */
    List<T> loadAll();

    /**
     * Saves all data to the storage.
     *
     * @param data A list of data items to be saved.
     */
    void saveAll(List<T> data);

    /**
     * Saves a single data item to the storage.
     *
     * @param data The data item to be saved.
     */
    void save(T data);

}
