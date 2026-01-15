/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * ATM entity matching legacy Mongoose schema exactly.
 *
 * Collection name: "atms" (Mongoose pluralizes "ATM" model name)
 *
 * Legacy reference: atm-locator/models/atmModel.js:9-77
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "atms")
public class Atm {

    @Id
    private String id;

    private String name;

    private Address address;

    private Coordinates coordinates;

    private Timings timings;

    private String atmHours;

    private Integer numberOfATMs;

    @Field("isOpen")
    private Boolean isOpen;

    @Field("interPlanetary")
    private Boolean interPlanetary = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * MongoDB version field (equivalent to Mongoose __v)
     * Included for document compatibility but not actively used.
     */
    @Field("__v")
    private Integer version;
}
