package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public record AtmResponse(
        @JsonProperty("_id")
        String id,
        String name,
        CoordinatesResponse coordinates,
        AddressResponse address,
        Boolean isOpen
) {
}
