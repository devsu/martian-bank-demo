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
