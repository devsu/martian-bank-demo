package com.martianbank.atmlocator.service;

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
}
