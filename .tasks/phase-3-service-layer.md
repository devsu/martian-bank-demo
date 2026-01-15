# Phase 3: Service Layer & Business Logic

## Overview

Implement the ATM service with exact query logic, randomization, and projection matching the legacy Node.js behavior. This phase is critical for functional parity.

## Prerequisites

- Phase 2 and 2.5 completed
- All model and repository classes in place
- Tests passing

## Deliverables

1. `AtmService.java` - Service implementation with business logic
2. `AtmFilterRequest.java` - DTO for filter parameters
3. `AtmCreateRequest.java` - DTO for ATM creation
4. `AtmListResponse.java` - DTO for list response projection
5. `AtmDetailResponse.java` - DTO for specific ATM response
6. `AtmNotFoundException.java` - Custom exception

## Legacy Reference

**Query Logic** (`atm-locator/controllers/atmController.js:13-36`):
```javascript
const getATMs = asyncHandler(async (req, res) => {
  let query = {
    interPlanetary: false,  // Default
  };
  if (req.body.isOpenNow) {
    query.isOpen = true;
  }
  if (req.body.isInterPlanetary) {
    query.interPlanetary = true;
  }
  const ATMs = await ATM.find(query, {
    name: 1,
    coordinates: 1,
    address: 1,
    isOpen: 1,
  });
  const shuffledATMs = [...ATMs].sort(() => Math.random() - 0.5).slice(0, 4);
  // ...
});
```

**Specific ATM Response** (`atm-locator/controllers/atmController.js:96-102`):
```javascript
res.status(200).json({
  coordinates: atm.coordinates,
  timings: atm.timings,
  atmHours: atm.atmHours,
  numberOfATMs: atm.numberOfATMs,
  isOpen: atm.isOpen,
});
```

## Implementation Steps

### Step 1: Create Request DTOs

#### AtmFilterRequest

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/dto/AtmFilterRequest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for filtering ATMs.
 *
 * Legacy request body (atmController.js:17-22):
 * {
 *   "isOpenNow": boolean,      // Optional
 *   "isInterPlanetary": boolean // Optional
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmFilterRequest {

    /**
     * Filter for currently open ATMs.
     * If true, adds isOpen: true to query.
     */
    private Boolean isOpenNow;

    /**
     * Filter for interplanetary ATMs.
     * If true, sets interPlanetary: true (overrides default false).
     */
    private Boolean isInterPlanetary;
}
```

#### AtmCreateRequest

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/dto/AtmCreateRequest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new ATM.
 *
 * Legacy request body (atmController.js:42-57):
 * Flat structure with nested object fields unpacked.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmCreateRequest {

    private String name;

    // Address fields (flattened from nested object)
    private String street;
    private String city;
    private String state;
    private String zip;

    // Coordinates fields (flattened from nested object)
    private Double latitude;
    private Double longitude;

    // Timings fields (flattened from nested object)
    private String monFri;
    private String satSun;
    private String holidays;  // Optional

    private String atmHours;
    private Integer numberOfATMs;
    private Boolean isOpen;
    private Boolean interPlanetary;
}
```

### Step 2: Create Response DTOs

#### AtmListResponse

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/dto/AtmListResponse.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for ATM list endpoint.
 *
 * Legacy projection (atmController.js:23-28):
 * {
 *   name: 1,
 *   coordinates: 1,
 *   address: 1,
 *   isOpen: 1,
 * }
 *
 * Note: _id is included by default in MongoDB projections.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmListResponse {

    /**
     * MongoDB ObjectId as string.
     * JSON field name "_id" to match legacy response.
     */
    @JsonProperty("_id")
    private String id;

    private String name;

    private Coordinates coordinates;

    private Address address;

    private Boolean isOpen;

    /**
     * Factory method to create response from entity.
     */
    public static AtmListResponse fromEntity(Atm atm) {
        AtmListResponse response = new AtmListResponse();
        response.setId(atm.getId());
        response.setName(atm.getName());
        response.setCoordinates(atm.getCoordinates());
        response.setAddress(atm.getAddress());
        response.setIsOpen(atm.getIsOpen());
        return response;
    }
}
```

#### AtmDetailResponse

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/dto/AtmDetailResponse.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for specific ATM endpoint.
 *
 * Legacy response (atmController.js:96-102):
 * {
 *   coordinates: atm.coordinates,
 *   timings: atm.timings,
 *   atmHours: atm.atmHours,
 *   numberOfATMs: atm.numberOfATMs,
 *   isOpen: atm.isOpen,
 * }
 *
 * Note: Excludes _id, name, address, interPlanetary, timestamps
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmDetailResponse {

    private Coordinates coordinates;

    private Timings timings;

    private String atmHours;

    private Integer numberOfATMs;

    private Boolean isOpen;

    /**
     * Factory method to create response from entity.
     */
    public static AtmDetailResponse fromEntity(Atm atm) {
        AtmDetailResponse response = new AtmDetailResponse();
        response.setCoordinates(atm.getCoordinates());
        response.setTimings(atm.getTimings());
        response.setAtmHours(atm.getAtmHours());
        response.setNumberOfATMs(atm.getNumberOfATMs());
        response.setIsOpen(atm.getIsOpen());
        return response;
    }
}
```

### Step 3: Create Custom Exception

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/exception/AtmNotFoundException.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.exception;

/**
 * Exception thrown when an ATM is not found.
 *
 * Maps to 404 status code in controller layer.
 */
public class AtmNotFoundException extends RuntimeException {

    public AtmNotFoundException(String message) {
        super(message);
    }

    public AtmNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Step 4: Create Service Implementation

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/service/AtmService.java`

```java
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
```

## Directory Structure After Phase 3

```
atm-locator-java/src/main/java/com/martianbank/atmlocator/
├── AtmLocatorApplication.java
├── config/
│   ├── MongoConfig.java
│   ├── CorsConfig.java
│   └── DataSeeder.java
├── model/
│   ├── Atm.java
│   ├── Address.java
│   ├── Coordinates.java
│   └── Timings.java
├── repository/
│   └── AtmRepository.java
├── service/
│   └── AtmService.java (new)
├── dto/
│   ├── AtmFilterRequest.java (new)
│   ├── AtmCreateRequest.java (new)
│   ├── AtmListResponse.java (new)
│   └── AtmDetailResponse.java (new)
└── exception/
    └── AtmNotFoundException.java (new)
```

## Critical Logic to Verify

### 1. Query Building
```
Input: null or empty body
Output: { interPlanetary: false }

Input: { isOpenNow: true }
Output: { interPlanetary: false, isOpen: true }

Input: { isInterPlanetary: true }
Output: { interPlanetary: true }

Input: { isOpenNow: true, isInterPlanetary: true }
Output: { interPlanetary: true, isOpen: true }
```

### 2. Response Projection
- `getATMs`: Only returns `_id`, `name`, `coordinates`, `address`, `isOpen`
- `getSpecificATM`: Only returns `coordinates`, `timings`, `atmHours`, `numberOfATMs`, `isOpen`

### 3. Randomization
- Results are shuffled before returning
- Maximum 4 results returned

## Success Criteria

### Automated Verification

- [x] Project compiles: `./gradlew build -x test`
- [x] All new classes are properly annotated
- [x] No compilation errors

## Notes

- `AtmListResponse` uses `@JsonProperty("_id")` to ensure the JSON field name matches legacy MongoDB behavior
- The shuffle implementation uses `Collections.shuffle()` which provides better randomization than the legacy `Math.random() - 0.5` approach, but achieves the same functional result
- Default value for `interPlanetary` is `false` when building from request (matching legacy Mongoose schema default)
