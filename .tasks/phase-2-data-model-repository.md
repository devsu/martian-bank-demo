# Phase 2: Data Model & Repository Layer

## Overview

Implement the ATM entity with MongoDB annotations, embedded objects for nested structures, repository interface, and database seeding logic that mirrors the legacy Node.js behavior.

## Prerequisites

- Phase 1 and 1.5 completed
- Project compiles and tests pass

## Deliverables

1. `Atm.java` - Main entity class
2. `Address.java` - Embedded address object
3. `Coordinates.java` - Embedded coordinates object
4. `Timings.java` - Embedded timings object
5. `AtmRepository.java` - Spring Data MongoDB repository
6. `DataSeeder.java` - Database seeding component
7. `atm_data.json` - Seed data file (copied from legacy)

## Legacy Reference

**Mongoose Schema** (`atm-locator/models/atmModel.js:9-77`):
```javascript
const atmSchema = mongoose.Schema({
    name: { type: String, required: true },
    address: {
        street: { type: String, required: true },
        city: { type: String, required: true },
        state: { type: String, required: true },
        zip: { type: String, required: true },
    },
    coordinates: {
        latitude: { type: Number, required: true },
        longitude: { type: Number, required: true },
    },
    timings: {
        monFri: { type: String, required: true },
        satSun: { type: String, required: true },
        holidays: { type: String },  // Optional
    },
    atmHours: { type: String, required: true },
    numberOfATMs: { type: Number, required: true },
    isOpen: { type: Boolean, required: true },
    interPlanetary: { type: Boolean, required: true, default: false },
}, { timestamps: true });
```

## Implementation Steps

### Step 1: Create Embedded Classes

#### Address Class

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/model/Address.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded address object matching legacy Mongoose schema.
 * All fields are required except as noted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;

    private String city;

    private String state;

    private String zip;
}
```

#### Coordinates Class

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/model/Coordinates.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded coordinates object matching legacy Mongoose schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    private Double latitude;

    private Double longitude;
}
```

#### Timings Class

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/model/Timings.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded timings object matching legacy Mongoose schema.
 * holidays field is optional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timings {

    private String monFri;

    private String satSun;

    private String holidays;  // Optional in legacy schema
}
```

### Step 2: Create Main ATM Entity

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/model/Atm.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * ATM entity matching legacy Mongoose schema exactly.
 *
 * Collection name: "atms" (Mongoose pluralizes "ATM" model name)
 *
 * Legacy reference: atm-locator/models/atmModel.js:9-77
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "atms")
public class Atm {

    @Id
    private String id;

    private String name;

    private Address address;

    private Coordinates coordinates;

    private Timings timings;

    private String atmHours;

    private Integer numberOfATMs;

    @Field("isOpen")
    private Boolean isOpen;

    @Field("interPlanetary")
    private Boolean interPlanetary = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * MongoDB version field (equivalent to Mongoose __v)
     * Included for document compatibility but not actively used.
     */
    @Field("__v")
    private Integer version;
}
```

### Step 3: Create Repository Interface

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/repository/AtmRepository.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.repository;

