/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimingsTest {

    @Test
    void timings_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Timings timings = TestDataFactory.createTestTimings();

        // Then
        assertThat(timings.getMonFri()).isEqualTo("9:00 AM - 5:00 PM");
        assertThat(timings.getSatSun()).isEqualTo("10:00 AM - 3:00 PM");
        assertThat(timings.getHolidays()).isEqualTo("Closed on holidays");
    }

    @Test
    void timings_holidaysOptional_canBeNull() {
        // Given
        Timings timings = TestDataFactory.createTimingsWithoutHolidays();

        // Then
        assertThat(timings.getMonFri()).isNotNull();
        assertThat(timings.getSatSun()).isNotNull();
        assertThat(timings.getHolidays()).isNull();
    }

    @Test
    void timings_equality_basedOnAllFields() {
        // Given
        Timings timings1 = TestDataFactory.createTestTimings();
        Timings timings2 = TestDataFactory.createTestTimings();

        // Then
        assertThat(timings1).isEqualTo(timings2);
        assertThat(timings1.hashCode()).isEqualTo(timings2.hashCode());
    }

    @Test
    void timings_allArgsConstructor_setsAllFields() {
        // When
        Timings timings = new Timings("Mon-Fri", "Sat-Sun", "Holidays");

        // Then
        assertThat(timings.getMonFri()).isEqualTo("Mon-Fri");
        assertThat(timings.getSatSun()).isEqualTo("Sat-Sun");
        assertThat(timings.getHolidays()).isEqualTo("Holidays");
    }
}
