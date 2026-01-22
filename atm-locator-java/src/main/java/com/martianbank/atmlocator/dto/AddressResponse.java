package com.martianbank.atmlocator.dto;

/**
 * Response DTO for the physical address of an ATM.
 * Maps to the Address schema in the OpenAPI specification.
 *
 * @param street Street address
 * @param city   City name
 * @param state  State or province code
 * @param zip    Postal/ZIP code
 */
public record AddressResponse(
        String street,
        String city,
        String state,
        String zip
) {
}
