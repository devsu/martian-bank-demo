package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.AddressResponse;
import com.martianbank.atmlocator.dto.AtmCreateRequest;
import com.martianbank.atmlocator.dto.AtmDetailsResponse;
import com.martianbank.atmlocator.dto.AtmFullResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.dto.CoordinatesResponse;
import com.martianbank.atmlocator.dto.TimingsResponse;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.exception.InvalidObjectIdException;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import com.martianbank.atmlocator.repository.AtmRepository;
import com.martianbank.atmlocator.util.RandomizationUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AtmService providing ATM location operations.
 * Fetches ATM data from MongoDB via AtmRepository.
 */
@Service
public class AtmServiceImpl implements AtmService {

    static final int MAX_RESULTS = 4;

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

        // Apply filters on entities using Stream API with AND logic, then map to DTOs
        List<AtmResponse> filteredAtms = allAtms.stream()
                .filter(atm -> applyFilters(atm, request))
                .map(this::mapToAtmResponse)
                .collect(Collectors.toList());

        // Throw exception if no ATMs match the criteria
        if (filteredAtms.isEmpty()) {
            throw new AtmNotFoundException();
        }

        // Select random subset up to MAX_RESULTS
        return RandomizationUtils.selectRandom(filteredAtms, MAX_RESULTS);
    }

    /**
     * Applies all filters to an ATM entity using AND logic.
     * Null filter values mean no filter is applied for that field.
     *
     * @param atm     the ATM entity to check
     * @param request the search request containing optional filters
     * @return true if the ATM matches all specified filters, false otherwise
     */
    private boolean applyFilters(Atm atm, AtmSearchRequest request) {
        return matchesInterPlanetaryFilter(atm, request)
                && matchesOpenNowFilter(atm, request);
    }

    /**
     * Checks if ATM matches the isInterPlanetary filter.
     * When filter is null, defaults to non-interplanetary ATMs (false).
     *
     * @param atm     the ATM entity to check
     * @param request the search request containing the filter
     * @return true if ATM matches the filter criteria
     */
    private boolean matchesInterPlanetaryFilter(Atm atm, AtmSearchRequest request) {
        boolean wantInterPlanetary = request != null && Boolean.TRUE.equals(request.isInterPlanetary());
        boolean isInterPlanetary = Boolean.TRUE.equals(atm.getIsInterPlanetary());
        return wantInterPlanetary == isInterPlanetary;
    }

    /**
     * Checks if ATM matches the isOpenNow filter.
     * When filter is null, all ATMs pass this filter.
     * When filter is true, only open ATMs pass.
     * When filter is false, only closed ATMs pass.
     *
     * @param atm     the ATM entity to check
     * @param request the search request containing the filter
     * @return true if ATM matches the filter criteria
     */
    private boolean matchesOpenNowFilter(Atm atm, AtmSearchRequest request) {
        if (request == null || request.isOpenNow() == null) {
            return true; // No filter applied
        }
        boolean wantOpen = Boolean.TRUE.equals(request.isOpenNow());
        boolean isOpen = Boolean.TRUE.equals(atm.getIsOpenNow());
        return wantOpen == isOpen;
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

    @Override
    public AtmDetailsResponse findById(String id) {
        // Validate ObjectId format using MongoDB's ObjectId.isValid()
        if (!ObjectId.isValid(id)) {
            throw new InvalidObjectIdException(id);
        }

        // Find ATM by ID
        Atm atm = atmRepository.findById(id)
                .orElseThrow(() -> new AtmNotFoundException(id));

        // Map to AtmDetailsResponse
        return mapToAtmDetailsResponse(atm);
    }

    /**
     * Maps an Atm entity to an AtmDetailsResponse DTO.
     *
     * @param atm the ATM entity to convert
     * @return the corresponding AtmDetailsResponse DTO
     */
    private AtmDetailsResponse mapToAtmDetailsResponse(Atm atm) {
        CoordinatesResponse coordinates = null;
        TimingsResponse timings = null;

        if (atm.getCoordinates() != null) {
            coordinates = new CoordinatesResponse(
                    atm.getCoordinates().getLatitude(),
                    atm.getCoordinates().getLongitude()
            );
        }

        if (atm.getTimings() != null) {
            timings = new TimingsResponse(
                    atm.getTimings().getMonFri(),
                    atm.getTimings().getSatSun(),
                    atm.getTimings().getHolidays()
            );
        }

        return new AtmDetailsResponse(
                coordinates,
                timings,
                atm.getAtmHours(),
                atm.getNumberOfATMs(),
                atm.getIsOpenNow()
        );
    }

    @Override
    public AtmFullResponse create(AtmCreateRequest request) {
        // Convert request DTO to entity
        Atm atm = mapToAtmEntity(request);

        // Save to repository
        Atm savedAtm = atmRepository.save(atm);

        // Map saved entity to full response DTO
        return mapToAtmFullResponse(savedAtm);
    }

    /**
     * Maps an AtmCreateRequest DTO to an Atm entity.
     *
     * @param request the ATM creation request
     * @return the corresponding Atm entity
     */
    private Atm mapToAtmEntity(AtmCreateRequest request) {
        Coordinates coordinates = Coordinates.builder()
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();

        Address address = Address.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .zip(request.zip())
                .build();

        Timings timings = Timings.builder()
                .monFri(request.monFri())
                .satSun(request.satSun())
                .holidays(request.holidays())
                .build();

        return Atm.builder()
                .name(request.name())
                .coordinates(coordinates)
                .address(address)
                .timings(timings)
                .atmHours(request.atmHours())
                .numberOfATMs(request.numberOfATMs())
                .isOpenNow(request.isOpen())
                .isInterPlanetary(request.interPlanetary())
                .build();
    }

    /**
     * Maps an Atm entity to an AtmFullResponse DTO.
     *
     * @param atm the ATM entity to convert
     * @return the corresponding AtmFullResponse DTO
     */
    private AtmFullResponse mapToAtmFullResponse(Atm atm) {
        CoordinatesResponse coordinates = null;
        AddressResponse address = null;
        TimingsResponse timings = null;

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

        if (atm.getTimings() != null) {
            timings = new TimingsResponse(
                    atm.getTimings().getMonFri(),
                    atm.getTimings().getSatSun(),
                    atm.getTimings().getHolidays()
            );
        }

        return new AtmFullResponse(
                atm.getId(),
                atm.getName(),
                address,
                coordinates,
                timings,
                atm.getAtmHours(),
                atm.getNumberOfATMs(),
                atm.getIsOpenNow(),
                atm.getIsInterPlanetary(),
                atm.getCreatedAt(),
                atm.getUpdatedAt(),
                0  // MongoDB version key, defaulting to 0 for new documents
        );
    }
}
