package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * Response DTO for validation errors.
 * Contains a message and a map of field-specific error messages.
 *
 * @param message Generic error message
 * @param errors  Map of field names to their validation error messages
 */
@Schema(description = "Validation error response format")
public record ValidationErrorResponse(
        @Schema(description = "Generic error message", example = "Validation failed", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,

        @Schema(description = "Map of field names to error messages", example = "{\"location.coordinates.latitude\": \"must be between -90 and 90\"}", requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, String> errors
) {
    /**
     * Creates a ValidationErrorResponse with a message and field errors.
     *
     * @param message the error message
     * @param errors  map of field names to error messages
     * @return a ValidationErrorResponse with the provided details
     */
    public static ValidationErrorResponse of(String message, Map<String, String> errors) {
        return new ValidationErrorResponse(message, errors);
    }
}
