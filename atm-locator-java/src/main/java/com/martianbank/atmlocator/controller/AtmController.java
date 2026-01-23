package com.martianbank.atmlocator.controller;

import com.martianbank.atmlocator.dto.AtmCreateRequest;
import com.martianbank.atmlocator.dto.AtmDetailsResponse;
import com.martianbank.atmlocator.dto.AtmFullResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.dto.ErrorResponse;
import com.martianbank.atmlocator.service.AtmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for ATM location operations.
 * Provides endpoints to discover ATM locations based on various filters.
 */
@RestController
@RequestMapping("/api/atm")
@Tag(name = "ATM", description = "ATM location and management operations")
public class AtmController {

    private final AtmService atmService;

    /**
     * Constructs an AtmController with the required service dependency.
     *
     * @param atmService the ATM service for handling business logic
     */
    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    /**
     * Get a list of ATMs based on optional filters.
     * Returns up to 4 randomly selected ATMs based on the provided criteria.
     *
     * @param request Optional filter options (isOpenNow, isInterPlanetary)
     * @return List of ATM responses matching the criteria
     */
    @Operation(
            summary = "Get list of ATMs",
            description = "Returns a list of up to 4 randomly selected ATMs based on the provided filters. " +
                    "By default, returns non-interplanetary ATMs. Results are shuffled on each request."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of ATMs successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AtmResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No ATMs found matching the criteria",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "No ATMs found")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<List<AtmResponse>> getAtms(
            @RequestBody(required = false) AtmSearchRequest request) {

        List<AtmResponse> atms = atmService.findAtms(request);
        return ResponseEntity.ok(atms);
    }

    /**
     * Get detailed information for a specific ATM by its MongoDB ObjectId.
     *
     * @param id MongoDB ObjectId of the ATM
     * @return Detailed ATM information including coordinates, timings, and availability
     */
    @Operation(
            summary = "Get ATM by ID",
            description = "Retrieves detailed information for a specific ATM by its MongoDB ObjectId. " +
                    "Returns operational details including coordinates, timings, and availability."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ATM details successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AtmDetailsResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "ATM not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AtmDetailsResponse> getAtmById(
            @Parameter(description = "MongoDB ObjectId of the ATM", example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        AtmDetailsResponse atm = atmService.findById(id);
        return ResponseEntity.ok(atm);
    }

    /**
     * Add a new ATM to the system.
     * Creates a new ATM entry in the database with the provided details.
     *
     * @param request ATM creation request containing all required fields
     * @return Created ATM with generated ID and timestamps
     */
    @Operation(
            summary = "Add a new ATM",
            description = "Creates a new ATM entry in the database. All required fields must be provided. " +
                    "This endpoint is intended for administrative use."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "ATM successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AtmFullResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/add")
    public ResponseEntity<AtmFullResponse> addAtm(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ATM creation request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AtmCreateRequest.class)
                    )
            )
            @Valid @RequestBody AtmCreateRequest request) {
        AtmFullResponse createdAtm = atmService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAtm);
    }
}
