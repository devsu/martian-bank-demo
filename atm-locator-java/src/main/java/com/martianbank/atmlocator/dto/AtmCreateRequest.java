/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new ATM.
 *
 * Legacy request body (atmController.js:42-57):
 * Flat structure with nested object fields unpacked.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmCreateRequest {

    private String name;

    // Address fields (flattened from nested object)
    private String street;
    private String city;
    private String state;
    private String zip;

    // Coordinates fields (flattened from nested object)
    private Double latitude;
    private Double longitude;

    // Timings fields (flattened from nested object)
    private String monFri;
    private String satSun;
    private String holidays;  // Optional

    private String atmHours;
    private Integer numberOfATMs;
    private Boolean isOpen;
    private Boolean interPlanetary;
}