import com.martianbank.atmlocator.model.Atm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
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
```

### Step 4: Copy Seed Data File

**File**: `atm-locator-java/src/main/resources/atm_data.json`

Copy the content from `atm-locator/config/atm_data.json` (13 ATM records).

> **Note**: The JSON file should be copied exactly as-is from the legacy application. The DataSeeder will handle the MongoDB extended JSON format (`$oid`, `$date`) during processing.

### Step 5: Create Data Seeder

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/config/DataSeeder.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with ATM data on application startup.
 *
 * Mirrors legacy behavior from atm-locator/config/db.js:40-64:
 * 1. Read atm_data.json
 * 2. Process MongoDB extended JSON format ($oid, $date)
 * 3. Drop existing ATM collection
 * 4. Insert seed data
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private static final String SEED_FILE = "atm_data.json";

    private final AtmRepository atmRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(AtmRepository atmRepository, ObjectMapper objectMapper) {
        this.atmRepository = atmRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        logger.info("Seeding database with data from {} ...", SEED_FILE);

        try {
            List<Atm> atms = loadAndProcessSeedData();
            dropAndReseed(atms);
            logger.info("Database seeded with {} records.", atms.size());
        } catch (Exception e) {
            logger.error("Error seeding database: {}", e.getMessage());
        }
    }

    /**
     * Load seed data from JSON file and process MongoDB extended JSON format.
     */
    List<Atm> loadAndProcessSeedData() throws Exception {
        ClassPathResource resource = new ClassPathResource(SEED_FILE);
        InputStream inputStream = resource.getInputStream();

        List<JsonNode> jsonNodes = objectMapper.readValue(
                inputStream,
                new TypeReference<List<JsonNode>>() {}
        );

        List<Atm> atms = new ArrayList<>();
        for (JsonNode node : jsonNodes) {
            Atm atm = processAtmNode(node);
            atms.add(atm);
        }

        return atms;
    }

    /**
     * Process a single ATM JSON node, handling MongoDB extended JSON format.
     *
     * Legacy processing (db.js:48-53):
     * - _id: { $oid: "..." } -> ObjectId
     * - createdAt: { $date: "..." } -> Date
     * - updatedAt: { $date: "..." } -> Date
     */
    private Atm processAtmNode(JsonNode node) {
        Atm atm = new Atm();

        // Process _id ($oid format)
        JsonNode idNode = node.get("_id");
        if (idNode != null && idNode.has("$oid")) {
            atm.setId(idNode.get("$oid").asText());
        }

        // Basic fields
        atm.setName(node.get("name").asText());
        atm.setAtmHours(node.get("atmHours").asText());
        atm.setNumberOfATMs(node.get("numberOfATMs").asInt());
        atm.setIsOpen(node.get("isOpen").asBoolean());
        atm.setInterPlanetary(node.has("interPlanetary") ? node.get("interPlanetary").asBoolean() : false);

        // Process address
        JsonNode addressNode = node.get("address");
        if (addressNode != null) {
            Address address = new Address();
            address.setStreet(addressNode.get("street").asText());
            address.setCity(addressNode.get("city").asText());
            address.setState(addressNode.get("state").asText());
            address.setZip(addressNode.get("zip").asText());
            atm.setAddress(address);
        }

        // Process coordinates
        JsonNode coordsNode = node.get("coordinates");
        if (coordsNode != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setLatitude(coordsNode.get("latitude").asDouble());
            coordinates.setLongitude(coordsNode.get("longitude").asDouble());
            atm.setCoordinates(coordinates);
        }

        // Process timings
        JsonNode timingsNode = node.get("timings");
        if (timingsNode != null) {
            Timings timings = new Timings();
            timings.setMonFri(timingsNode.get("monFri").asText());
            timings.setSatSun(timingsNode.get("satSun").asText());
            if (timingsNode.has("holidays") && !timingsNode.get("holidays").isNull()) {
                timings.setHolidays(timingsNode.get("holidays").asText());
            }
            atm.setTimings(timings);
        }

        // Process timestamps ($date format)
        JsonNode createdAtNode = node.get("createdAt");
        if (createdAtNode != null && createdAtNode.has("$date")) {
            atm.setCreatedAt(Instant.parse(createdAtNode.get("$date").asText()));
        }

        JsonNode updatedAtNode = node.get("updatedAt");
        if (updatedAtNode != null && updatedAtNode.has("$date")) {
            atm.setUpdatedAt(Instant.parse(updatedAtNode.get("$date").asText()));
        }

        // Version field
        if (node.has("__v")) {
            atm.setVersion(node.get("__v").asInt());
        }

        return atm;
    }

    /**
     * Drop existing collection and insert seed data.
     *
     * Legacy behavior (db.js:55-60):
     * - ATM.collection.drop()
     * - ATM.insertMany(processedData)
     */
    private void dropAndReseed(List<Atm> atms) {
        try {
            atmRepository.deleteAll();
            logger.debug("Dropped existing ATM collection");
        } catch (Exception e) {
            logger.debug("Error dropping collection (may not exist): {}", e.getMessage());
        }

        atmRepository.saveAll(atms);
    }
}
```

### Step 6: Enable Auditing for Timestamps

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/config/MongoConfig.java`

Add the `@EnableMongoAuditing` annotation to the existing `MongoConfig` class:

```java
// Add this import
import org.springframework.data.mongodb.config.EnableMongoAuditing;

// Add this annotation to the class
@Configuration
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {
    // ... existing code
}
```

## Directory Structure After Phase 2

```
atm-locator-java/src/main/java/com/martianbank/atmlocator/
├── AtmLocatorApplication.java
├── config/
│   ├── MongoConfig.java (updated with @EnableMongoAuditing)
│   ├── CorsConfig.java
│   └── DataSeeder.java (new)
├── model/
│   ├── Atm.java (new)
│   ├── Address.java (new)
│   ├── Coordinates.java (new)
│   └── Timings.java (new)
└── repository/
    └── AtmRepository.java (new)

atm-locator-java/src/main/resources/
├── application.yml
└── atm_data.json (new - copy from legacy)
```

## Success Criteria

### Automated Verification

- [x] Project compiles: `./gradlew build -x test`
- [x] Seed data file exists and is valid JSON
- [x] Entity classes compile without Lombok errors

## Notes

- The `@Field` annotation is used on `isOpen` and `interPlanetary` to ensure exact field name matching with MongoDB documents
- The `__v` version field is included for compatibility but not actively used by Spring Data
- Timestamps use `Instant` instead of `Date` for modern Java date/time handling
- The DataSeeder runs on every startup, matching legacy behavior (drop and reseed)
