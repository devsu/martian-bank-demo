package com.martianbank.atmlocator.dto;

/**
 * Response DTO for detailed ATM information returned for specific ATM queries.
 * Maps to the ATMDetails schema in the OpenAPI specification.
 *
 * @param coordinates  Geographic coordinates of the ATM
 * @param timings      Operating hours for the ATM location
 * @param atmHours     ATM machine availability hours
 * @param numberOfATMs Number of ATM machines at this location
 * @param isOpen       Whether the ATM location is currently operational
 */
public record AtmDetailsResponse(
        CoordinatesResponse coordinates,
        TimingsResponse timings,
        String atmHours,
        Integer numberOfATMs,
        Boolean isOpen
) {
}
