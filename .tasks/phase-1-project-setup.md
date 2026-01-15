# Phase 1: Project Setup & Configuration

## Overview

Create the Spring Boot 3.x project structure with Java 25, Gradle build configuration, and MongoDB connection setup.

## Prerequisites

- Java 25 JDK installed
- Gradle 8.x installed
- Access to the `martian-bank-demo` repository

## Deliverables

1. New `atm-locator-java/` directory with Spring Boot project
2. `build.gradle` with all dependencies
3. `settings.gradle` with project name
4. `application.yml` with MongoDB and server configuration
5. Main application class
6. MongoDB configuration class
7. CORS configuration class

## Implementation Steps

### Step 1: Create Project Directory Structure

Create the following directory structure:

```
atm-locator-java/
├── build.gradle
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/com/martianbank/atmlocator/
│   │   │   ├── AtmLocatorApplication.java
│   │   │   └── config/
│   │   │       ├── MongoConfig.java
│   │   │       └── CorsConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/martianbank/atmlocator/
│           └── AtmLocatorApplicationTest.java
```

### Step 2: Create `settings.gradle`

**File**: `atm-locator-java/settings.gradle`

```groovy
rootProject.name = 'atm-locator'
```

### Step 3: Create `build.gradle`

**File**: `atm-locator-java/build.gradle`

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.2'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'jacoco'
}

group = 'com.martianbank'
version = '1.0.0'

java {
    sourceCompatibility = '25'
    targetCompatibility = '25'
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // OpenAPI Documentation
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

    // Lombok (optional but recommended)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core:5.8.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.8.0'
}

jacoco {
    toolVersion = "0.8.11"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.90
            }
        }
    }
}

tasks.named('test') {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

tasks.named('check') {
    dependsOn jacocoTestCoverageVerification
}
```

### Step 4: Create `application.yml`

**File**: `atm-locator-java/src/main/resources/application.yml`

```yaml
server:
  port: ${PORT:8001}

spring:
  application:
    name: atm-locator

  data:
    mongodb:
      uri: ${DB_URL:mongodb://root:example@localhost:27017/}
      database: test
      auto-index-creation: true

  # Support for DATABASE_HOST environment variable (legacy compatibility)
  # If DATABASE_HOST is set, it overrides DB_URL
  config:
    import: optional:classpath:application-local.yml

logging:
  level:
    root: INFO
    com.martianbank.atmlocator: DEBUG
    org.springframework.data.mongodb: DEBUG

# OpenAPI configuration
springdoc:
  api-docs:
    path: /docs.json
  swagger-ui:
    path: /docs

# Profile-specific MongoDB configuration
---
spring:
  config:
    activate:
      on-profile: docker
  data:
    mongodb:
      host: ${DATABASE_HOST:mongo}
      port: 27017
      database: test
```

### Step 5: Create Main Application Class

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/AtmLocatorApplication.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtmLocatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtmLocatorApplication.class, args);
    }
}
```

### Step 6: Create MongoDB Configuration

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/config/MongoConfig.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri:}")
    private String mongoUri;

    @Value("${DATABASE_HOST:}")
    private String databaseHost;

    @Value("${spring.data.mongodb.database:test}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        String connectionString = buildConnectionString();
        logger.info(" --- Connecting to MongoDB for atm-locator microservice --- ");
        logger.info("Connection string: {}", maskPassword(connectionString));

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        MongoClient client = MongoClients.create(settings);
        logger.info(" --- MongoDB Connected --- ");
        return client;
    }

    private String buildConnectionString() {
        // If DATABASE_HOST is set, use local MongoDB format (legacy compatibility)
        if (databaseHost != null && !databaseHost.isEmpty()) {
            logger.info("Connecting to local MongoDB at {} ...", databaseHost);
            return String.format("mongodb://%s:27017/", databaseHost);
        }

        // Otherwise use DB_URL (MongoDB Atlas or full connection string)
        if (mongoUri != null && !mongoUri.isEmpty()) {
            logger.info("Connecting to MongoDB Atlas (Cloud) ...");
            return mongoUri;
        }

        // Default fallback
        return "mongodb://localhost:27017/";
    }

    private String maskPassword(String uri) {
        // Mask password in logs for security
        return uri.replaceAll("://[^:]+:[^@]+@", "://***:***@");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
```

### Step 7: Create CORS Configuration

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/config/CorsConfig.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS configuration to match legacy Node.js behavior:
 * cors({credentials: true, origin: true})
 *
 * This allows all origins with credentials support.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins (origin: true in Express)
        config.addAllowedOriginPattern("*");

        // Allow credentials (credentials: true in Express)
        config.setAllowCredentials(true);

        // Allow all headers
        config.addAllowedHeader("*");

        // Allow all HTTP methods
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

### Step 8: Create Basic Application Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/AtmLocatorApplicationTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration"
})
class AtmLocatorApplicationTest {

    @Test
    void contextLoads() {
        // Verifies application context starts without errors
    }
}
```

## Success Criteria

### Automated Verification

- [x] Project compiles: `cd atm-locator-java && ./gradlew build -x test`
- [x] Dependencies resolve: `./gradlew dependencies`
- [ ] Application starts (with MongoDB available): `./gradlew bootRun`

## Files Created

| File | Purpose |
|------|---------|
| `settings.gradle` | Project name configuration |
| `build.gradle` | Dependencies and build configuration |
| `application.yml` | Application properties |
| `AtmLocatorApplication.java` | Main entry point |
| `MongoConfig.java` | MongoDB connection configuration |
| `CorsConfig.java` | CORS policy matching legacy |
| `AtmLocatorApplicationTest.java` | Context load test |

## Notes

- The MongoDB configuration supports both `DB_URL` (Atlas) and `DATABASE_HOST` (local) environment variables for backward compatibility with the legacy system
- CORS is configured to allow all origins with credentials, matching the Express `cors({credentials: true, origin: true})` configuration
- JaCoCo is configured for 90% coverage target but excluded from this phase's verification
