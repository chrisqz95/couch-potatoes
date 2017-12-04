package com.example.potato.couchpotatoes;

/**
 * A simple interface to allow for for callbacks
 * @param <T> object to be returned in callback
 */
public interface SimpleCallback<T> {
    void callback(T data);
}
