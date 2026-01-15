/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded timings object matching legacy Mongoose schema.
 * holidays field is optional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timings {

    private String monFri;

    private String satSun;

    private String holidays;  // Optional in legacy schema
}
