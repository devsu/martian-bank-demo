package com.martianbank.atmlocator.exception;

/**
 * Exception thrown when no ATMs are found matching the search criteria.
 */
public class AtmNotFoundException extends ResourceNotFoundException {

    private static final String DEFAULT_MESSAGE = "No ATMs found";

    public AtmNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
