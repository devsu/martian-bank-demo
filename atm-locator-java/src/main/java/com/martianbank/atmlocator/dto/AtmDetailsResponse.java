package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for detailed ATM information returned for specific ATM queries.
 * Maps to the ATMDetails schema in the OpenAPI specification.
 *
 * @param coordinates  Geographic coordinates of the ATM
 * @param timings      Operating hours for the ATM location
 * @param atmHours     ATM machine availability hours
 * @param numberOfATMs Number of ATM machines at this location
 * @param isOpen       Whether the ATM location is currently operational
 */
@Schema(description = "Detailed ATM information returned for specific ATM queries")
public record AtmDetailsResponse(
        @Schema(description = "Geographic coordinates of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        CoordinatesResponse coordinates,

        @Schema(description = "Operating hours for the ATM location", requiredMode = Schema.RequiredMode.REQUIRED)
        TimingsResponse timings,

        @Schema(description = "ATM machine availability hours", example = "24/7", requiredMode = Schema.RequiredMode.REQUIRED)
        String atmHours,

        @Schema(description = "Number of ATM machines at this location", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer numberOfATMs,

        @Schema(description = "Whether the ATM location is currently operational", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isOpen
) {
}
