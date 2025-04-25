package com.trackon.dao;

import java.util.List;

public interface BaseDAO<T> {
    T findById(int id);
    List<T> findAll();
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(int id);
} 