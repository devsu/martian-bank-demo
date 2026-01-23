package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for the physical address when creating an ATM.
 * All fields are required and must not be blank.
 *
 * @param street Street address
 * @param city   City name
 * @param state  State or province code
 * @param zip    Postal/ZIP code
 */
@Schema(description = "Physical address for the ATM location")
public record AddressRequest(
        @Schema(description = "Street address", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Street address is required")
        String street,

        @Schema(description = "City name", example = "San Francisco", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "City is required")
        String city,

        @Schema(description = "State or province code", example = "CA", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "State is required")
        String state,

        @Schema(description = "Postal/ZIP code", example = "94102", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "ZIP code is required")
        String zip
) {
}
