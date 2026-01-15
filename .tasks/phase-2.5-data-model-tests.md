# Phase 2.5: Tests for Data Model & Repository Layer

## Overview

Create unit tests for the data model classes, repository interface, and database seeder with mocked MongoDB dependencies.

## Prerequisites

- Phase 2 completed successfully
- All model and repository classes compile without errors

## Deliverables

1. `AtmTest.java` - Entity class tests
2. `AddressTest.java` - Address embedded object tests
3. `CoordinatesTest.java` - Coordinates embedded object tests
4. `TimingsTest.java` - Timings embedded object tests
5. `AtmRepositoryTest.java` - Repository method tests
6. `DataSeederTest.java` - Seeder logic tests
7. `TestDataFactory.java` - Reusable test data factory

## Implementation Steps

### Step 1: Create Test Data Factory

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/testutil/TestDataFactory.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.testutil;

import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating test data objects.
 * Provides consistent test data across all test classes.
 */
public class TestDataFactory {

    public static final String TEST_ATM_ID = "64a6f1cc8c1899820dbdf25a";
    public static final String TEST_ATM_NAME = "Martian ATM (Highway)";

    /**
     * Creates a complete ATM entity with all fields populated.
     * Based on first record from atm_data.json
     */
    public static Atm createTestAtm() {
        Atm atm = new Atm();
        atm.setId(TEST_ATM_ID);
        atm.setName(TEST_ATM_NAME);
        atm.setAddress(createTestAddress());
        atm.setCoordinates(createTestCoordinates());
        atm.setTimings(createTestTimings());
        atm.setAtmHours("24 hours");
        atm.setNumberOfATMs(2);
        atm.setIsOpen(true);
        atm.setInterPlanetary(false);
        atm.setCreatedAt(Instant.parse("2023-07-06T16:54:36.22Z"));
        atm.setUpdatedAt(Instant.parse("2023-07-06T16:54:36.22Z"));
        atm.setVersion(0);
        return atm;
    }

    /**
     * Creates an ATM with interPlanetary = true
     */
    public static Atm createInterplanetaryAtm() {
        Atm atm = createTestAtm();
        atm.setId("64b072fd6981fda9e346bdde");
        atm.setName("Earthern ATM (Georgia Tech)");
        atm.setInterPlanetary(true);
        atm.getAddress().setCity("Atlanta");
        atm.getAddress().setState("Georgia");
        return atm;
    }

    /**
     * Creates an ATM with isOpen = false
     */
    public static Atm createClosedAtm() {
        Atm atm = createTestAtm();
        atm.setId("64a6f2268c1899820dbdf25c");
        atm.setName("Martian ATM (Claytor Lake)");
        atm.setIsOpen(false);
        return atm;
    }

