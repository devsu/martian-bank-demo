package com.martianbank.atmlocator.exception;

/**
 * Exception thrown when attempting to create an ATM that already exists.
 * An ATM is considered a duplicate if another ATM already exists at the same coordinates
 * (latitude and longitude combination).
 */
public class DuplicateAtmException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "An ATM already exists at this location";

    /**
     * Constructs a DuplicateAtmException with a default message.
     */
    public DuplicateAtmException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a DuplicateAtmException with a custom message.
     *
     * @param message the detail message
     */
    public DuplicateAtmException(String message) {
        super(message);
    }

    /**
     * Constructs a DuplicateAtmException for specific coordinates.
     *
     * @param latitude the latitude of the duplicate location
     * @param longitude the longitude of the duplicate location
     */
    public DuplicateAtmException(Double latitude, Double longitude) {
        super(String.format("An ATM already exists at coordinates (%.6f, %.6f)", latitude, longitude));
    }

    /**
     * Factory method to create a DuplicateAtmException for a duplicate name.
     *
     * @param name the duplicate ATM name
     * @return a new DuplicateAtmException instance
     */
    public static DuplicateAtmException forName(String name) {
        return new DuplicateAtmException(String.format("An ATM with name '%s' already exists", name));
    }
}
