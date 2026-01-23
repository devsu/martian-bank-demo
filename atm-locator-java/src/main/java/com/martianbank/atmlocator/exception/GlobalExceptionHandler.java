package com.martianbank.atmlocator.exception;

import com.martianbank.atmlocator.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Global exception handler providing standardized ErrorResponse responses.
 * Stack traces are optionally included based on configuration (for non-production environments).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.error.include-stack-trace:false}")
    private boolean includeStackTrace;

    /**
     * Handles AtmNotFoundException - returns 404 NOT_FOUND.
     * Used when an ATM with a specific ID cannot be found or no ATMs match the search criteria.
     */
    @ExceptionHandler(AtmNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAtmNotFoundException(AtmNotFoundException ex) {
        ErrorResponse errorResponse = createErrorResponse(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles InvalidObjectIdException - returns 404 NOT_FOUND.
     * Used when an invalid MongoDB ObjectId format is provided.
     */
    @ExceptionHandler(InvalidObjectIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidObjectIdException(InvalidObjectIdException ex) {
        ErrorResponse errorResponse = createErrorResponse(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles malformed JSON requests - returns 400 BAD_REQUEST.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = createErrorResponse("Malformed JSON request", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Fallback handler for all other exceptions - returns 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = createErrorResponse("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Creates an ErrorResponse from an exception, using the exception's message.
     */
    private ErrorResponse createErrorResponse(Exception ex) {
        return createErrorResponse(ex.getMessage(), ex);
    }

    /**
     * Creates an ErrorResponse with a custom message and optional stack trace.
     */
    private ErrorResponse createErrorResponse(String message, Exception ex) {
        String stack = null;
        if (includeStackTrace) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            stack = sw.toString();
        }
        return ErrorResponse.of(message, stack);
    }
}
