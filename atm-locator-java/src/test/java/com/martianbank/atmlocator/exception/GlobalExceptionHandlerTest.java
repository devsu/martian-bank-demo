package com.martianbank.atmlocator.exception;

import com.martianbank.atmlocator.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for GlobalExceptionHandler verifying correct HTTP responses.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        ReflectionTestUtils.setField(handler, "includeStackTrace", false);
    }

    @Test
    @DisplayName("handleAtmNotFoundException should return 404 with ErrorResponse")
    void handleAtmNotFoundException_ShouldReturn404WithErrorResponse() {
        AtmNotFoundException exception = new AtmNotFoundException();

        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("No ATMs found");
        assertThat(response.getBody().stack()).isNull();
    }

    @Test
    @DisplayName("handleAtmNotFoundException with ID should return 404 with specific message")
    void handleAtmNotFoundException_WithId_ShouldReturn404WithSpecificMessage() {
        AtmNotFoundException exception = new AtmNotFoundException("abc123");

        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("ATM information not found");
        assertThat(response.getBody().stack()).isNull();
    }

    @Test
    @DisplayName("handleInvalidObjectIdException should return 404 with ErrorResponse")
    void handleInvalidObjectIdException_ShouldReturn404WithErrorResponse() {
        InvalidObjectIdException exception = new InvalidObjectIdException("invalid-id");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidObjectIdException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Resource not found");
        assertThat(response.getBody().stack()).isNull();
    }

    @Test
    @DisplayName("handleHttpMessageNotReadable should return 400 with ErrorResponse")
    void handleHttpMessageNotReadable_ShouldReturn400WithErrorResponse() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Malformed JSON request");
        assertThat(response.getBody().stack()).isNull();
    }

    @Test
    @DisplayName("handleGenericException should return 500 with ErrorResponse")
    void handleGenericException_ShouldReturn500WithErrorResponse() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().stack()).isNull();
    }

    @Test
    @DisplayName("handleGenericException with stack trace enabled should include stack trace")
    void handleGenericException_WithStackTraceEnabled_ShouldIncludeStackTrace() {
        ReflectionTestUtils.setField(handler, "includeStackTrace", true);
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().stack()).isNotNull();
        assertThat(response.getBody().stack()).contains("RuntimeException");
    }

    @Test
    @DisplayName("handleAtmNotFoundException with stack trace enabled should include stack trace")
    void handleAtmNotFoundException_WithStackTraceEnabled_ShouldIncludeStackTrace() {
        ReflectionTestUtils.setField(handler, "includeStackTrace", true);
        AtmNotFoundException exception = new AtmNotFoundException();

        ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("No ATMs found");
        assertThat(response.getBody().stack()).isNotNull();
        assertThat(response.getBody().stack()).contains("AtmNotFoundException");
    }
}
