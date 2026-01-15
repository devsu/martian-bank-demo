/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void address_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Address address = TestDataFactory.createTestAddress();

        // Then
        assertThat(address.getStreet()).isEqualTo("14th Street, Martian Way");
        assertThat(address.getCity()).isEqualTo("Musk City");
        assertThat(address.getState()).isEqualTo("Mars");
        assertThat(address.getZip()).isEqualTo("40411");
    }

    @Test
    void address_equality_basedOnAllFields() {
        // Given
        Address address1 = TestDataFactory.createTestAddress();
        Address address2 = TestDataFactory.createTestAddress();

        // Then
        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void address_allArgsConstructor_setsAllFields() {
        // When
        Address address = new Address("Street", "City", "State", "12345");

        // Then
        assertThat(address.getStreet()).isEqualTo("Street");
        assertThat(address.getCity()).isEqualTo("City");
        assertThat(address.getState()).isEqualTo("State");
        assertThat(address.getZip()).isEqualTo("12345");
    }

    @Test
    void address_noArgsConstructor_createsEmptyObject() {
        // When
        Address address = new Address();

        // Then
        assertThat(address.getStreet()).isNull();
        assertThat(address.getCity()).isNull();
        assertThat(address.getState()).isNull();
        assertThat(address.getZip()).isNull();
    }
}
