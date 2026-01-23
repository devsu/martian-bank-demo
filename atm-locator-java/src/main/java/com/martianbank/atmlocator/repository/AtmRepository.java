package com.martianbank.atmlocator.repository;

import com.martianbank.atmlocator.model.Atm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ATM data access operations.
 * Extends MongoRepository to provide standard CRUD operations.
 *
 * Filtering is performed in the service layer to match
 * the behavior of the Node.js implementation.
 */
@Repository
public interface AtmRepository extends MongoRepository<Atm, String> {

    /**
     * Checks if an ATM already exists at the given coordinates.
     * Used for duplicate detection when creating new ATMs.
     *
     * @param latitude the latitude to check
     * @param longitude the longitude to check
     * @return true if an ATM exists at the specified coordinates, false otherwise
     */
    boolean existsByCoordinatesLatitudeAndCoordinatesLongitude(Double latitude, Double longitude);

    /**
     * Checks if an ATM already exists with the given name.
     * Used for duplicate detection when creating new ATMs.
     *
     * @param name the name to check
     * @return true if an ATM exists with the specified name, false otherwise
     */
    boolean existsByName(String name);
}