    /**
     * Creates a list of 5 test ATMs for testing limit functionality.
     * Mix of open/closed and planetary/interplanetary.
     */
    public static List<Atm> createAtmList(int count) {
        List<Atm> atms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Atm atm = createTestAtm();
            atm.setId("testId" + i);
            atm.setName("Test ATM " + i);
            atm.setIsOpen(i % 2 == 0);  // Alternate open/closed
            atms.add(atm);
        }
        return atms;
    }

    /**
     * Creates a list of non-interplanetary ATMs (Mars ATMs).
     */
    public static List<Atm> createMarsAtms(int count) {
        List<Atm> atms = createAtmList(count);
        atms.forEach(atm -> atm.setInterPlanetary(false));
        return atms;
    }

    /**
     * Creates a list of open, non-interplanetary ATMs.
     */
    public static List<Atm> createOpenMarsAtms(int count) {
        List<Atm> atms = createAtmList(count);
        atms.forEach(atm -> {
            atm.setInterPlanetary(false);
            atm.setIsOpen(true);
        });
        return atms;
    }

    /**
     * Creates a test Address object.
     */
    public static Address createTestAddress() {
        Address address = new Address();
        address.setStreet("14th Street, Martian Way");
        address.setCity("Musk City");
        address.setState("Mars");
        address.setZip("40411");
        return address;
    }

    /**
     * Creates a test Coordinates object.
     */
    public static Coordinates createTestCoordinates() {
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(37.775);
        coordinates.setLongitude(-81.188);
        return coordinates;
    }

    /**
     * Creates a test Timings object with all fields.
     */
    public static Timings createTestTimings() {
        Timings timings = new Timings();
        timings.setMonFri("9:00 AM - 5:00 PM");
        timings.setSatSun("10:00 AM - 3:00 PM");
        timings.setHolidays("Closed on holidays");
        return timings;
    }

    /**
     * Creates a Timings object without holidays (optional field).
     */
    public static Timings createTimingsWithoutHolidays() {
        Timings timings = new Timings();
        timings.setMonFri("9:00 AM - 5:00 PM");
        timings.setSatSun("10:00 AM - 3:00 PM");
        timings.setHolidays(null);
        return timings;
    }
}
```

### Step 2: Create Model Tests

#### ATM Entity Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/model/AtmTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AtmTest {

    @Test
    void atm_defaultInterPlanetary_isFalse() {
        Atm atm = new Atm();
        assertThat(atm.getInterPlanetary()).isFalse();
    }

    @Test
    void atm_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // Then
        assertThat(atm.getId()).isEqualTo(TestDataFactory.TEST_ATM_ID);
        assertThat(atm.getName()).isEqualTo(TestDataFactory.TEST_ATM_NAME);
        assertThat(atm.getAddress()).isNotNull();
        assertThat(atm.getCoordinates()).isNotNull();
        assertThat(atm.getTimings()).isNotNull();
        assertThat(atm.getAtmHours()).isEqualTo("24 hours");
        assertThat(atm.getNumberOfATMs()).isEqualTo(2);
        assertThat(atm.getIsOpen()).isTrue();
        assertThat(atm.getInterPlanetary()).isFalse();
        assertThat(atm.getCreatedAt()).isNotNull();
        assertThat(atm.getUpdatedAt()).isNotNull();
    }

    @Test
    void atm_equality_basedOnAllFields() {
        // Given
        Atm atm1 = TestDataFactory.createTestAtm();
        Atm atm2 = TestDataFactory.createTestAtm();

        // Then
        assertThat(atm1).isEqualTo(atm2);
        assertThat(atm1.hashCode()).isEqualTo(atm2.hashCode());
    }

    @Test
    void atm_toString_containsAllFields() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // When
        String toString = atm.toString();

        // Then
        assertThat(toString).contains(TestDataFactory.TEST_ATM_ID);
        assertThat(toString).contains(TestDataFactory.TEST_ATM_NAME);
    }

    @Test
    void atm_timestamps_acceptInstant() {
        // Given
        Atm atm = new Atm();
        Instant now = Instant.now();

        // When
        atm.setCreatedAt(now);
        atm.setUpdatedAt(now);

        // Then
        assertThat(atm.getCreatedAt()).isEqualTo(now);
        assertThat(atm.getUpdatedAt()).isEqualTo(now);
    }
}
```

#### Address Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/model/AddressTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void address_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Address address = TestDataFactory.createTestAddress();

        // Then
        assertThat(address.getStreet()).isEqualTo("14th Street, Martian Way");
        assertThat(address.getCity()).isEqualTo("Musk City");
        assertThat(address.getState()).isEqualTo("Mars");
        assertThat(address.getZip()).isEqualTo("40411");
    }

    @Test
    void address_equality_basedOnAllFields() {
        // Given
        Address address1 = TestDataFactory.createTestAddress();
        Address address2 = TestDataFactory.createTestAddress();

        // Then
        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void address_allArgsConstructor_setsAllFields() {
        // When
        Address address = new Address("Street", "City", "State", "12345");

        // Then
        assertThat(address.getStreet()).isEqualTo("Street");
        assertThat(address.getCity()).isEqualTo("City");
        assertThat(address.getState()).isEqualTo("State");
        assertThat(address.getZip()).isEqualTo("12345");
    }

    @Test
    void address_noArgsConstructor_createsEmptyObject() {
        // When
        Address address = new Address();

        // Then
        assertThat(address.getStreet()).isNull();
        assertThat(address.getCity()).isNull();
        assertThat(address.getState()).isNull();
        assertThat(address.getZip()).isNull();
    }
}
```

#### Coordinates Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/model/CoordinatesTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesTest {

    @Test
    void coordinates_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Coordinates coordinates = TestDataFactory.createTestCoordinates();

        // Then
        assertThat(coordinates.getLatitude()).isEqualTo(37.775);
        assertThat(coordinates.getLongitude()).isEqualTo(-81.188);
    }

    @Test
    void coordinates_equality_basedOnAllFields() {
        // Given
        Coordinates coord1 = TestDataFactory.createTestCoordinates();
        Coordinates coord2 = TestDataFactory.createTestCoordinates();

        // Then
        assertThat(coord1).isEqualTo(coord2);
        assertThat(coord1.hashCode()).isEqualTo(coord2.hashCode());
    }

    @Test
    void coordinates_allArgsConstructor_setsAllFields() {
        // When
        Coordinates coordinates = new Coordinates(40.7128, -74.0060);

        // Then
        assertThat(coordinates.getLatitude()).isEqualTo(40.7128);
        assertThat(coordinates.getLongitude()).isEqualTo(-74.0060);
    }

    @Test
    void coordinates_handlesNegativeValues() {
        // Given
        Coordinates coordinates = new Coordinates(-94.764, 31.1897);

        // Then
        assertThat(coordinates.getLatitude()).isEqualTo(-94.764);
        assertThat(coordinates.getLongitude()).isEqualTo(31.1897);
    }
}
```

