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
 * Embedded coordinates object matching legacy Mongoose schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {

    private Double latitude;

    private Double longitude;
}
