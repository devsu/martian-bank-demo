package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for geographic coordinates of an ATM.
 * Maps to the Coordinates schema in the OpenAPI specification.
 *
 * @param latitude  Geographic latitude (-90 to 90)
 * @param longitude Geographic longitude (-180 to 180)
 */
@Schema(description = "Geographic coordinates of the ATM")
public record CoordinatesResponse(
        @Schema(description = "Geographic latitude", example = "37.7749", minimum = "-90", maximum = "90", requiredMode = Schema.RequiredMode.REQUIRED)
        Double latitude,

        @Schema(description = "Geographic longitude", example = "-122.4194", minimum = "-180", maximum = "180", requiredMode = Schema.RequiredMode.REQUIRED)
        Double longitude
) {
}
