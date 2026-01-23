package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for ATM operating hours.
 * Maps to the Timings schema in the OpenAPI specification.
 *
 * @param monFri   Monday through Friday hours
 * @param satSun   Saturday and Sunday hours
 * @param holidays Holiday hours (optional)
 */
@Schema(description = "Operating hours for the ATM location")
public record TimingsResponse(
        @Schema(description = "Monday through Friday hours", example = "9:00 AM - 6:00 PM", requiredMode = Schema.RequiredMode.REQUIRED)
        String monFri,

        @Schema(description = "Saturday and Sunday hours", example = "10:00 AM - 4:00 PM", requiredMode = Schema.RequiredMode.REQUIRED)
        String satSun,

        @Schema(description = "Holiday hours (optional)", example = "Closed")
        String holidays
) {
}
