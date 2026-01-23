package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new ATM.
 * Maps to the AddATMRequest schema in the OpenAPI specification.
 * All fields are required and validated using Bean Validation annotations.
 *
 * @param name            Name or identifier of the ATM location
 * @param location        Location information including coordinates and address
 * @param isOpenNow       Whether the ATM location is currently operational
 * @param isInterPlanetary Whether this is an interplanetary ATM location
 */
@Schema(description = "Request body for creating a new ATM")
public record AtmCreateRequest(
        @Schema(description = "Name or identifier of the ATM location", example = "Martian Bank ATM - Downtown", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "ATM name is required")
        String name,

        @Schema(description = "Location information including coordinates and address", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Location information is required")
        @Valid
        LocationRequest location,

        @Schema(description = "Whether the ATM location is currently operational", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Open status is required")
        Boolean isOpenNow,

        @Schema(description = "Whether this is an interplanetary ATM location", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Interplanetary status is required")
        Boolean isInterPlanetary
) {
}
