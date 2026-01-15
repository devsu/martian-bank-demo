/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.controller;

import com.martianbank.atmlocator.dto.*;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.service.AtmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for ATM operations.
 *
 * Base path: /api/atm (matching legacy routes)
 *
 * Legacy routes (atmRoutes.js:16-18):
 * - POST /          → getATMs
 * - POST /add       → addATM
 * - GET  /:id       → getSpecificATM
 */
@RestController
@RequestMapping("/api/atm")
@Tag(name = "ATM", description = "ATM location operations")
public class AtmController {

    private static final Logger logger = LoggerFactory.getLogger(AtmController.class);

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    /**
     * Get list of ATMs with optional filters.
     *
     * Legacy: POST /api/atm
     * Returns max 4 randomized ATMs matching filters.
     *
     * @param request Filter parameters (isOpenNow, isInterPlanetary)
     * @return List of ATM summaries
     */
    @PostMapping(value = {"", "/"})
    @Operation(
            summary = "Get all ATMs",
            description = "Get a list of all ATMs according to the filters"
    )
    @ApiResponse(responseCode = "200", description = "ATM details successfully fetched")
    @ApiResponse(responseCode = "404", description = "No ATMs found")
    public ResponseEntity<?> getATMs(@RequestBody(required = false) AtmFilterRequest request) {
        logger.debug("GET ATMs called with filter: {}", request);

        try {
            List<AtmListResponse> atms = atmService.getATMs(request);
            return ResponseEntity.ok(atms);
        } catch (AtmNotFoundException ex) {
            // Legacy: returns plain string "No ATMs found" for this case
            // res.status(404).json("No ATMs found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No ATMs found");
        }
    }

    /**
     * Add a new ATM.
     *
     * Legacy: POST /api/atm/add
     * Returns the created ATM with all fields including _id.
     *
     * @param request ATM data to create
     * @return Created ATM entity
     */
    @PostMapping("/add")
    @Operation(
            summary = "Add new ATM",
            description = "Create a new ATM record"
    )
    @ApiResponse(responseCode = "201", description = "ATM created successfully")
    @ApiResponse(responseCode = "404", description = "Could not create ATM")
    public ResponseEntity<Atm> addATM(@RequestBody AtmCreateRequest request) {
        logger.debug("ADD ATM called with name: {}", request.getName());

        Atm created = atmService.addATM(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get specific ATM by ID.
     *
     * Legacy: GET /api/atm/:id
     * Returns ATM details (coordinates, timings, hours, numberOfATMs, isOpen).
     *
     * @param id MongoDB ObjectId as string
     * @return ATM detail response
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get ATM by ID",
            description = "Get ATM details by ID"
    )
    @ApiResponse(responseCode = "200", description = "ATM details successfully fetched")
    @ApiResponse(responseCode = "404", description = "ATM not found")
    public ResponseEntity<AtmDetailResponse> getSpecificATM(
            @Parameter(description = "ID of the ATM", required = true)
            @PathVariable String id) {
        logger.debug("GET specific ATM called with id: {}", id);

        AtmDetailResponse atm = atmService.getSpecificATM(id);
        return ResponseEntity.ok(atm);
    }
}
