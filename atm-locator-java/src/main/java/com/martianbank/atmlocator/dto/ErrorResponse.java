package com.martianbank.atmlocator.dto;

/**
 * Standard error response format for the ATM Locator API.
 * Maps to the ErrorResponse schema in the OpenAPI specification.
 *
 * @param message Error message describing what went wrong
 * @param stack   Stack trace (only included in non-production environments, nullable)
 */
public record ErrorResponse(
        String message,
        String stack
) {
    /**
     * Creates an ErrorResponse with a message and no stack trace.
     *
     * @param message the error message
     * @return an ErrorResponse with null stack trace
     */
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }

    /**
     * Creates an ErrorResponse with a message and stack trace.
     *
     * @param message the error message
     * @param stack   the stack trace (can be null)
     * @return an ErrorResponse with the provided details
     */
    public static ErrorResponse of(String message, String stack) {
        return new ErrorResponse(message, stack);
    }
}
