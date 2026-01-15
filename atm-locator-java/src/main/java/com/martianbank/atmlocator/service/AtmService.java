/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.*;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.model.*;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for ATM operations.
 *
 * Implements exact business logic from legacy atmController.js
 */
@Service
public class AtmService {

    private static final Logger logger = LoggerFactory.getLogger(AtmService.class);
    private static final int MAX_RESULTS = 4;

    private final AtmRepository atmRepository;

    public AtmService(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    /**
     * Get filtered list of ATMs.
     *
     * Legacy logic (atmController.js:13-36):
     * 1. Default query: interPlanetary: false
     * 2. If isOpenNow is truthy, add isOpen: true
     * 3. If isInterPlanetary is truthy, set interPlanetary: true
     * 4. Project only: name, coordinates, address, isOpen
     * 5. Shuffle results randomly
     * 6. Limit to 4 results
     *
     * @param request Filter parameters (isOpenNow, isInterPlanetary)
     * @return List of ATMs (max 4, randomized)
     * @throws AtmNotFoundException if no ATMs match the filter
     */
    public List<AtmListResponse> getATMs(AtmFilterRequest request) {
        logger.debug("Getting ATMs with filter: {}", request);

        List<Atm> atms = queryAtms(request);

        if (atms == null || atms.isEmpty()) {
            logger.debug("No ATMs found matching filter");
            throw new AtmNotFoundException("No results found");
        }

        // Shuffle results (legacy: sort(() => Math.random() - 0.5))
        List<Atm> shuffled = shuffleAtms(atms);

        // Limit to 4 results (legacy: .slice(0, 4))
        List<Atm> limited = shuffled.stream()
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());

        // Project to response DTO
        return limited.stream()
                .map(AtmListResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Query ATMs based on filter parameters.
     *
     * Legacy query building (atmController.js:14-22):
     * - Default: { interPlanetary: false }
     * - If isOpenNow: adds { isOpen: true }
     * - If isInterPlanetary: sets { interPlanetary: true }
     */
    private List<Atm> queryAtms(AtmFilterRequest request) {
        boolean interPlanetary = request != null
                && Boolean.TRUE.equals(request.getIsInterPlanetary());

        boolean filterByOpen = request != null
                && Boolean.TRUE.equals(request.getIsOpenNow());

        logger.debug("Query params - interPlanetary: {}, filterByOpen: {}",
                interPlanetary, filterByOpen);

        if (filterByOpen) {
            return atmRepository.findByInterPlanetaryAndIsOpen(interPlanetary, true);
        } else {
            return atmRepository.findByInterPlanetary(interPlanetary);
        }
    }

    /**
     * Shuffle ATMs randomly.
     *
     * Legacy: [...ATMs].sort(() => Math.random() - 0.5)
     *
     * Note: Collections.shuffle provides better randomization than
     * the legacy approach, but the end result is the same - random ordering.
     */
    private List<Atm> shuffleAtms(List<Atm> atms) {
        List<Atm> shuffled = new java.util.ArrayList<>(atms);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * Add a new ATM.
     *
     * Legacy logic (atmController.js:41-88):
     * 1. Extract fields from flat request body
     * 2. Build nested ATM object
     * 3. Save and return created ATM
     *
     * @param request ATM creation data
     * @return Created ATM entity (full object including _id)
     * @throws AtmNotFoundException if creation fails
     */
    public Atm addATM(AtmCreateRequest request) {
        logger.debug("Adding new ATM: {}", request.getName());

        Atm atm = buildAtmFromRequest(request);

        try {
            Atm created = atmRepository.save(atm);
            logger.debug("Created ATM with id: {}", created.getId());
            return created;
        } catch (Exception e) {
            logger.error("Failed to create ATM: {}", e.getMessage());
            throw new AtmNotFoundException("Could not create ATM");
        }
    }

    /**
     * Build ATM entity from flat request DTO.
     *
     * Legacy (atmController.js:58-79):
     * Reconstructs nested objects from flat fields.
     */
    private Atm buildAtmFromRequest(AtmCreateRequest request) {
        Atm atm = new Atm();
        atm.setName(request.getName());

        // Build nested address
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZip(request.getZip());
        atm.setAddress(address);

        // Build nested coordinates
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(request.getLatitude());
        coordinates.setLongitude(request.getLongitude());
        atm.setCoordinates(coordinates);

        // Build nested timings
        Timings timings = new Timings();
        timings.setMonFri(request.getMonFri());
        timings.setSatSun(request.getSatSun());
        timings.setHolidays(request.getHolidays());
        atm.setTimings(timings);

        atm.setAtmHours(request.getAtmHours());
        atm.setNumberOfATMs(request.getNumberOfATMs());
        atm.setIsOpen(request.getIsOpen());
        atm.setInterPlanetary(request.getInterPlanetary() != null
                ? request.getInterPlanetary() : false);

        return atm;
    }

    /**
     * Get specific ATM by ID.
     *
     * Legacy logic (atmController.js:93-107):
     * 1. Find by MongoDB ObjectId
     * 2. Return specific fields only
     * 3. 404 if not found
     *
     * @param id MongoDB ObjectId as string
     * @return ATM detail response (projected fields)
     * @throws AtmNotFoundException if ATM not found or invalid ID
     */
    public AtmDetailResponse getSpecificATM(String id) {
        logger.debug("Getting ATM by id: {}", id);

        return atmRepository.findById(id)
                .map(atm -> {
                    logger.debug("Found ATM: {}", atm.getName());
                    return AtmDetailResponse.fromEntity(atm);
                })
                .orElseThrow(() -> {
                    logger.debug("ATM not found with id: {}", id);
                    return new AtmNotFoundException("ATM not found");
                });
    }
}
