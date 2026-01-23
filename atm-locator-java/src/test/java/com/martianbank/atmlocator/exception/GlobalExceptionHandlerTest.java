package com.martianbank.atmlocator.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

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
    }

    @Test
    @DisplayName("handleAtmNotFoundException should return 404 with message body")
    void handleAtmNotFoundException_ShouldReturn404WithMessage() {
        AtmNotFoundException exception = new AtmNotFoundException();

        ResponseEntity<String> response = handler.handleAtmNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("No ATMs found");
    }

    @Test
    @DisplayName("handleHttpMessageNotReadable should return 400 with ProblemDetail")
    void handleHttpMessageNotReadable_ShouldReturn400WithProblemDetail() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ProblemDetail> response = handler.handleHttpMessageNotReadable(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
        assertThat(response.getBody().getDetail()).isEqualTo("Malformed JSON request");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("handleGenericException should return 500 with ProblemDetail")
    void handleGenericException_ShouldReturn500WithProblemDetail() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ProblemDetail> response = handler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
