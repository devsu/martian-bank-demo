package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * Entity representing an ATM location stored in MongoDB.
 * Maps to the "atms" collection in the database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "atms")
public class Atm {

    /**
     * MongoDB document identifier.
     */
    @Id
    private String id;

    /**
     * ATM location name (e.g., "Martian ATM (Highway)").
     */
    private String name;

    /**
     * Physical address of the ATM.
     * Stored as embedded document at root level.
     */
    private Address address;

    /**
     * Geographic coordinates for map display.
     * Stored as embedded document at root level.
     */
    private Coordinates coordinates;

    /**
     * Current operational status of the ATM.
     */
    @Field("isOpen")
    private Boolean isOpenNow;

    /**
     * Indicates if the ATM is located on another planet (inter-planetary).
     */
    @Field("interPlanetary")
    private Boolean isInterPlanetary;

    /**
     * Operating hours information for the ATM location.
     */
    private Timings timings;

    /**
     * ATM operational hours (e.g., "24 hours").
     */
    private String atmHours;

    /**
     * Number of ATM machines at this location.
     */
    private Integer numberOfATMs;

    /**
     * Record creation timestamp.
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Record last update timestamp.
     */
    @LastModifiedDate
    private Instant updatedAt;
}