#### Timings Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/model/TimingsTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimingsTest {

    @Test
    void timings_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Timings timings = TestDataFactory.createTestTimings();

        // Then
        assertThat(timings.getMonFri()).isEqualTo("9:00 AM - 5:00 PM");
        assertThat(timings.getSatSun()).isEqualTo("10:00 AM - 3:00 PM");
        assertThat(timings.getHolidays()).isEqualTo("Closed on holidays");
    }

    @Test
    void timings_holidaysOptional_canBeNull() {
        // Given
        Timings timings = TestDataFactory.createTimingsWithoutHolidays();

        // Then
        assertThat(timings.getMonFri()).isNotNull();
        assertThat(timings.getSatSun()).isNotNull();
        assertThat(timings.getHolidays()).isNull();
    }

    @Test
    void timings_equality_basedOnAllFields() {
        // Given
        Timings timings1 = TestDataFactory.createTestTimings();
        Timings timings2 = TestDataFactory.createTestTimings();

        // Then
        assertThat(timings1).isEqualTo(timings2);
        assertThat(timings1.hashCode()).isEqualTo(timings2.hashCode());
    }

    @Test
    void timings_allArgsConstructor_setsAllFields() {
        // When
        Timings timings = new Timings("Mon-Fri", "Sat-Sun", "Holidays");

        // Then
        assertThat(timings.getMonFri()).isEqualTo("Mon-Fri");
        assertThat(timings.getSatSun()).isEqualTo("Sat-Sun");
        assertThat(timings.getHolidays()).isEqualTo("Holidays");
    }
}
```

### Step 3: Create Repository Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/repository/AtmRepositoryTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.repository;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AtmRepository interface.
 *
 * Since we're mocking the database, these tests verify the repository
 * interface contract rather than actual database operations.
 */
class AtmRepositoryTest {

    @Test
    void repository_hasFindByInterPlanetaryMethod() throws NoSuchMethodException {
        // Verify the method exists with correct signature
        Method method = AtmRepository.class.getMethod("findByInterPlanetary", Boolean.class);
        assertThat(method).isNotNull();
        assertThat(method.getReturnType().getSimpleName()).isEqualTo("List");
    }

    @Test
    void repository_hasFindByInterPlanetaryAndIsOpenMethod() throws NoSuchMethodException {
        // Verify the method exists with correct signature
        Method method = AtmRepository.class.getMethod(
                "findByInterPlanetaryAndIsOpen",
                Boolean.class,
                Boolean.class
        );
        assertThat(method).isNotNull();
        assertThat(method.getReturnType().getSimpleName()).isEqualTo("List");
    }

    @Test
    void repository_extendsMongoRepository() {
        // Verify interface hierarchy
        Class<?>[] interfaces = AtmRepository.class.getInterfaces();
        assertThat(interfaces).hasSize(1);
        assertThat(interfaces[0].getSimpleName()).isEqualTo("MongoRepository");
    }
}
```

