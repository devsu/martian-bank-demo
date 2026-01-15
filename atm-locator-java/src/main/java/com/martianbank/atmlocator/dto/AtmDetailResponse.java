/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for specific ATM endpoint.
 *
 * Legacy response (atmController.js:96-102):
 * {
 *   coordinates: atm.coordinates,
 *   timings: atm.timings,
 *   atmHours: atm.atmHours,
 *   numberOfATMs: atm.numberOfATMs,
 *   isOpen: atm.isOpen,
 * }
 *
 * Note: Excludes _id, name, address, interPlanetary, timestamps
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmDetailResponse {

    private Coordinates coordinates;

    private Timings timings;

    private String atmHours;

    private Integer numberOfATMs;

    private Boolean isOpen;

    /**
     * Factory method to create response from entity.
     */
    public static AtmDetailResponse fromEntity(Atm atm) {
        AtmDetailResponse response = new AtmDetailResponse();
        response.setCoordinates(atm.getCoordinates());
        response.setTimings(atm.getTimings());
        response.setAtmHours(atm.getAtmHours());
        response.setNumberOfATMs(atm.getNumberOfATMs());
        response.setIsOpen(atm.getIsOpen());
        return response;
    }
}
