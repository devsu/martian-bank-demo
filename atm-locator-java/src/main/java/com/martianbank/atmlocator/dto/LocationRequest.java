package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for the location information when creating an ATM.
 * Contains nested coordinates and address DTOs with cascading validation.
 *
 * @param coordinates Geographic coordinates of the ATM
 * @param address     Physical address of the ATM
 */
@Schema(description = "Location information for the ATM including coordinates and address")
public record LocationRequest(
        @Schema(description = "Geographic coordinates of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Coordinates are required")
        @Valid
        CoordinatesRequest coordinates,

        @Schema(description = "Physical address of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Address is required")
        @Valid
        AddressRequest address
) {
}
