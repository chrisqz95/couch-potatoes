package com.example.potato.couchpotatoes;

/**
 * A simple interface to allow for callbacks to be passed to functions.
 * @param <T> object type that will be passed to the callback function.
 */
public interface SimpleCallback<T> {
    /**
     * Callback method.
     * @param data passed to callback function.
     */
    void callback(T data);
}
