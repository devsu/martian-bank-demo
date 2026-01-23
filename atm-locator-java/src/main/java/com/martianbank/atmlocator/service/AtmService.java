package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.AtmDetailsResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;

import java.util.List;

/**
 * Service interface for ATM location operations.
 * Provides methods to search and retrieve ATM locations based on various criteria.
 */
public interface AtmService {

    /**
     * Finds ATMs based on the provided search criteria.
     * Returns up to 4 randomly selected ATMs that match the filters.
     *
     * @param request the search request containing optional filters (isOpenNow, isInterPlanetary)
     * @return a list of ATM responses matching the criteria, up to 4 results
     */
    List<AtmResponse> findAtms(AtmSearchRequest request);

    /**
     * Finds a specific ATM by its MongoDB ObjectId.
     *
     * @param id the MongoDB ObjectId of the ATM
     * @return detailed ATM information
     * @throws com.martianbank.atmlocator.exception.InvalidObjectIdException if the ID format is invalid
     * @throws com.martianbank.atmlocator.exception.AtmNotFoundException if no ATM is found with the given ID
     */
    AtmDetailsResponse findById(String id);
}
