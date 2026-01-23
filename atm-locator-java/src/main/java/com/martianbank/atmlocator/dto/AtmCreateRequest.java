package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new ATM.
 * Maps to the AddATMRequest schema in the OpenAPI specification.
 * All required fields are validated using Bean Validation annotations.
 *
 * @param name            Name or identifier of the ATM location
 * @param street          Street address of the ATM
 * @param city            City where the ATM is located
 * @param state           State or province code
 * @param zip             Postal/ZIP code
 * @param latitude        Geographic latitude coordinate
 * @param longitude       Geographic longitude coordinate
 * @param monFri          Operating hours for Monday through Friday
 * @param satSun          Operating hours for Saturday and Sunday
 * @param holidays        Operating hours on holidays (optional)
 * @param atmHours        ATM machine availability hours
 * @param numberOfATMs    Number of ATM machines at this location
 * @param isOpen          Whether the ATM location is currently operational
 * @param interPlanetary  Whether this is an interplanetary ATM location
 */
@Schema(description = "Request body for creating a new ATM")
public record AtmCreateRequest(
        @Schema(description = "Name or identifier of the ATM location", example = "Martian Bank ATM - Downtown", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "ATM name is required")
        String name,

        @Schema(description = "Street address of the ATM", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Street address is required")
        String street,

        @Schema(description = "City where the ATM is located", example = "San Francisco", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "City is required")
        String city,

        @Schema(description = "State or province code", example = "CA", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "State is required")
        String state,

        @Schema(description = "Postal/ZIP code", example = "94102", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "ZIP code is required")
        String zip,

        @Schema(description = "Geographic latitude coordinate (supports interplanetary coordinates)", example = "37.7749", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Latitude is required")
        Double latitude,

        @Schema(description = "Geographic longitude coordinate (supports interplanetary coordinates)", example = "-122.4194", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Longitude is required")
        Double longitude,

        @Schema(description = "Operating hours for Monday through Friday", example = "9:00 AM - 6:00 PM", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Monday-Friday hours are required")
        String monFri,

        @Schema(description = "Operating hours for Saturday and Sunday", example = "10:00 AM - 4:00 PM", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Saturday-Sunday hours are required")
        String satSun,

        @Schema(description = "Operating hours on holidays (optional)", example = "Closed")
        String holidays,

        @Schema(description = "ATM machine availability hours", example = "24/7", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "ATM hours are required")
        String atmHours,

        @Schema(description = "Number of ATM machines at this location", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Number of ATMs is required")
        @Min(value = 1, message = "Number of ATMs must be at least 1")
        Integer numberOfATMs,

        @Schema(description = "Whether the ATM location is currently operational", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Open status is required")
        Boolean isOpen,

        @Schema(description = "Whether this is an interplanetary ATM location", example = "false", defaultValue = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Interplanetary status is required")
        Boolean interPlanetary
) {
}
