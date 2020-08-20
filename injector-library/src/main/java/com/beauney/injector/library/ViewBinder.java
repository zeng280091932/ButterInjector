package com.beauney.injector.library;

/**
 * @author zengjiantao
 * @since 2020-08-20
 */
public interface ViewBinder<T> {
    void bind(T target);
}
