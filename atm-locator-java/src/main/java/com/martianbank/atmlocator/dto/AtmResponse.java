package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for ATM information returned in list queries.
 * Maps to the ATMListItem schema in the OpenAPI specification.
 *
 * @param id          MongoDB ObjectId (serialized as "_id" in JSON)
 * @param name        Name or identifier of the ATM location
 * @param coordinates Geographic coordinates of the ATM
 * @param address     Physical address of the ATM
 * @param isOpen      Whether the ATM location is currently operational
 */
@Schema(description = "ATM summary returned in list queries")
public record AtmResponse(
        @Schema(description = "MongoDB ObjectId", example = "507f1f77bcf86cd799439011", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("_id")
        String id,

        @Schema(description = "Name or identifier of the ATM location", example = "Martian Bank ATM - Downtown", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Geographic coordinates of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        CoordinatesResponse coordinates,

        @Schema(description = "Physical address of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        AddressResponse address,

        @Schema(description = "Whether the ATM location is currently operational", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isOpen
) {
}
