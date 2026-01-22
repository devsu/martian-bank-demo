package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded model representing operating hours of an ATM location.
 * Used as a nested document within the Atm entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timings {

    /**
     * Monday-Friday business hours (e.g., "9:00 AM - 5:00 PM").
     */
    private String monFri;

    /**
     * Saturday-Sunday business hours (e.g., "10:00 AM - 3:00 PM").
     */
    private String satSun;

    /**
     * Holiday hours (optional, e.g., "Closed on holidays").
     */
    private String holidays;
}
