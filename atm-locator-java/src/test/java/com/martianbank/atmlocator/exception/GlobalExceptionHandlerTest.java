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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void handleHttpMessageNotReadable_returns400() {
        // Given
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Malformed JSON");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid request body");
    }

    @Test
    void handleTypeMismatch_returns404() {
        // Given
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getMessage()).thenReturn("Type mismatch");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Resource not found");
    }
}
