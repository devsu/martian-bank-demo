/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error response DTO matching legacy format.
 *
 * Legacy format (errorMiddleware.js:23-26):
 * {
 *   "message": "Error message",
 *   "stack": null  // or stack trace if not production
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErrorResponse {

    private String message;

    /**
     * Stack trace - null in production, populated in development.
     * Legacy: process.env.NODE_ENV === "production" ? null : err.stack
     */
    private String stack;

    /**
     * Factory method for production errors (no stack trace).
     */
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }

    /**
     * Factory method with stack trace for development.
     */
    public static ErrorResponse withStack(String message, String stack) {
        return new ErrorResponse(message, stack);
    }
}
