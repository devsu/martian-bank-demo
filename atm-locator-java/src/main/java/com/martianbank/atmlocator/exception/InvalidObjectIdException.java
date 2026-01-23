package com.martianbank.atmlocator.exception;

/**
 * Exception thrown when an invalid ObjectId format is provided.
 * Returns "Resource not found" message as per OpenAPI specification.
 */
public class InvalidObjectIdException extends RuntimeException {

    private static final String MESSAGE = "Resource not found";

    /**
     * Constructs an InvalidObjectIdException with the standard message.
     *
     * @param id the invalid ObjectId string (not used in message)
     */
    public InvalidObjectIdException(String id) {
        super(MESSAGE);
    }
}
