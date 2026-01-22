package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded model representing the physical address of an ATM.
 * Used as a nested document within the Atm entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    /**
     * Street address.
     */
    private String street;

    /**
     * City name.
     */
    private String city;

    /**
     * State or province.
     */
    private String state;

    /**
     * ZIP/Postal code.
     */
    private String zip;
}
