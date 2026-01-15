/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.exception;

/**
 * Exception thrown when an ATM is not found.
 *
 * Maps to 404 status code in controller layer.
 */
public class AtmNotFoundException extends RuntimeException {

    public AtmNotFoundException(String message) {
        super(message);
    }

    public AtmNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