### Step 4: Create DataSeeder Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/config/DataSeederTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private AtmRepository atmRepository;

    @Captor
    private ArgumentCaptor<List<Atm>> atmListCaptor;

    private DataSeeder dataSeeder;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        dataSeeder = new DataSeeder(atmRepository, objectMapper);
    }

    @Test
    void run_deletesExistingDataBeforeSeeding() throws Exception {
        // When
        dataSeeder.run();

        // Then
        verify(atmRepository).deleteAll();
        verify(atmRepository).saveAll(anyList());
    }

    @Test
    void run_callsDeleteBeforeSave() throws Exception {
        // When
        dataSeeder.run();

        // Then - verify order of operations
        var inOrder = inOrder(atmRepository);
        inOrder.verify(atmRepository).deleteAll();
        inOrder.verify(atmRepository).saveAll(anyList());
    }

    @Test
    void loadAndProcessSeedData_returns13Records() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        assertThat(atms).hasSize(13);
    }

    @Test
    void loadAndProcessSeedData_processesMongoOidFormat() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - first ATM should have correct ID from $oid
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getId()).isEqualTo("64a6f1cc8c1899820dbdf25a");
    }

    @Test
    void loadAndProcessSeedData_processesMongoDateFormat() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - timestamps should be parsed from $date format
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getCreatedAt()).isNotNull();
        assertThat(firstAtm.getUpdatedAt()).isNotNull();
    }

    @Test
    void loadAndProcessSeedData_parsesNestedAddressObject() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getAddress()).isNotNull();
        assertThat(firstAtm.getAddress().getStreet()).isEqualTo("14th Street, Martian Way");
        assertThat(firstAtm.getAddress().getCity()).isEqualTo("Musk City");
        assertThat(firstAtm.getAddress().getState()).isEqualTo("Mars");
        assertThat(firstAtm.getAddress().getZip()).isEqualTo("40411");
    }

    @Test
    void loadAndProcessSeedData_parsesNestedCoordinatesObject() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getCoordinates()).isNotNull();
        assertThat(firstAtm.getCoordinates().getLatitude()).isEqualTo(37.775);
        assertThat(firstAtm.getCoordinates().getLongitude()).isEqualTo(-81.188);
    }

    @Test
    void loadAndProcessSeedData_parsesNestedTimingsObject() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getTimings()).isNotNull();
        assertThat(firstAtm.getTimings().getMonFri()).isEqualTo("9:00 AM - 5:00 PM");
        assertThat(firstAtm.getTimings().getSatSun()).isEqualTo("10:00 AM - 3:00 PM");
        assertThat(firstAtm.getTimings().getHolidays()).isEqualTo("Closed on holidays");
    }

    @Test
    void loadAndProcessSeedData_parsesInterPlanetaryField() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - most ATMs should be non-interplanetary
        long nonInterplanetary = atms.stream()
                .filter(atm -> !atm.getInterPlanetary())
                .count();
        long interplanetary = atms.stream()
                .filter(Atm::getInterPlanetary)
                .count();

        assertThat(nonInterplanetary).isEqualTo(11);  // 11 Mars ATMs
        assertThat(interplanetary).isEqualTo(2);      // 2 interplanetary ATMs
    }

    @Test
    void loadAndProcessSeedData_parsesIsOpenField() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - verify isOpen field is correctly parsed
        long openAtms = atms.stream()
                .filter(Atm::getIsOpen)
                .count();

        assertThat(openAtms).isGreaterThan(0);
        assertThat(openAtms).isLessThan(13);  // Not all ATMs are open
    }

    @Test
    void run_savesAllLoadedAtms() throws Exception {
        // When
        dataSeeder.run();

        // Then
        verify(atmRepository).saveAll(atmListCaptor.capture());
        List<Atm> savedAtms = atmListCaptor.getValue();
        assertThat(savedAtms).hasSize(13);
    }

    @Test
    void run_continuesIfDeleteFails() throws Exception {
        // Given
        doThrow(new RuntimeException("Collection doesn't exist"))
                .when(atmRepository).deleteAll();

        // When - should not throw
        dataSeeder.run();

        // Then - save should still be attempted
        verify(atmRepository).saveAll(anyList());
    }
}
```

## Success Criteria

### Automated Verification

- [ ] All tests pass: `cd atm-locator-java && ./gradlew test`
- [ ] Test report shows all tests green
- [ ] No compilation errors in test classes

### Manual Verification

- [ ] All test files created in correct directories
- [ ] TestDataFactory provides comprehensive test data
- [ ] DataSeeder tests verify MongoDB extended JSON processing

## Test Summary

| Test Class | Test Count | Purpose |
|------------|------------|---------|
| `AtmTest` | 5 | Entity field access and defaults |
| `AddressTest` | 4 | Address embedded object |
| `CoordinatesTest` | 4 | Coordinates embedded object |
| `TimingsTest` | 4 | Timings embedded object |
| `AtmRepositoryTest` | 3 | Repository interface contract |
| `DataSeederTest` | 12 | Seed data processing logic |
| `TestDataFactory` | N/A | Utility class (not tested directly) |

**Total: 32 tests**

## Notes

- Repository tests verify interface contract rather than actual MongoDB queries (since we're mocking)
- DataSeeder tests use the actual `atm_data.json` file to verify parsing logic
- TestDataFactory is used across all test classes for consistent test data
- The DataSeeder test for "continues if delete fails" matches legacy behavior where collection.drop() errors are caught and logged
