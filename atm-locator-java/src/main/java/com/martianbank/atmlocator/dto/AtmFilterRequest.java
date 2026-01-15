/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for filtering ATMs.
 *
 * Legacy request body (atmController.js:17-22):
 * {
 *   "isOpenNow": boolean,      // Optional
 *   "isInterPlanetary": boolean // Optional
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter parameters for ATM search")
public class AtmFilterRequest {

    /**
     * Filter for currently open ATMs.
     * If true, adds isOpen: true to query.
     */
    @Schema(description = "Filter for currently open ATMs", example = "true")
    private Boolean isOpenNow;

    /**
     * Filter for interplanetary ATMs.
     * If true, sets interPlanetary: true (overrides default false).
     */
    @Schema(description = "Filter for interplanetary ATMs", example = "false")
    private Boolean isInterPlanetary;
}
