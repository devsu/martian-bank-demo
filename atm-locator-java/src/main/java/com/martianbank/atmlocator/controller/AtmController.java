package com.martianbank.atmlocator.controller;

import com.martianbank.atmlocator.dto.AddressResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.dto.CoordinatesResponse;
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

        // Mocked ATM data for initial API contract validation
        List<AtmResponse> mockedAtms = List.of(
                new AtmResponse(
                        "507f1f77bcf86cd799439011",
                        "Martian Bank ATM - Downtown",
                        new CoordinatesResponse(37.7749, -122.4194),
                        new AddressResponse("123 Main St", "San Francisco", "CA", "94102"),
                        true
                ),
                new AtmResponse(
                        "507f1f77bcf86cd799439012",
                        "Martian Bank ATM - Financial District",
                        new CoordinatesResponse(37.7899, -122.4014),
                        new AddressResponse("456 Market St", "San Francisco", "CA", "94103"),
                        true
                ),
                new AtmResponse(
                        "507f1f77bcf86cd799439013",
                        "Martian Bank ATM - Mission Bay",
                        new CoordinatesResponse(37.7707, -122.3912),
                        new AddressResponse("789 Berry St", "San Francisco", "CA", "94158"),
                        false
                ),
                new AtmResponse(
                        "507f1f77bcf86cd799439014",
                        "Martian Bank ATM - Castro",
                        new CoordinatesResponse(37.7609, -122.4350),
                        new AddressResponse("321 Castro St", "San Francisco", "CA", "94114"),
                        true
                )
        );

        return ResponseEntity.ok(mockedAtms);
    }
}
