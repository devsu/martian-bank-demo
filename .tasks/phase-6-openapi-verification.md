# Phase 6: OpenAPI Documentation & Final Verification

## Overview

Add Springdoc OpenAPI documentation to match the legacy Swagger documentation and perform final verification that all endpoints behave identically to the Node.js version.

## Prerequisites

- Phase 5 and 5.5 completed
- Docker integration working
- All tests passing

## Deliverables

1. OpenAPI configuration class
2. API documentation annotations (already added in Phase 4)
3. Verification that `/docs` endpoint works
4. Final API parity verification report

## Legacy Reference

**Swagger UI location**: `http://atm-locator:8001/docs`
**Swagger JSON**: `http://atm-locator:8001/docs.json`

**Legacy swagger.yaml** (`atm-locator/utils/swagger.yaml`):
```yaml
openapi: 3.0.0
info:
  title: atm-locator
  version: 1.0.0
  description: API documentation for the atm-locator microservice

paths:
  /api/atm/:
    post:
      summary: Get all ATMs
      description: Get a list of all ATMs according to the filters
      tags:
        - ATM
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                isOpenNow:
                  type: boolean
                isInterPlanetary:
                  type: boolean
      responses:
        "200":
          description: ATM details successfully fetched
        "404":
          description: Invalid request

  /api/atm/{id}:
    get:
      summary: Get ATM by ID
      description: Get ATM details by ID
      tags:
        - ATM
      parameters:
        - name: id
          in: path
          description: ID of the ATM
          required: true
          schema:
            type: string
      responses:
        "200":
          description: ATM details successfully fetched
        "404":
          description: Invalid request
```

## Implementation Steps

### Step 1: Create OpenAPI Configuration

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/config/OpenApiConfig.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration matching legacy Swagger setup.
 *
 * Legacy endpoints:
 * - Swagger UI: /docs
 * - OpenAPI JSON: /docs.json
 *
 * Configured via application.yml:
 * springdoc.api-docs.path: /docs.json
 * springdoc.swagger-ui.path: /docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI atmLocatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("atm-locator")
                        .version("1.0.0")
                        .description("API documentation for the atm-locator microservice")
                        .license(new License()
                                .name("BSD-style")
                                .url("https://github.com/cisco-open/martian-bank-demo/blob/main/LICENSE")));
    }
}
```

### Step 2: Verify application.yml OpenAPI Settings

Ensure `application.yml` has these settings (added in Phase 1):

```yaml
springdoc:
  api-docs:
    path: /docs.json
  swagger-ui:
    path: /docs
```

### Step 3: Enhance Controller Annotations

The controller should already have OpenAPI annotations from Phase 4. Verify and enhance if needed:

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/controller/AtmController.java`

Verify these annotations are present:

```java
@Tag(name = "ATM", description = "ATM location operations")

@Operation(summary = "Get all ATMs", description = "Get a list of all ATMs according to the filters")
@ApiResponse(responseCode = "200", description = "ATM details successfully fetched")
@ApiResponse(responseCode = "404", description = "Invalid request")

@Operation(summary = "Add new ATM", description = "Create a new ATM record")

@Operation(summary = "Get ATM by ID", description = "Get ATM details by ID")
@Parameter(description = "ID of the ATM", required = true)
```

### Step 4: Add Schema Annotations to DTOs (Optional Enhancement)

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/dto/AtmFilterRequest.java`

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter parameters for ATM search")
public class AtmFilterRequest {

    @Schema(description = "Filter for currently open ATMs", example = "true")
    private Boolean isOpenNow;

    @Schema(description = "Filter for interplanetary ATMs", example = "false")
    private Boolean isInterPlanetary;
}
```

### Step 5: Create Final Verification Checklist

**File**: `atm-locator-java/VERIFICATION.md`

