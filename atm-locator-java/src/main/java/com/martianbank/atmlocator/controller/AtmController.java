package com.martianbank.atmlocator.controller;

import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.service.AtmService;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<List<AtmResponse>> getAtms(
            @RequestBody(required = false) AtmSearchRequest request) {

        List<AtmResponse> atms = atmService.findAtms(request);
        return ResponseEntity.ok(atms);
    }
}
