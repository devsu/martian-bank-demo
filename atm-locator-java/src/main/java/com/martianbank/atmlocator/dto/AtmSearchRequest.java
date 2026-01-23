package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for searching ATMs with optional filters.
 * Maps to the GetATMsRequest schema in the OpenAPI specification.
 *
 * @param isOpenNow       Filter to return only currently open ATMs (optional, defaults to false)
 * @param isInterPlanetary Filter to return interplanetary ATMs instead of Earth-based ATMs (optional, defaults to false)
 */
@Schema(description = "Filter options for ATM search")
public record AtmSearchRequest(
        @Schema(description = "Filter to return only currently open ATMs", example = "true")
        Boolean isOpenNow,

        @Schema(description = "Filter to return interplanetary ATMs instead of Earth-based ATMs", example = "false")
        Boolean isInterPlanetary
) {
    /**
     * Creates an AtmSearchRequest with default values (both filters null/false).
     */
    public AtmSearchRequest() {
        this(null, null);
    }
}
