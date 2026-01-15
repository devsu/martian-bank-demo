/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtmNotFoundExceptionTest {

    @Test
    void constructor_setsMessage() {
        // When
        AtmNotFoundException exception = new AtmNotFoundException("Test message");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Test message");
    }

    @Test
    void constructor_setsMessageAndCause() {
        // Given
        Throwable cause = new RuntimeException("Root cause");

        // When
        AtmNotFoundException exception = new AtmNotFoundException("Test message", cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void exception_isRuntimeException() {
        // When
        AtmNotFoundException exception = new AtmNotFoundException("Test");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
