package com.martianbank.atmlocator.exception;

import com.martianbank.atmlocator.dto.ErrorResponse;
import com.martianbank.atmlocator.dto.ValidationErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("MethodArgumentNotValidException handling")
    class MethodArgumentNotValidExceptionTests {

        @Test
        @DisplayName("should return 400 BAD_REQUEST with 'Validation failed' message and field errors")
        void shouldReturn400WithValidationFailedAndFieldErrors() {
            // Arrange
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("atmCreateRequest", "name", "Name is required");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            // Act
            ResponseEntity<ValidationErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Validation failed");
            assertThat(response.getBody().errors()).containsEntry("name", "Name is required");
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST with multiple field errors")
        void shouldReturn400WithMultipleFieldErrors() {
            // Arrange
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError nameError = new FieldError("atmCreateRequest", "name", "Name is required");
            FieldError latitudeError = new FieldError("atmCreateRequest", "location.coordinates.latitude", "Latitude must be between -90 and 90");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(nameError, latitudeError));

            // Act
            ResponseEntity<ValidationErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Validation failed");
            assertThat(response.getBody().errors()).hasSize(2);
            assertThat(response.getBody().errors()).containsEntry("name", "Name is required");
            assertThat(response.getBody().errors()).containsEntry("location.coordinates.latitude", "Latitude must be between -90 and 90");
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST with nested field errors using dot notation")
        void shouldReturn400WithNestedFieldErrorsUsingDotNotation() {
            // Arrange
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError nestedError = new FieldError("atmCreateRequest", "location.address.city", "City is required");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(nestedError));

            // Act
            ResponseEntity<ValidationErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Validation failed");
            assertThat(response.getBody().errors()).containsEntry("location.address.city", "City is required");
        }

        @Test
        @DisplayName("should return 400 BAD_REQUEST with empty errors map when no field errors")
        void shouldReturn400WithEmptyErrorsMapWhenNoFieldErrors() {
            // Arrange
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of());

            // Act
            ResponseEntity<ValidationErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Validation failed");
            assertThat(response.getBody().errors()).isEmpty();
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
    @DisplayName("DuplicateAtmException handling")
    class DuplicateAtmExceptionTests {

        @Test
        @DisplayName("should return 409 CONFLICT with default message when using default constructor")
        void shouldReturn409WithDefaultMessage() {
            // Arrange
            DuplicateAtmException exception = new DuplicateAtmException();

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleDuplicateAtmException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An ATM already exists at this location");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should return 409 CONFLICT with coordinates message when using coordinates constructor")
        void shouldReturn409WithCoordinatesMessage() {
            // Arrange
            DuplicateAtmException exception = new DuplicateAtmException(37.7749, -122.4194);

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleDuplicateAtmException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An ATM already exists at coordinates (37.774900, -122.419400)");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should return 409 CONFLICT with custom message when using custom message constructor")
        void shouldReturn409WithCustomMessage() {
            // Arrange
            DuplicateAtmException exception = new DuplicateAtmException("Custom duplicate message");

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleDuplicateAtmException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Custom duplicate message");
            assertThat(response.getBody().stack()).isNull();
        }

        @Test
        @DisplayName("should include stack trace when stack trace is enabled")
        void shouldIncludeStackTraceWhenEnabled() {
            // Arrange
            ReflectionTestUtils.setField(handler, "includeStackTrace", true);
            DuplicateAtmException exception = new DuplicateAtmException();

            // Act
            ResponseEntity<ErrorResponse> response = handler.handleDuplicateAtmException(exception);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("An ATM already exists at this location");
            assertThat(response.getBody().stack()).isNotNull();
            assertThat(response.getBody().stack()).contains("DuplicateAtmException");
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
