package com.group6.utils;

import java.util.List;

public interface Storage<T> {

    List<T> loadAll();

    void saveAll(List<T> data);

    void save(T data);

}
