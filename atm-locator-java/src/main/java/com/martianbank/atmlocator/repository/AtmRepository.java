/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.repository;

import com.martianbank.atmlocator.model.Atm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ATM entity operations.
 *
 * Matches legacy query patterns from atmController.js
 */
@Repository
public interface AtmRepository extends MongoRepository<Atm, String> {

    /**
     * Find ATMs by interPlanetary flag.
     * Legacy: ATM.find({ interPlanetary: false/true })
     */
    List<Atm> findByInterPlanetary(Boolean interPlanetary);

    /**
     * Find ATMs by interPlanetary and isOpen flags.
     * Legacy: ATM.find({ interPlanetary: false/true, isOpen: true })
     */
    List<Atm> findByInterPlanetaryAndIsOpen(Boolean interPlanetary, Boolean isOpen);
}
