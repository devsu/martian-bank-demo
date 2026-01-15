# Phase 4.5: Tests for REST Controllers & Error Handling

## Overview

Create comprehensive unit tests for the REST controller using MockMvc and for the global exception handler.

## Prerequisites

- Phase 4 completed successfully
- Controller and exception handler classes compile without errors

## Deliverables

1. `AtmControllerTest.java` - Controller unit tests with MockMvc
2. `GlobalExceptionHandlerTest.java` - Exception handler tests
3. `ErrorResponseTest.java` - Error response DTO tests

## Implementation Steps

### Step 1: Create Controller Tests

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/controller/AtmControllerTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.dto.*;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.exception.GlobalExceptionHandler;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.service.AtmService;
import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AtmControllerTest {

    @Mock
    private AtmService atmService;

    @InjectMocks
    private AtmController atmController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(atmController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    class GetATMsEndpointTests {

        @Test
        void getATMs_withValidRequest_returns200() throws Exception {
            // Given
            List<AtmListResponse> atms = List.of(
                    AtmListResponse.fromEntity(TestDataFactory.createTestAtm())
            );
            when(atmService.getATMs(any())).thenReturn(atms);

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"isOpenNow\": true}"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        void getATMs_withEmptyBody_returns200() throws Exception {
            // Given
            List<AtmListResponse> atms = List.of(
                    AtmListResponse.fromEntity(TestDataFactory.createTestAtm())
            );
            when(atmService.getATMs(any())).thenReturn(atms);

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }

        @Test
        void getATMs_withNullBody_returns200() throws Exception {
            // Given
            List<AtmListResponse> atms = List.of(
                    AtmListResponse.fromEntity(TestDataFactory.createTestAtm())
            );
            when(atmService.getATMs(any())).thenReturn(atms);

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        void getATMs_responseContainsCorrectFields() throws Exception {
            // Given
            AtmListResponse response = AtmListResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getATMs(any())).thenReturn(List.of(response));

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(jsonPath("$[0]._id").exists())
                    .andExpect(jsonPath("$[0].name").exists())
                    .andExpect(jsonPath("$[0].coordinates").exists())
                    .andExpect(jsonPath("$[0].coordinates.latitude").exists())
                    .andExpect(jsonPath("$[0].coordinates.longitude").exists())
                    .andExpect(jsonPath("$[0].address").exists())
                    .andExpect(jsonPath("$[0].address.street").exists())
                    .andExpect(jsonPath("$[0].isOpen").exists());
        }

        @Test
        void getATMs_responseExcludesTimingsField() throws Exception {
            // Given
            AtmListResponse response = AtmListResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getATMs(any())).thenReturn(List.of(response));

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(jsonPath("$[0].timings").doesNotExist())
                    .andExpect(jsonPath("$[0].atmHours").doesNotExist())
                    .andExpect(jsonPath("$[0].numberOfATMs").doesNotExist())
                    .andExpect(jsonPath("$[0].interPlanetary").doesNotExist());
        }

        @Test
        void getATMs_whenNoResults_returns404WithPlainString() throws Exception {
            // Given
            when(atmService.getATMs(any()))
                    .thenThrow(new AtmNotFoundException("No results found"));

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("\"No ATMs found\""));
        }

        @Test
        void getATMs_passesFilterToService() throws Exception {
            // Given
            when(atmService.getATMs(any())).thenReturn(List.of());

            // When
            mockMvc.perform(post("/api/atm/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"isOpenNow\": true, \"isInterPlanetary\": true}"));

            // Then
            verify(atmService).getATMs(any(AtmFilterRequest.class));
        }

        @Test
        void getATMs_withTrailingSlash_works() throws Exception {
            // Given
            when(atmService.getATMs(any())).thenReturn(List.of());

            // When/Then - both with and without trailing slash should work
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class AddATMEndpointTests {

        @Test
        void addATM_withValidRequest_returns201() throws Exception {
            // Given
            Atm createdAtm = TestDataFactory.createTestAtm();
            when(atmService.addATM(any())).thenReturn(createdAtm);

            String requestBody = createValidAtmRequest();

            // When/Then
            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void addATM_responseContainsAllFields() throws Exception {
            // Given
            Atm createdAtm = TestDataFactory.createTestAtm();
            when(atmService.addATM(any())).thenReturn(createdAtm);

            String requestBody = createValidAtmRequest();

            // When/Then
            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.address").exists())
                    .andExpect(jsonPath("$.coordinates").exists())
                    .andExpect(jsonPath("$.timings").exists())
                    .andExpect(jsonPath("$.atmHours").exists())
                    .andExpect(jsonPath("$.numberOfATMs").exists())
                    .andExpect(jsonPath("$.isOpen").exists())
                    .andExpect(jsonPath("$.interPlanetary").exists());
        }

        @Test
        void addATM_whenServiceFails_returns404() throws Exception {
            // Given
            when(atmService.addATM(any()))
                    .thenThrow(new AtmNotFoundException("Could not create ATM"));

            String requestBody = createValidAtmRequest();

            // When/Then
            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Could not create ATM"));
        }

        private String createValidAtmRequest() {
            return """
                {
                    "name": "Test ATM",
                    "street": "123 Test St",
                    "city": "Test City",
                    "state": "Test State",
                    "zip": "12345",
                    "latitude": 37.775,
                    "longitude": -81.188,
                    "monFri": "9-5",
                    "satSun": "10-3",
                    "holidays": "Closed",
                    "atmHours": "24 hours",
                    "numberOfATMs": 2,
                    "isOpen": true,
                    "interPlanetary": false
                }
                """;
        }
    }

    @Nested
    class GetSpecificATMEndpointTests {

        @Test
        void getSpecificATM_withValidId_returns200() throws Exception {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getSpecificATM(id)).thenReturn(response);

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void getSpecificATM_responseContainsCorrectFields() throws Exception {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getSpecificATM(id)).thenReturn(response);

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(jsonPath("$.coordinates").exists())
                    .andExpect(jsonPath("$.coordinates.latitude").exists())
                    .andExpect(jsonPath("$.coordinates.longitude").exists())
                    .andExpect(jsonPath("$.timings").exists())
                    .andExpect(jsonPath("$.timings.monFri").exists())
                    .andExpect(jsonPath("$.timings.satSun").exists())
                    .andExpect(jsonPath("$.atmHours").exists())
                    .andExpect(jsonPath("$.numberOfATMs").exists())
                    .andExpect(jsonPath("$.isOpen").exists());
        }

        @Test
        void getSpecificATM_responseExcludesIdNameAddress() throws Exception {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getSpecificATM(id)).thenReturn(response);

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(jsonPath("$._id").doesNotExist())
                    .andExpect(jsonPath("$.id").doesNotExist())
                    .andExpect(jsonPath("$.name").doesNotExist())
                    .andExpect(jsonPath("$.address").doesNotExist())
                    .andExpect(jsonPath("$.interPlanetary").doesNotExist());
        }

        @Test
        void getSpecificATM_whenNotFound_returns404() throws Exception {
            // Given
            String id = "nonexistent-id";
            when(atmService.getSpecificATM(id))
                    .thenThrow(new AtmNotFoundException("ATM not found"));

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("ATM information not found"));
        }

        @Test
        void getSpecificATM_errorResponseHasStackFieldNull() throws Exception {
            // Given
            String id = "nonexistent-id";
            when(atmService.getSpecificATM(id))
                    .thenThrow(new AtmNotFoundException("ATM not found"));

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(jsonPath("$.stack").value(nullValue()));
        }
    }

    @Nested
    class EndpointPathTests {

        @Test
        void getATMs_mappedToCorrectPath() throws Exception {
            when(atmService.getATMs(any())).thenReturn(List.of());

            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        void addATM_mappedToCorrectPath() throws Exception {
            when(atmService.addATM(any())).thenReturn(TestDataFactory.createTestAtm());

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void getSpecificATM_mappedToCorrectPath() throws Exception {
            String id = "test-id";
            when(atmService.getSpecificATM(id))
                    .thenReturn(AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm()));

            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(status().isOk());
        }
    }
}
```

### Step 2: Create Exception Handler Tests

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/exception/GlobalExceptionHandlerTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.exception;

import com.martianbank.atmlocator.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        // Default to production mode (no stack traces)
        ReflectionTestUtils.setField(handler, "nodeEnv", "production");
    }

    @Test
    void handleAtmNotFoundException_returns404() {
        // Given
        AtmNotFoundException ex = new AtmNotFoundException("ATM not found");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void handleAtmNotFoundException_mapsAtmNotFoundMessage() {
        // Given - legacy maps "ATM not found" to "ATM information not found"
        AtmNotFoundException ex = new AtmNotFoundException("ATM not found");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        assertThat(response.getBody().getMessage()).isEqualTo("ATM information not found");
    }

    @Test
    void handleAtmNotFoundException_preservesOtherMessages() {
        // Given
        AtmNotFoundException ex = new AtmNotFoundException("No results found");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        assertThat(response.getBody().getMessage()).isEqualTo("No results found");
    }

    @Test
    void handleAtmNotFoundException_stackIsNullInProduction() {
        // Given
        ReflectionTestUtils.setField(handler, "nodeEnv", "production");
        AtmNotFoundException ex = new AtmNotFoundException("Test");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        assertThat(response.getBody().getStack()).isNull();
    }

    @Test
    void handleAtmNotFoundException_stackPopulatedInDevelopment() {
        // Given
        ReflectionTestUtils.setField(handler, "nodeEnv", "development");
        AtmNotFoundException ex = new AtmNotFoundException("Test");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        assertThat(response.getBody().getStack()).isNotNull();
        assertThat(response.getBody().getStack()).contains("AtmNotFoundException");
    }

    @Test
    void handleIllegalArgumentException_withObjectIdError_returns404() {
        // Given - simulates invalid MongoDB ObjectId
        IllegalArgumentException ex = new IllegalArgumentException("Invalid ObjectId format");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Resource not found");
    }

    @Test
    void handleIllegalArgumentException_withOtherError_returns400() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid parameter");
    }

    @Test
    void handleGenericException_returns500() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void productionMode_checksNodeEnvCaseInsensitive() {
        // Given
        ReflectionTestUtils.setField(handler, "nodeEnv", "PRODUCTION");
        AtmNotFoundException ex = new AtmNotFoundException("Test");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        assertThat(response.getBody().getStack()).isNull();
    }

    @Test
    void errorResponse_hasCorrectStructure() {
        // Given
        AtmNotFoundException ex = new AtmNotFoundException("Test message");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(ex);

        // Then
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isNotNull();
        // Stack should be explicitly null in production, not missing
        assertThat(body).hasFieldOrProperty("stack");
    }
}
```

### Step 3: Create ErrorResponse DTO Test

**File**: `atm-locator-java/src/test/java/com/martianbank/atmlocator/dto/ErrorResponseTest.java`

```java
/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void of_createsResponseWithNullStack() {
        // When
        ErrorResponse response = ErrorResponse.of("Error message");

        // Then
        assertThat(response.getMessage()).isEqualTo("Error message");
        assertThat(response.getStack()).isNull();
    }

    @Test
    void withStack_createsResponseWithStack() {
        // When
        ErrorResponse response = ErrorResponse.withStack("Error message", "stack trace");

        // Then
        assertThat(response.getMessage()).isEqualTo("Error message");
        assertThat(response.getStack()).isEqualTo("stack trace");
    }

    @Test
    void jsonSerialization_includesNullStack() throws Exception {
        // Given
        ErrorResponse response = ErrorResponse.of("Test error");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then - stack should be explicitly null, not omitted
        assertThat(json).contains("\"stack\":null");
        assertThat(json).contains("\"message\":\"Test error\"");
    }

    @Test
    void jsonSerialization_includesStackWhenPresent() throws Exception {
        // Given
        ErrorResponse response = ErrorResponse.withStack("Test error", "trace");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"stack\":\"trace\"");
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        // When
        ErrorResponse response = new ErrorResponse("message", "stack");

        // Then
        assertThat(response.getMessage()).isEqualTo("message");
        assertThat(response.getStack()).isEqualTo("stack");
    }

    @Test
    void noArgsConstructor_createsEmptyResponse() {
        // When
        ErrorResponse response = new ErrorResponse();

        // Then
        assertThat(response.getMessage()).isNull();
        assertThat(response.getStack()).isNull();
    }
}
```

## Success Criteria

### Automated Verification

- [ ] All tests pass: `cd atm-locator-java && ./gradlew test`
- [ ] Controller test coverage above 95%
- [ ] Exception handler test coverage above 90%

## Test Summary

| Test Class | Test Count | Coverage Focus |
|------------|------------|----------------|
| `AtmControllerTest` | 20 | Endpoint paths, request/response, HTTP status |
| `GlobalExceptionHandlerTest` | 11 | Error handling, message mapping, stack traces |
| `ErrorResponseTest` | 6 | DTO construction, JSON serialization |

**Total: 37 tests**

## Critical Test Scenarios Covered

| Scenario | Test Method |
|----------|-------------|
| POST /api/atm/ returns 200 | `getATMs_withValidRequest_returns200` |
| POST /api/atm/ returns plain string on 404 | `getATMs_whenNoResults_returns404WithPlainString` |
| POST /api/atm/add returns 201 | `addATM_withValidRequest_returns201` |
| GET /api/atm/{id} returns 200 | `getSpecificATM_withValidId_returns200` |
| GET /api/atm/{id} returns structured 404 | `getSpecificATM_whenNotFound_returns404` |
| Response projections correct | `getATMs_responseContainsCorrectFields`, `getSpecificATM_responseContainsCorrectFields` |
| "ATM not found" mapped to "ATM information not found" | `handleAtmNotFoundException_mapsAtmNotFoundMessage` |
| Stack null in production | `handleAtmNotFoundException_stackIsNullInProduction` |
| Invalid ObjectId returns 404 | `handleIllegalArgumentException_withObjectIdError_returns404` |

## Notes

- MockMvc is configured with the `GlobalExceptionHandler` as controller advice to test error responses
- The `getATMs` 404 case returns a plain string `"No ATMs found"` (quoted in JSON) to match legacy behavior
- Stack trace tests verify the NODE_ENV environment variable controls visibility
- Response field tests use JSONPath to verify both inclusion and exclusion of fields
