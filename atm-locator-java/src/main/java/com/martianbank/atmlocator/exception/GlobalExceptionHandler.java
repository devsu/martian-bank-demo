package com.martianbank.atmlocator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler providing RFC 7807 ProblemDetail responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles AtmNotFoundException - returns 404 with plain string body.
     * Per OpenAPI spec, returns a simple string message.
     */
    @ExceptionHandler(AtmNotFoundException.class)
    public ResponseEntity<String> handleAtmNotFoundException(AtmNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles malformed JSON requests - returns 400 with ProblemDetail.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request"
        );
        problemDetail.setTitle("Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Fallback handler for all other exceptions - returns 500 with ProblemDetail.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
