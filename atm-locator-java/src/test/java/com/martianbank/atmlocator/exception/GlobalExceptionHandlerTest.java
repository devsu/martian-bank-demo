package com.martianbank.atmlocator.exception;

import com.martianbank.atmlocator.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("AtmNotFoundException handling")
    class AtmNotFoundExceptionTests {

        @Test
        @DisplayName("should return 404 NOT_FOUND with default message when no ATMs found")
        void shouldReturn404WithDefaultMessageWhenNoAtmsFound() {
            // Arrange
            AtmNotFoundException exception = new AtmNotFoundException();

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("No ATMs found");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should return 404 NOT_FOUND with specific message when ATM ID not found")
        void shouldReturn404WithSpecificMessageWhenAtmIdNotFound() {
            // Arrange
            AtmNotFoundException exception = new AtmNotFoundException("abc123");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("ATM information not found");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should include stack trace when stack trace is enabled")
        void shouldIncludeStackTraceWhenEnabled() {
            // Arrange
            ReflectionTestUtils.setField(handler, "includeStackTrace", true);
            AtmNotFoundException exception = new AtmNotFoundException();

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("No ATMs found");
            assertThat(response.getBody().stack()).isNotNull();
            assertThat(response.getBody().stack()).contains("AtmNotFoundException");
        }
    }

    @Nested
    @DisplayName("InvalidObjectIdException handling")
    class InvalidObjectIdExceptionTests {

        @Test
        @DisplayName("should return 404 NOT_FOUND with 'Resource not found' message")
        void shouldReturn404WithResourceNotFoundMessage() {
            // Arrange
            InvalidObjectIdException exception = new InvalidObjectIdException("invalid-id");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleInvalidObjectIdException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Resource not found");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should include stack trace when stack trace is enabled")
        void shouldIncludeStackTraceWhenEnabled() {
            // Arrange
            ReflectionTestUtils.setField(handler, "includeStackTrace", true);
            InvalidObjectIdException exception = new InvalidObjectIdException("invalid-id");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleInvalidObjectIdException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Resource not found");
            assertThat(response.getBody().stack()).isNotNull();
            assertThat(response.getBody().stack()).contains("InvalidObjectIdException");
        }
    }

    @Nested
    @DisplayName("HttpMessageNotReadableException handling")
    class HttpMessageNotReadableExceptionTests {

        @Test
        @DisplayName("should return 400 BAD_REQUEST with 'Malformed JSON request' message")
        void shouldReturn400WithMalformedJsonMessage() {
            // Arrange
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Malformed JSON request");
            assertThat(response.getBody().stack()).isNull();
        }
    }

    @Nested
    @DisplayName("Generic Exception handling")
    class GenericExceptionTests {

        @Test
        @DisplayName("should return 500 INTERNAL_SERVER_ERROR with generic message")
        void shouldReturn500WithGenericMessage() {
            // Arrange
            Exception exception = new RuntimeException("Unexpected error");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should include stack trace when stack trace is enabled")
        void shouldIncludeStackTraceWhenEnabled() {
            // Arrange
            ReflectionTestUtils.setField(handler, "includeStackTrace", true);
            Exception exception = new RuntimeException("Unexpected error");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
            assertThat(response.getBody().stack()).isNotNull();
            assertThat(response.getBody().stack()).contains("RuntimeException");
        }

        @Test
        @DisplayName("should handle NullPointerException as generic exception")
        void shouldHandleNullPointerExceptionAsGenericException() {
            // Arrange
            Exception exception = new NullPointerException("null reference");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        }

        @Test
        @DisplayName("should handle IllegalStateException as generic exception")
        void shouldHandleIllegalStateExceptionAsGenericException() {
            // Arrange
            Exception exception = new IllegalStateException("invalid state");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        }
    }

    @Nested
    @DisplayName("ErrorResponse format verification")
    class ErrorResponseFormatTests {

        @Test
        @DisplayName("should return ErrorResponse with message and null stack when stack trace disabled")
        void shouldReturnErrorResponseWithNullStackWhenDisabled() {
            // Arrange
            AtmNotFoundException exception = new AtmNotFoundException();

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleAtmNotFoundException(exception);

            // Assert
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
            assertThat(response.getBody().message()).isNotNull().isNotEmpty();
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should return ErrorResponse with message and stack when stack trace enabled")
        void shouldReturnErrorResponseWithStackWhenEnabled() {
            // Arrange
            ReflectionTestUtils.setField(handler, "includeStackTrace", true);
            Exception exception = new RuntimeException("test error");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

            // Assert
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
            assertThat(response.getBody().message()).isNotNull().isNotEmpty();
            assertThat(response.getBody().stack()).isNotNull().isNotEmpty();
        }
    }
}
