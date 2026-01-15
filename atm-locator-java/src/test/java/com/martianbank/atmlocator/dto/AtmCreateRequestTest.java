/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AtmCreateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void noArgsConstructor_createsEmptyRequest() {
        AtmCreateRequest request = new AtmCreateRequest();
        assertThat(request.getName()).isNull();
        assertThat(request.getStreet()).isNull();
        assertThat(request.getCity()).isNull();
        assertThat(request.getState()).isNull();
        assertThat(request.getZip()).isNull();
        assertThat(request.getLatitude()).isNull();
        assertThat(request.getLongitude()).isNull();
        assertThat(request.getMonFri()).isNull();
        assertThat(request.getSatSun()).isNull();
        assertThat(request.getHolidays()).isNull();
        assertThat(request.getAtmHours()).isNull();
        assertThat(request.getNumberOfATMs()).isNull();
        assertThat(request.getIsOpen()).isNull();
        assertThat(request.getInterPlanetary()).isNull();
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        AtmCreateRequest request = new AtmCreateRequest(
                "Test ATM",
                "123 Test St",
                "Test City",
                "Test State",
                "12345",
                37.775,
                -81.188,
                "9-5",
                "10-3",
                "Closed",
                "24 hours",
                2,
                true,
                false
        );

        assertThat(request.getName()).isEqualTo("Test ATM");
        assertThat(request.getStreet()).isEqualTo("123 Test St");
        assertThat(request.getCity()).isEqualTo("Test City");
        assertThat(request.getState()).isEqualTo("Test State");
        assertThat(request.getZip()).isEqualTo("12345");
        assertThat(request.getLatitude()).isEqualTo(37.775);
        assertThat(request.getLongitude()).isEqualTo(-81.188);
        assertThat(request.getMonFri()).isEqualTo("9-5");
        assertThat(request.getSatSun()).isEqualTo("10-3");
        assertThat(request.getHolidays()).isEqualTo("Closed");
        assertThat(request.getAtmHours()).isEqualTo("24 hours");
        assertThat(request.getNumberOfATMs()).isEqualTo(2);
        assertThat(request.getIsOpen()).isTrue();
        assertThat(request.getInterPlanetary()).isFalse();
    }

    @Test
    void setters_setCorrectValues() {
        AtmCreateRequest request = new AtmCreateRequest();
        request.setName("Test ATM");
        request.setStreet("123 Test St");
        request.setCity("Test City");
        request.setState("Test State");
        request.setZip("12345");
        request.setLatitude(37.775);
        request.setLongitude(-81.188);
        request.setMonFri("9-5");
        request.setSatSun("10-3");
        request.setHolidays("Closed");
        request.setAtmHours("24 hours");
        request.setNumberOfATMs(2);
        request.setIsOpen(true);
        request.setInterPlanetary(false);

        assertThat(request.getName()).isEqualTo("Test ATM");
        assertThat(request.getStreet()).isEqualTo("123 Test St");
        assertThat(request.getLatitude()).isEqualTo(37.775);
        assertThat(request.getLongitude()).isEqualTo(-81.188);
        assertThat(request.getNumberOfATMs()).isEqualTo(2);
        assertThat(request.getIsOpen()).isTrue();
        assertThat(request.getInterPlanetary()).isFalse();
    }

    @Test
    void jsonDeserialization_parsesAllFields() throws Exception {
        String json = """
                {
                    "name": "Test ATM",
                    "street": "123 Test St",
                    "city": "Test City",
                    "state": "Test State",
                    "zip": "12345",
                    "latitude": 37.775,
                    "longitude": -81.188,
                    "monFri": "9-5",
                    "satSun": "10-3",
                    "holidays": "Closed",
                    "atmHours": "24 hours",
                    "numberOfATMs": 2,
                    "isOpen": true,
                    "interPlanetary": false
                }
                """;

        AtmCreateRequest request = objectMapper.readValue(json, AtmCreateRequest.class);

        assertThat(request.getName()).isEqualTo("Test ATM");
        assertThat(request.getStreet()).isEqualTo("123 Test St");
        assertThat(request.getCity()).isEqualTo("Test City");
        assertThat(request.getLatitude()).isEqualTo(37.775);
        assertThat(request.getNumberOfATMs()).isEqualTo(2);
        assertThat(request.getIsOpen()).isTrue();
        assertThat(request.getInterPlanetary()).isFalse();
    }
}
