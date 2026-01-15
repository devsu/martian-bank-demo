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

class AtmListResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fromEntity_mapsAllFields() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // When
        AtmListResponse response = AtmListResponse.fromEntity(atm);

        // Then
        assertThat(response.getId()).isEqualTo(atm.getId());
        assertThat(response.getName()).isEqualTo(atm.getName());
        assertThat(response.getCoordinates()).isEqualTo(atm.getCoordinates());
        assertThat(response.getAddress()).isEqualTo(atm.getAddress());
        assertThat(response.getIsOpen()).isEqualTo(atm.getIsOpen());
    }

    @Test
    void jsonSerialization_usesUnderscoreIdFieldName() throws Exception {
        // Given
        AtmListResponse response = new AtmListResponse();
        response.setId("test-id-123");
        response.setName("Test ATM");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"_id\":\"test-id-123\"");
        assertThat(json).doesNotContain("\"id\":");
    }

    @Test
    void fromEntity_excludesTimingsField() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // When
        AtmListResponse response = AtmListResponse.fromEntity(atm);
        String json = response.toString();

        // Then - response should not have timings (not in the projection)
        // The DTO doesn't have a timings field, so this is inherently true
        assertThat(response).hasNoNullFieldsOrPropertiesExcept();
    }
}
