/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for ATM list endpoint.
 *
 * Legacy projection (atmController.js:23-28):
 * {
 *   name: 1,
 *   coordinates: 1,
 *   address: 1,
 *   isOpen: 1,
 * }
 *
 * Note: _id is included by default in MongoDB projections.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtmListResponse {

    /**
     * MongoDB ObjectId as string.
     * JSON field name "_id" to match legacy response.
     */
    @JsonProperty("_id")
    private String id;

    private String name;

    private Coordinates coordinates;

    private Address address;

    private Boolean isOpen;

    /**
     * Factory method to create response from entity.
     */
    public static AtmListResponse fromEntity(Atm atm) {
        AtmListResponse response = new AtmListResponse();
        response.setId(atm.getId());
        response.setName(atm.getName());
        response.setCoordinates(atm.getCoordinates());
        response.setAddress(atm.getAddress());
        response.setIsOpen(atm.getIsOpen());
        return response;
    }
}
