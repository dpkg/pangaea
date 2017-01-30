package com.cvent.pangaea;

/**
 * An interface that allows us to transform/resolve part of an object at runtime.
 *
 * @author bryan
 * @param <T> The type T that will get dynamically resolved at runtime
 */
public interface TemplateResolver<T> {

    /**
     * Resolve will take the value and resolve any dynamic attributes into another instance of value T where anything
     * that was dynamic gets replaced by the resolver implementation.
     *
     * @param key The key name of the value you want to replace
     * @param value The value to try and replace dynamic components
     * @return A resolved value
     */
    T resolve(String key, T value);
}
