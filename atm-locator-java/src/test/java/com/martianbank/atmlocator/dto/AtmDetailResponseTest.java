/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtmDetailResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fromEntity_mapsAllFields() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // When
        AtmDetailResponse response = AtmDetailResponse.fromEntity(atm);

        // Then
        assertThat(response.getCoordinates()).isEqualTo(atm.getCoordinates());
        assertThat(response.getTimings()).isEqualTo(atm.getTimings());
        assertThat(response.getAtmHours()).isEqualTo(atm.getAtmHours());
        assertThat(response.getNumberOfATMs()).isEqualTo(atm.getNumberOfATMs());
        assertThat(response.getIsOpen()).isEqualTo(atm.getIsOpen());
    }

    @Test
    void fromEntity_excludesIdNameAddress() throws Exception {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // When
        AtmDetailResponse response = AtmDetailResponse.fromEntity(atm);
        String json = objectMapper.writeValueAsString(response);

        // Then - these fields should not be in the response
        assertThat(json).doesNotContain("\"_id\"");
        assertThat(json).doesNotContain("\"name\"");
        assertThat(json).doesNotContain("\"address\"");
        assertThat(json).doesNotContain("\"interPlanetary\"");
    }

    @Test
    void jsonSerialization_includesNestedObjects() throws Exception {
        // Given
        AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"coordinates\"");
        assertThat(json).contains("\"timings\"");
        assertThat(json).contains("\"latitude\"");
        assertThat(json).contains("\"monFri\"");
    }
}
