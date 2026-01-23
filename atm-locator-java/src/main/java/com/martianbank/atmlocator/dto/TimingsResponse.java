package com.martianbank.atmlocator.dto;

/**
 * Response DTO for ATM operating hours.
 * Maps to the Timings schema in the OpenAPI specification.
 *
 * @param monFri   Monday through Friday hours
 * @param satSun   Saturday and Sunday hours
 * @param holidays Holiday hours (optional)
 */
public record TimingsResponse(
        String monFri,
        String satSun,
        String holidays
) {
}
