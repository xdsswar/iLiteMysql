package com.inv.xflux.model.in;

import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * @author XDSSWAR
 * Do not Modify this @interface
 * @param <T> Entity Needed
 */
public interface IModel<T> {
    void insert(T val);
    void delete(T val);
    void update(T oldVal, T newVal);
    ObservableList<T> getAll() ;
    T get(long id);
}
