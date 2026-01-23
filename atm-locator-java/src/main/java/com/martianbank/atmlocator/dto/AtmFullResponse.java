package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response DTO for complete ATM information returned after creation.
 * Maps to the ATMFull schema in the OpenAPI specification.
 *
 * @param id              MongoDB ObjectId (serialized as "_id" in JSON)
 * @param name            Name or identifier of the ATM location
 * @param address         Physical address of the ATM
 * @param coordinates     Geographic coordinates of the ATM
 * @param timings         Operating hours for the ATM location
 * @param atmHours        ATM machine availability hours
 * @param numberOfATMs    Number of ATM machines at this location
 * @param isOpen          Whether the ATM location is currently operational
 * @param interPlanetary  Whether this is an interplanetary ATM location
 * @param createdAt       Timestamp when the ATM was created
 * @param updatedAt       Timestamp when the ATM was last updated
 * @param version         MongoDB version key
 */
@Schema(description = "Complete ATM object as stored in database")
public record AtmFullResponse(
        @Schema(description = "MongoDB ObjectId", example = "507f1f77bcf86cd799439011", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("_id")
        String id,

        @Schema(description = "Name or identifier of the ATM location", example = "Martian Bank ATM - Downtown", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Physical address of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        AddressResponse address,

        @Schema(description = "Geographic coordinates of the ATM", requiredMode = Schema.RequiredMode.REQUIRED)
        CoordinatesResponse coordinates,

        @Schema(description = "Operating hours for the ATM location")
        TimingsResponse timings,

        @Schema(description = "ATM machine availability hours", example = "24/7")
        String atmHours,

        @Schema(description = "Number of ATM machines at this location", example = "2")
        Integer numberOfATMs,

        @Schema(description = "Whether the ATM location is currently operational", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isOpen,

        @Schema(description = "Whether this is an interplanetary ATM location", example = "false")
        Boolean interPlanetary,

        @Schema(description = "Timestamp when the ATM was created", example = "2024-01-15T10:30:00.000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the ATM was last updated", example = "2024-01-15T10:30:00.000Z")
        Instant updatedAt,

        @Schema(description = "MongoDB version key", example = "0")
        @JsonProperty("__v")
        Integer version
) {
}
