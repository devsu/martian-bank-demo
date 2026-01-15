/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtmFilterRequestTest {

    @Test
    void noArgsConstructor_createsEmptyRequest() {
        AtmFilterRequest request = new AtmFilterRequest();
        assertThat(request.getIsOpenNow()).isNull();
        assertThat(request.getIsInterPlanetary()).isNull();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        AtmFilterRequest request = new AtmFilterRequest(true, false);
        assertThat(request.getIsOpenNow()).isTrue();
        assertThat(request.getIsInterPlanetary()).isFalse();
    }

    @Test
    void setters_setCorrectValues() {
        AtmFilterRequest request = new AtmFilterRequest();
        request.setIsOpenNow(true);
        request.setIsInterPlanetary(true);

        assertThat(request.getIsOpenNow()).isTrue();
        assertThat(request.getIsInterPlanetary()).isTrue();
    }
}
