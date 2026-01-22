package com.martianbank.atmlocator.dto;

/**
 * Response DTO for geographic coordinates of an ATM.
 * Maps to the Coordinates schema in the OpenAPI specification.
 *
 * @param latitude  Geographic latitude (-90 to 90)
 * @param longitude Geographic longitude (-180 to 180)
 */
public record CoordinatesResponse(
        Double latitude,
        Double longitude
) {
}
