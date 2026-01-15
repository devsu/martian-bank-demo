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
