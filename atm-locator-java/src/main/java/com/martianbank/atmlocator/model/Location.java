package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded model representing the complete location information of an ATM.
 * Contains both physical address and geographic coordinates.
 * Used as a nested document within the Atm entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    /**
     * Physical address of the ATM.
     */
    private Address address;

    /**
     * Geographic coordinates for map display.
     */
    private Coordinates coordinates;
}
