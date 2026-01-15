/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AtmTest {

    @Test
    void atm_defaultInterPlanetary_isFalse() {
        Atm atm = new Atm();
        assertThat(atm.getInterPlanetary()).isFalse();
    }

    @Test
    void atm_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // Then
        assertThat(atm.getId()).isEqualTo(TestDataFactory.TEST_ATM_ID);
        assertThat(atm.getName()).isEqualTo(TestDataFactory.TEST_ATM_NAME);
        assertThat(atm.getAddress()).isNotNull();
        assertThat(atm.getCoordinates()).isNotNull();
        assertThat(atm.getTimings()).isNotNull();
        assertThat(atm.getAtmHours()).isEqualTo("24 hours");
        assertThat(atm.getNumberOfATMs()).isEqualTo(2);
        assertThat(atm.getIsOpen()).isTrue();
        assertThat(atm.getInterPlanetary()).isFalse();
        assertThat(atm.getCreatedAt()).isNotNull();
        assertThat(atm.getUpdatedAt()).isNotNull();
    }

    @Test
    void atm_equality_basedOnAllFields() {
        // Given
        Atm atm1 = TestDataFactory.createTestAtm();
        Atm atm2 = TestDataFactory.createTestAtm();

        // Then
        assertThat(atm1).isEqualTo(atm2);
        assertThat(atm1.hashCode()).isEqualTo(atm2.hashCode());
    }

    @Test
    void atm_toString_containsAllFields() {
        // Given
        Atm atm = TestDataFactory.createTestAtm();

        // When
        String toString = atm.toString();

        // Then
        assertThat(toString).contains(TestDataFactory.TEST_ATM_ID);
        assertThat(toString).contains(TestDataFactory.TEST_ATM_NAME);
    }

    @Test
    void atm_timestamps_acceptInstant() {
        // Given
        Atm atm = new Atm();
        Instant now = Instant.now();

        // When
        atm.setCreatedAt(now);
        atm.setUpdatedAt(now);

        // Then
        assertThat(atm.getCreatedAt()).isEqualTo(now);
        assertThat(atm.getUpdatedAt()).isEqualTo(now);
    }
}
