/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class AtmLocatorApplicationTest {

    @Test
    void mainClass_exists() {
        // Verify main class can be instantiated
        assertThatNoException().isThrownBy(() -> {
            AtmLocatorApplication app = new AtmLocatorApplication();
        });
    }

    @Test
    void mainMethod_exists() {
        // Verify main method signature is correct
        // This doesn't actually start the app, just verifies the method exists
        assertThatNoException().isThrownBy(() -> {
            AtmLocatorApplication.class.getMethod("main", String[].class);
        });
    }
}
