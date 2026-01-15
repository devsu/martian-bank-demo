# Phase 4: REST Controllers & Error Handling

## Overview

Implement REST endpoints with exact request/response formats matching the legacy Node.js application, and global exception handling that produces identical error responses.

## Prerequisites

- Phase 3 and 3.5 completed
- Service layer and DTOs in place
- All tests passing

## Deliverables

1. `AtmController.java` - REST controller with all 3 endpoints
2. `GlobalExceptionHandler.java` - Exception handling matching legacy format
3. `ErrorResponse.java` - Error response DTO

## Legacy Reference

**Routes** (`atm-locator/routes/atmRoutes.js:16-18`):
```javascript
router.post("/", getATMs);
router.post("/add", addATM);
router.get("/:id", getSpecificATM);
```

**Error Response Format** (`atm-locator/middleware/errorMiddleware.js:23-26`):
```javascript
res.status(statusCode).json({
  message: message,
  stack: process.env.NODE_ENV === "production" ? null : err.stack,
});
```

**Special Cases**:
- Invalid ObjectId → 404 "Resource not found"
- No ATMs found → 404 "No ATMs found" (plain string)
- ATM by ID not found → 404 `{"message": "ATM information not found"}`

## Implementation Steps

### Step 1: Create Error Response DTO

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/dto/ErrorResponse.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error response DTO matching legacy format.
 *
 * Legacy format (errorMiddleware.js:23-26):
 * {
 *   "message": "Error message",
 *   "stack": null  // or stack trace if not production
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErrorResponse {

    private String message;

    /**
     * Stack trace - null in production, populated in development.
     * Legacy: process.env.NODE_ENV === "production" ? null : err.stack
     */
    private String stack;

    /**
     * Factory method for production errors (no stack trace).
     */
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }

    /**
     * Factory method with stack trace for development.
     */
    public static ErrorResponse withStack(String message, String stack) {
        return new ErrorResponse(message, stack);
    }
}
```

### Step 2: Create Global Exception Handler

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/exception/GlobalExceptionHandler.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.exception;

import com.martianbank.atmlocator.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Global exception handler matching legacy error response format.
 *
 * Legacy behavior (errorMiddleware.js):
 * - 404 for not found errors
 * - CastError with ObjectId kind → 404 "Resource not found"
 * - Stack trace hidden in production
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${NODE_ENV:production}")
    private String nodeEnv;

    /**
     * Handle ATM not found exceptions.
     *
     * Legacy: res.status(404).json({ message: "ATM information not found" })
     */
    @ExceptionHandler(AtmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAtmNotFoundException(AtmNotFoundException ex) {
        logger.debug("ATM not found: {}", ex.getMessage());

        String message = mapErrorMessage(ex.getMessage());
        ErrorResponse response = buildErrorResponse(message, ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle invalid ID format (equivalent to Mongoose CastError).
     *
     * Legacy (errorMiddleware.js:18-21):
     * if (err.name === "CastError" && err.kind === "ObjectId") {
     *   statusCode = 404;
     *   message = "Resource not found";
     * }
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.debug("Invalid argument: {}", ex.getMessage());

        // Check if this is an invalid ObjectId error
        if (isInvalidObjectIdError(ex)) {
            ErrorResponse response = buildErrorResponse("Resource not found", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ErrorResponse response = buildErrorResponse(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle malformed JSON request body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.debug("Malformed request: {}", ex.getMessage());

        ErrorResponse response = buildErrorResponse("Invalid request body", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle type mismatch in path variables.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.debug("Type mismatch: {}", ex.getMessage());

        ErrorResponse response = buildErrorResponse("Resource not found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Catch-all handler for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse response = buildErrorResponse(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Map error messages to match legacy behavior.
     */
    private String mapErrorMessage(String message) {
        // Legacy: "ATM not found" → response shows "ATM information not found"
        if ("ATM not found".equals(message)) {
            return "ATM information not found";
        }
        return message;
    }

    /**
     * Check if the exception is related to invalid MongoDB ObjectId.
     */
    private boolean isInvalidObjectIdError(Exception ex) {
        String message = ex.getMessage();
        return message != null && (
                message.contains("ObjectId") ||
                message.contains("Invalid hexadecimal") ||
                message.contains("invalid hexadecimal")
        );
    }

    /**
     * Build error response with optional stack trace.
     *
     * Legacy: stack trace shown only when NODE_ENV !== "production"
     */
    private ErrorResponse buildErrorResponse(String message, Exception ex) {
        if (isProduction()) {
            return ErrorResponse.of(message);
        } else {
            return ErrorResponse.withStack(message, getStackTrace(ex));
        }
    }

    /**
     * Check if running in production mode.
     */
    private boolean isProduction() {
        return "production".equalsIgnoreCase(nodeEnv);
    }

    /**
     * Convert exception stack trace to string.
     */
    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
```

### Step 3: Create REST Controller

