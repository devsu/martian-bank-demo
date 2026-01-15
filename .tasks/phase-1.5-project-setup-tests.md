# Phase 1.5: Tests for Project Setup & Configuration

## Overview

Create unit tests for the configuration classes created in Phase 1. Focus on testing the MongoDB connection string building logic and CORS configuration.

## Prerequisites

- Phase 1 completed successfully
- Project compiles without errors

## Deliverables

1. `MongoConfigTest.java` - Tests for MongoDB configuration
2. `CorsConfigTest.java` - Tests for CORS configuration
3. All tests passing

## Implementation Steps

### Step 1: Create Test Directory Structure

Ensure the following test directories exist:

```
atm-locator-java/src/test/java/com/martianbank/atmlocator/
├── AtmLocatorApplicationTest.java  (from Phase 1)
└── config/
    ├── MongoConfigTest.java
    └── CorsConfigTest.java
```

### Step 2: Create MongoConfig Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/config/MongoConfigTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    @Test
    void getDatabaseName_returnsConfiguredDatabaseName() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseName", "testdb");

        // When
        String dbName = config.getDatabaseName();

        // Then
        assertThat(dbName).isEqualTo("testdb");
    }

    @Test
    void buildConnectionString_withDatabaseHost_usesLocalFormat() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "localhost");
        ReflectionTestUtils.setField(config, "mongoUri", "");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb://localhost:27017/");
    }

    @Test
    void buildConnectionString_withMongoUri_usesAtlasFormat() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "");
        ReflectionTestUtils.setField(config, "mongoUri", "mongodb+srv://user:pass@cluster.mongodb.net/db");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb+srv://user:pass@cluster.mongodb.net/db");
    }

    @Test
    void buildConnectionString_withBothSet_databaseHostTakesPrecedence() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "mongo");
        ReflectionTestUtils.setField(config, "mongoUri", "mongodb+srv://user:pass@cluster.mongodb.net/db");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb://mongo:27017/");
    }

    @Test
    void buildConnectionString_withNeitherSet_usesDefaultLocalhost() {
        // Given
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "databaseHost", "");
        ReflectionTestUtils.setField(config, "mongoUri", "");
        ReflectionTestUtils.setField(config, "databaseName", "test");

        // When
        String connectionString = ReflectionTestUtils.invokeMethod(config, "buildConnectionString");

        // Then
        assertThat(connectionString).isEqualTo("mongodb://localhost:27017/");
    }

    @Test
    void maskPassword_masksCredentialsInUri() {
        // Given
        MongoConfig config = new MongoConfig();
        String uri = "mongodb://root:secretpassword@localhost:27017/";

        // When
        String masked = ReflectionTestUtils.invokeMethod(config, "maskPassword", uri);

        // Then
        assertThat(masked).isEqualTo("mongodb://***:***@localhost:27017/");
        assertThat(masked).doesNotContain("root");
        assertThat(masked).doesNotContain("secretpassword");
    }

    @Test
    void maskPassword_handlesUriWithoutCredentials() {
        // Given
        MongoConfig config = new MongoConfig();
        String uri = "mongodb://localhost:27017/";

        // When
        String masked = ReflectionTestUtils.invokeMethod(config, "maskPassword", uri);

        // Then
        assertThat(masked).isEqualTo("mongodb://localhost:27017/");
    }
}
```

### Step 3: Create CorsConfig Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/config/CorsConfigTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @Test
    void corsFilter_isNotNull() {
        // When
        CorsFilter filter = corsConfig.corsFilter();

        // Then
        assertThat(filter).isNotNull();
    }

    @Test
    void corsFilter_allowsAllOriginPatterns() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowedOriginPatterns()).contains("*");
    }

    @Test
    void corsFilter_allowsCredentials() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowCredentials()).isTrue();
    }

    @Test
    void corsFilter_allowsAllHeaders() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowedHeaders()).contains("*");
    }

    @Test
    void corsFilter_allowsAllMethods() throws Exception {
        // Given
        CorsFilter filter = corsConfig.corsFilter();

        // When
        CorsConfiguration config = extractCorsConfiguration(filter);

        // Then
        assertThat(config.getAllowedMethods()).contains("*");
    }

    /**
     * Helper method to extract CorsConfiguration from CorsFilter.
     * Uses reflection since CorsFilter doesn't expose its configuration directly.
     */
    private CorsConfiguration extractCorsConfiguration(CorsFilter filter) throws Exception {
        Field configSourceField = CorsFilter.class.getDeclaredField("configSource");
        configSourceField.setAccessible(true);
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) configSourceField.get(filter);

        // Get configuration for the root path pattern
        return source.getCorsConfigurations().get("/**");
    }
}
```

### Step 4: Update Application Test (Enhanced)

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/AtmLocatorApplicationTest.java`

Update the existing test with additional verification:

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class AtmLocatorApplicationTest {

    @Test
    void mainClass_exists() {
        // Verify main class can be instantiated
        assertThatNoException().isThrownBy(() -> {
            AtmLocatorApplication app = new AtmLocatorApplication();
        });
    }

    @Test
    void mainMethod_exists() {
        // Verify main method signature is correct
        // This doesn't actually start the app, just verifies the method exists
        assertThatNoException().isThrownBy(() -> {
            AtmLocatorApplication.class.getMethod("main", String[].class);
        });
    }
}
```

## Success Criteria

### Automated Verification

- [x] All tests pass: `cd atm-locator-java && ./gradlew test`
- [x] No test failures in output
- [x] Test report generated: `build/reports/tests/test/index.html`

## Test Summary

| Test Class | Test Count | Coverage Target |
|------------|------------|-----------------|
| `MongoConfigTest` | 7 | Connection string logic |
| `CorsConfigTest` | 5 | CORS policy verification |
| `AtmLocatorApplicationTest` | 2 | Main class existence |

## Notes

- These tests use `ReflectionTestUtils` to test private methods in `MongoConfig` - this is acceptable for configuration classes
- The `CorsConfigTest` uses reflection to extract the `CorsConfiguration` from the filter since it's not directly exposed
- The application context test is intentionally minimal since MongoDB isn't available in the test environment
- Later phases will add more comprehensive integration tests once the service layer is complete
