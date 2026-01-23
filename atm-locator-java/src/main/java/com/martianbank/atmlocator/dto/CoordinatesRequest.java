package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for geographic coordinates when creating an ATM.
 * Contains validation constraints for latitude and longitude values.
 *
 * @param latitude  Geographic latitude (-90 to 90)
 * @param longitude Geographic longitude (-180 to 180)
 */
@Schema(description = "Geographic coordinates for the ATM location")
public record CoordinatesRequest(
        @Schema(description = "Geographic latitude", example = "37.7749", minimum = "-90", maximum = "90", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
        Double latitude,

        @Schema(description = "Geographic longitude", example = "-122.4194", minimum = "-180", maximum = "180", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
        Double longitude
) {
}