**File**: `atm-locator-java/src/main/java/com/martianbank/atmlocator/controller/AtmController.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.controller;

import com.martianbank.atmlocator.dto.*;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.service.AtmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for ATM operations.
 *
 * Base path: /api/atm (matching legacy routes)
 *
 * Legacy routes (atmRoutes.js:16-18):
 * - POST /          → getATMs
 * - POST /add       → addATM
 * - GET  /:id       → getSpecificATM
 */
@RestController
@RequestMapping("/api/atm")
@Tag(name = "ATM", description = "ATM location operations")
public class AtmController {

    private static final Logger logger = LoggerFactory.getLogger(AtmController.class);

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    /**
     * Get list of ATMs with optional filters.
     *
     * Legacy: POST /api/atm
     * Returns max 4 randomized ATMs matching filters.
     *
     * @param request Filter parameters (isOpenNow, isInterPlanetary)
     * @return List of ATM summaries
     */
    @PostMapping("/")
    @Operation(
            summary = "Get all ATMs",
            description = "Get a list of all ATMs according to the filters"
    )
    @ApiResponse(responseCode = "200", description = "ATM details successfully fetched")
    @ApiResponse(responseCode = "404", description = "No ATMs found")
    public ResponseEntity<?> getATMs(@RequestBody(required = false) AtmFilterRequest request) {
        logger.debug("GET ATMs called with filter: {}", request);

        try {
            List<AtmListResponse> atms = atmService.getATMs(request);
            return ResponseEntity.ok(atms);
        } catch (AtmNotFoundException ex) {
            // Legacy: returns plain string "No ATMs found" for this case
            // res.status(404).json("No ATMs found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No ATMs found");
        }
    }

    /**
     * Add a new ATM.
     *
     * Legacy: POST /api/atm/add
     * Returns the created ATM with all fields including _id.
     *
     * @param request ATM data to create
     * @return Created ATM entity
     */
    @PostMapping("/add")
    @Operation(
            summary = "Add new ATM",
            description = "Create a new ATM record"
    )
    @ApiResponse(responseCode = "201", description = "ATM created successfully")
    @ApiResponse(responseCode = "404", description = "Could not create ATM")
    public ResponseEntity<Atm> addATM(@RequestBody AtmCreateRequest request) {
        logger.debug("ADD ATM called with name: {}", request.getName());

        Atm created = atmService.addATM(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get specific ATM by ID.
     *
     * Legacy: GET /api/atm/:id
     * Returns ATM details (coordinates, timings, hours, numberOfATMs, isOpen).
     *
     * @param id MongoDB ObjectId as string
     * @return ATM detail response
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get ATM by ID",
            description = "Get ATM details by ID"
    )
    @ApiResponse(responseCode = "200", description = "ATM details successfully fetched")
    @ApiResponse(responseCode = "404", description = "ATM not found")
    public ResponseEntity<AtmDetailResponse> getSpecificATM(
            @Parameter(description = "ID of the ATM", required = true)
            @PathVariable String id) {
        logger.debug("GET specific ATM called with id: {}", id);

        AtmDetailResponse atm = atmService.getSpecificATM(id);
        return ResponseEntity.ok(atm);
    }
}
```

### Step 4: Add Trailing Slash Handling

Spring Boot by default doesn't match trailing slashes. To match legacy behavior where `/api/atm/` and `/api/atm` are both valid, add configuration:

**Update**: `atm-locator-java/src/main/resources/application.yml`

Add under the `spring` section:
```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```

Or alternatively, update the controller mapping to handle both:

```java
@PostMapping(value = {"", "/"})
public ResponseEntity<?> getATMs(@RequestBody(required = false) AtmFilterRequest request) {
```

## Directory Structure After Phase 4

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
│   └── AtmService.java
├── controller/
│   └── AtmController.java (new)
├── dto/
│   ├── AtmFilterRequest.java
│   ├── AtmCreateRequest.java
│   ├── AtmListResponse.java
│   ├── AtmDetailResponse.java
│   └── ErrorResponse.java (new)
└── exception/
    ├── AtmNotFoundException.java
    └── GlobalExceptionHandler.java (new)
```

## API Contract Verification

### Endpoint 1: POST /api/atm/

**Request**:
```json
{
  "isOpenNow": true,
  "isInterPlanetary": false
}
```

**Success Response (200)**:
```json
[
  {
    "_id": "64a6f1cc8c1899820dbdf25a",
    "name": "Martian ATM (Highway)",
    "coordinates": {
      "latitude": 37.775,
      "longitude": -81.188
    },
    "address": {
      "street": "14th Street, Martian Way",
      "city": "Musk City",
      "state": "Mars",
      "zip": "40411"
    },
    "isOpen": true
  }
]
```

**Error Response (404)**:
```
"No ATMs found"
```

### Endpoint 2: POST /api/atm/add

**Request**:
```json
{
  "name": "New ATM",
  "street": "123 Main St",
  "city": "New City",
  "state": "State",
  "zip": "12345",
  "latitude": 40.0,
  "longitude": -74.0,
  "monFri": "9-5",
  "satSun": "10-3",
  "holidays": "Closed",
  "atmHours": "24 hours",
  "numberOfATMs": 2,
  "isOpen": true,
  "interPlanetary": false
}
```

**Success Response (201)**:
```json
{
  "_id": "generated-id",
  "name": "New ATM",
  // ... all fields
}
```

### Endpoint 3: GET /api/atm/{id}

**Success Response (200)**:
```json
{
  "coordinates": {
    "latitude": 37.775,
    "longitude": -81.188
  },
  "timings": {
    "monFri": "9:00 AM - 5:00 PM",
    "satSun": "10:00 AM - 3:00 PM",
    "holidays": "Closed on holidays"
  },
  "atmHours": "24 hours",
  "numberOfATMs": 2,
  "isOpen": true
}
```

**Error Response (404)**:
```json
{
  "message": "ATM information not found",
  "stack": null
}
```

## Success Criteria

### Automated Verification

- [ ] Project compiles: `./gradlew build -x test`
- [ ] Controller endpoints match legacy paths
- [ ] Error handler produces correct response format

## Notes

- The `getATMs` endpoint returns a plain string `"No ATMs found"` for 404 (not wrapped in ErrorResponse) to match legacy behavior
- The `getSpecificATM` endpoint returns a structured error response for 404
- The `NODE_ENV` environment variable controls stack trace visibility (matching legacy)
- Trailing slash handling ensures both `/api/atm` and `/api/atm/` work
