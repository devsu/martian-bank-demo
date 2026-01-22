package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.AddressResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.dto.CoordinatesResponse;
import com.martianbank.atmlocator.util.RandomizationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AtmService providing ATM location operations.
 * Currently uses mocked data for initial development and testing.
 */
@Service
public class AtmServiceImpl implements AtmService {

    private static final int MAX_RESULTS = 4;

    // Mocked Earth-based ATM data
    private static final List<AtmResponse> EARTH_ATMS = List.of(
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
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439015",
                    "Martian Bank ATM - SOMA",
                    new CoordinatesResponse(37.7785, -122.4056),
                    new AddressResponse("500 Howard St", "San Francisco", "CA", "94105"),
                    false
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439016",
                    "Martian Bank ATM - Nob Hill",
                    new CoordinatesResponse(37.7930, -122.4161),
                    new AddressResponse("1000 California St", "San Francisco", "CA", "94108"),
                    true
            )
    );

    // Mocked interplanetary ATM data
    private static final List<AtmResponse> INTERPLANETARY_ATMS = List.of(
            new AtmResponse(
                    "507f1f77bcf86cd799439101",
                    "Martian Bank ATM - Olympus Mons Base",
                    new CoordinatesResponse(18.65, -133.8),
                    new AddressResponse("1 Olympus Plaza", "Olympus City", "Mars", "MRS-001"),
                    true
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439102",
                    "Martian Bank ATM - Valles Marineris Station",
                    new CoordinatesResponse(-14.0, -70.0),
                    new AddressResponse("Canyon View Drive", "Marineris Central", "Mars", "MRS-002"),
                    true
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439103",
                    "Martian Bank ATM - Hellas Basin Colony",
                    new CoordinatesResponse(-42.7, 70.0),
                    new AddressResponse("100 Crater Rim Road", "Hellas City", "Mars", "MRS-003"),
                    false
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439104",
                    "Martian Bank ATM - Phobos Orbital Station",
                    new CoordinatesResponse(0.0, 0.0),
                    new AddressResponse("Orbital Deck 7", "Phobos Station", "Phobos", "PHB-001"),
                    true
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439105",
                    "Martian Bank ATM - Europa Subsurface Hub",
                    new CoordinatesResponse(-10.3, 85.2),
                    new AddressResponse("Ice Shell Level 3", "Europa Base Alpha", "Europa", "EUR-001"),
                    false
            ),
            new AtmResponse(
                    "507f1f77bcf86cd799439106",
                    "Martian Bank ATM - Titan Dome",
                    new CoordinatesResponse(25.0, -45.0),
                    new AddressResponse("Methane Lake View", "Titan Settlement", "Titan", "TTN-001"),
                    true
            )
    );

    @Override
    public List<AtmResponse> findAtms(AtmSearchRequest request) {
        // Determine which ATM list to use based on isInterPlanetary filter
        List<AtmResponse> sourceAtms = getSourceAtms(request);

        // Apply isOpenNow filter if specified
        List<AtmResponse> filteredAtms = applyOpenNowFilter(sourceAtms, request);

        // Select random subset up to MAX_RESULTS
        return RandomizationUtils.selectRandom(filteredAtms, MAX_RESULTS);
    }

    /**
     * Gets the source ATM list based on the isInterPlanetary filter.
     *
     * @param request the search request
     * @return the appropriate ATM list (Earth or interplanetary)
     */
    private List<AtmResponse> getSourceAtms(AtmSearchRequest request) {
        if (request != null && Boolean.TRUE.equals(request.isInterPlanetary())) {
            return new ArrayList<>(INTERPLANETARY_ATMS);
        }
        return new ArrayList<>(EARTH_ATMS);
    }

    /**
     * Filters ATMs based on the isOpenNow flag.
     *
     * @param atms    the list of ATMs to filter
     * @param request the search request containing the filter
     * @return filtered list of ATMs
     */
    private List<AtmResponse> applyOpenNowFilter(List<AtmResponse> atms, AtmSearchRequest request) {
        if (request != null && Boolean.TRUE.equals(request.isOpenNow())) {
            return atms.stream()
                    .filter(atm -> Boolean.TRUE.equals(atm.isOpen()))
                    .collect(Collectors.toList());
        }
        return atms;
    }
}
