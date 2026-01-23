package com.martianbank.atmlocator.exception;

/**
 * Base exception class for resource not found scenarios.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
