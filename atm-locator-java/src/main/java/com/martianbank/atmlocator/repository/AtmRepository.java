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
    // No custom query methods needed.
    // Uses inherited findAll() - filtering done in service layer.
}
