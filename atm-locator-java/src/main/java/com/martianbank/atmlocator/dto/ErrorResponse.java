package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard error response format for the ATM Locator API.
 * Maps to the ErrorResponse schema in the OpenAPI specification.
 *
 * @param message Error message describing what went wrong
 * @param stack   Stack trace (only included in non-production environments, nullable)
 */
@Schema(description = "Standard error response format")
public record ErrorResponse(
        @Schema(description = "Error message describing what went wrong", example = "Resource not found", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,

        @Schema(description = "Stack trace (only included in non-production environments)", example = "null", nullable = true)
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
