package com.martianbank.atmlocator.dto;

/**
 * Request DTO for searching ATMs with optional filters.
 * Maps to the GetATMsRequest schema in the OpenAPI specification.
 *
 * @param isOpenNow       Filter to return only currently open ATMs (optional, defaults to false)
 * @param isInterPlanetary Filter to return interplanetary ATMs instead of Earth-based ATMs (optional, defaults to false)
 */
public record AtmSearchRequest(
        Boolean isOpenNow,
        Boolean isInterPlanetary
) {
    /**
     * Creates an AtmSearchRequest with default values (both filters null/false).
     */
    public AtmSearchRequest() {
        this(null, null);
    }
}