```markdown
# ATM Locator Java - Migration Verification Checklist

## API Endpoint Parity

### POST /api/atm/
- [ ] Returns JSON array
- [ ] Maximum 4 items returned
- [ ] Items contain: _id, name, coordinates, address, isOpen
- [ ] Items exclude: timings, atmHours, numberOfATMs, interPlanetary
- [ ] Results are randomized
- [ ] Empty filter returns non-interplanetary ATMs
- [ ] isOpenNow=true returns only open ATMs
- [ ] isInterPlanetary=true returns interplanetary ATMs
- [ ] No results returns 404 with "No ATMs found"

### POST /api/atm/add
- [ ] Returns 201 status code
- [ ] Returns full created ATM document
- [ ] Generated _id included
- [ ] Timestamps included (createdAt, updatedAt)
- [ ] Nested objects created correctly (address, coordinates, timings)

### GET /api/atm/{id}
- [ ] Returns 200 for valid ID
- [ ] Returns: coordinates, timings, atmHours, numberOfATMs, isOpen
- [ ] Excludes: _id, name, address, interPlanetary
- [ ] Invalid ID returns 404
- [ ] Error response has message and stack fields

## Error Response Format

### 404 Errors
- [ ] Response body: `{"message": "...", "stack": null}`
- [ ] Stack is null in production mode
- [ ] Stack populated in development mode
- [ ] "ATM not found" mapped to "ATM information not found"

### Invalid ObjectId
- [ ] Returns 404 (not 400)
- [ ] Message: "Resource not found"

## Database Behavior

- [ ] Connects to MongoDB on startup
- [ ] Seeds 13 ATM records from atm_data.json
- [ ] Drops existing collection before seeding
- [ ] Supports DATABASE_HOST environment variable
- [ ] Supports DB_URL environment variable

## Docker Integration

- [ ] Container builds successfully
- [ ] Container starts without errors
- [ ] Health check passes
- [ ] Port 8001 exposed
- [ ] Works with docker-compose
- [ ] NGINX routing works

## OpenAPI Documentation

- [ ] Swagger UI available at /docs
- [ ] OpenAPI JSON at /docs.json
- [ ] All endpoints documented
- [ ] Request/response schemas shown

## CORS Configuration

- [ ] All origins allowed
- [ ] Credentials allowed
- [ ] Preflight requests handled

## Performance

- [ ] Startup time < 30 seconds
- [ ] Response time < 500ms
- [ ] Memory usage < 512MB
```

## API Parity Test Results

Run the following commands to verify API parity:

### Test 1: List ATMs (Default Filter)
```bash
# Java
curl -s -X POST http://localhost:8001/api/atm/ \
  -H "Content-Type: application/json" \
  -d '{}' | jq '.[0] | keys'

# Expected: ["_id", "address", "coordinates", "isOpen", "name"]
```

### Test 2: List ATMs (Open Only)
```bash
curl -s -X POST http://localhost:8001/api/atm/ \
  -H "Content-Type: application/json" \
  -d '{"isOpenNow": true}' | jq 'map(.isOpen) | all'

# Expected: true
```

### Test 3: Get Specific ATM
```bash
# Get an ID first
ID=$(curl -s -X POST http://localhost:8001/api/atm/ \
  -H "Content-Type: application/json" \
  -d '{}' | jq -r '.[0]._id')

# Get details
curl -s "http://localhost:8001/api/atm/$ID" | jq 'keys'

# Expected: ["atmHours", "coordinates", "isOpen", "numberOfATMs", "timings"]
```

### Test 4: Error Response
```bash
curl -s "http://localhost:8001/api/atm/invalid-id" | jq 'keys'

# Expected: ["message", "stack"]
```

### Test 5: OpenAPI
```bash
curl -s http://localhost:8001/docs.json | jq '.info.title'

# Expected: "atm-locator"
```

## Success Criteria

### Automated Verification

- [x] OpenAPI JSON accessible: `curl http://localhost:8001/docs.json`
- [x] Swagger UI loads: `curl http://localhost:8001/docs`
- [x] All verification tests pass

## Directory Structure After Phase 6

```
atm-locator-java/src/main/java/com/martianbank/atmlocator/
├── AtmLocatorApplication.java
├── config/
│   ├── MongoConfig.java
│   ├── CorsConfig.java
│   ├── DataSeeder.java
│   └── OpenApiConfig.java (new/verified)
├── model/
│   └── ...
├── repository/
│   └── ...
├── service/
│   └── ...
├── controller/
│   └── AtmController.java (annotations verified)
├── dto/
│   └── ... (schema annotations added)
└── exception/
    └── ...

atm-locator-java/
├── VERIFICATION.md (new)
└── ...
```

## Notes

- The Springdoc OpenAPI automatically generates documentation from controller annotations
- The legacy Swagger setup used a separate YAML file; the Java version generates it automatically
- `/docs` path matches legacy for backward compatibility
- Schema annotations on DTOs are optional but improve documentation quality
