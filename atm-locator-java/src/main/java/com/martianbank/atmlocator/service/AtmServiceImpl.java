package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.AddressResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.dto.CoordinatesResponse;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.repository.AtmRepository;
import com.martianbank.atmlocator.util.RandomizationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AtmService providing ATM location operations.
 * Fetches ATM data from MongoDB via AtmRepository.
 */
@Service
public class AtmServiceImpl implements AtmService {

    private static final int MAX_RESULTS = 4;

    private final AtmRepository atmRepository;

    /**
     * Constructor injection for AtmRepository.
     *
     * @param atmRepository the repository for ATM data access
     */
    public AtmServiceImpl(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    @Override
    public List<AtmResponse> findAtms(AtmSearchRequest request) {
        // Fetch all ATMs from the repository
        List<Atm> allAtms = atmRepository.findAll();

        // Convert entities to DTOs
        List<AtmResponse> atmResponses = allAtms.stream()
                .map(this::mapToAtmResponse)
                .collect(Collectors.toList());

        // Filter by isInterPlanetary (default: non-interplanetary)
        List<AtmResponse> filteredByPlanetary = filterByInterPlanetary(atmResponses, allAtms, request);

        // Apply isOpenNow filter if specified
        List<AtmResponse> filteredAtms = applyOpenNowFilter(filteredByPlanetary, request);

        // Select random subset up to MAX_RESULTS
        return RandomizationUtils.selectRandom(filteredAtms, MAX_RESULTS);
    }

    /**
     * Maps an Atm entity to an AtmResponse DTO.
     *
     * @param atm the ATM entity to convert
     * @return the corresponding AtmResponse DTO
     */
    private AtmResponse mapToAtmResponse(Atm atm) {
        CoordinatesResponse coordinates = null;
        AddressResponse address = null;

        if (atm.getCoordinates() != null) {
            coordinates = new CoordinatesResponse(
                    atm.getCoordinates().getLatitude(),
                    atm.getCoordinates().getLongitude()
            );
        }

        if (atm.getAddress() != null) {
            address = new AddressResponse(
                    atm.getAddress().getStreet(),
                    atm.getAddress().getCity(),
                    atm.getAddress().getState(),
                    atm.getAddress().getZip()
            );
        }

        return new AtmResponse(
                atm.getId(),
                atm.getName(),
                coordinates,
                address,
                atm.getIsOpenNow()
        );
    }

    /**
     * Filters ATM responses based on the isInterPlanetary flag.
     * By default, returns non-interplanetary ATMs.
     *
     * @param atmResponses the list of ATM responses
     * @param entities     the corresponding ATM entities (for checking interPlanetary flag)
     * @param request      the search request containing the filter
     * @return filtered list of ATM responses
     */
    private List<AtmResponse> filterByInterPlanetary(List<AtmResponse> atmResponses, List<Atm> entities, AtmSearchRequest request) {
        boolean wantInterPlanetary = request != null && request.isInterPlanetary() != null && request.isInterPlanetary();

        // Create a map of entity IDs to their interPlanetary status
        return atmResponses.stream()
                .filter(response -> {
                    // Find the corresponding entity
                    Atm entity = entities.stream()
                            .filter(e -> e.getId().equals(response.id()))
                            .findFirst()
                            .orElse(null);

                    if (entity == null) {
                        return false;
                    }

                    boolean isInterPlanetary = entity.getIsInterPlanetary();
                    return wantInterPlanetary == isInterPlanetary;
                })
                .collect(Collectors.toList());
    }

    /**
     * Filters ATMs based on the isOpenNow flag.
     *
     * @param atms    the list of ATMs to filter
     * @param request the search request containing the filter
     * @return filtered list of ATMs
     */
    private List<AtmResponse> applyOpenNowFilter(List<AtmResponse> atms, AtmSearchRequest request) {
        if (request != null && request.isOpenNow() != null && request.isOpenNow()) {
            return atms.stream()
                    .filter(atm -> atm.isOpen())
                    .collect(Collectors.toList());
        }
        return atms;
    }
}
