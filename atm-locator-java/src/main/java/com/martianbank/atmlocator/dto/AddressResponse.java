package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for the physical address of an ATM.
 * Maps to the Address schema in the OpenAPI specification.
 *
 * @param street Street address
 * @param city   City name
 * @param state  State or province code
 * @param zip    Postal/ZIP code
 */
@Schema(description = "Physical address of the ATM")
public record AddressResponse(
        @Schema(description = "Street address", example = "123 Main St", requiredMode = Schema.RequiredMode.REQUIRED)
        String street,

        @Schema(description = "City name", example = "San Francisco", requiredMode = Schema.RequiredMode.REQUIRED)
        String city,

        @Schema(description = "State or province code", example = "CA", requiredMode = Schema.RequiredMode.REQUIRED)
        String state,

        @Schema(description = "Postal/ZIP code", example = "94102", requiredMode = Schema.RequiredMode.REQUIRED)
        String zip
) {
}
