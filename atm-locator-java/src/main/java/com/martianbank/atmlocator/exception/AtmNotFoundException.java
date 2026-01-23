package com.martianbank.atmlocator.exception;

/**
 * Exception thrown when an ATM with a specific ID cannot be found,
 * or when no ATMs match the search criteria.
 */
public class AtmNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "No ATMs found";
    private static final String ATM_INFO_NOT_FOUND_MESSAGE = "ATM information not found";

    /**
     * Constructs an AtmNotFoundException with a default message.
     * Used when no ATMs match the search criteria.
     */
    public AtmNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs an AtmNotFoundException for a specific ATM ID lookup.
     * Uses the message "ATM information not found" as per OpenAPI specification.
     *
     * @param id the ID of the ATM that was not found (not used in message)
     */
    public AtmNotFoundException(String id) {
        super(ATM_INFO_NOT_FOUND_MESSAGE);
    }
}
