package com.martianbank.atmlocator.controller;

import com.martianbank.atmlocator.dto.AtmDetailsResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.service.AtmService;
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

    /**
     * Get detailed information for a specific ATM by its MongoDB ObjectId.
     *
     * @param id MongoDB ObjectId of the ATM
     * @return Detailed ATM information including coordinates, timings, and availability
     */
    @GetMapping("/{id}")
    public ResponseEntity<AtmDetailsResponse> getAtmById(@PathVariable String id) {
        AtmDetailsResponse atm = atmService.findById(id);
        return ResponseEntity.ok(atm);
    }
}
