package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded model representing geographic coordinates of an ATM.
 * Used as a nested document within the Atm entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    /**
     * Geographic latitude (-90 to 90 degrees).
     */
    private Double latitude;

    /**
     * Geographic longitude (-180 to 180 degrees).
     */
    private Double longitude;
}
