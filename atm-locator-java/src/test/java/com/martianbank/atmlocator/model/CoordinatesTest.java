/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.model;

import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinatesTest {

    @Test
    void coordinates_allFieldsSet_gettersReturnCorrectValues() {
        // Given
        Coordinates coordinates = TestDataFactory.createTestCoordinates();

        // Then
        assertThat(coordinates.getLatitude()).isEqualTo(37.775);
        assertThat(coordinates.getLongitude()).isEqualTo(-81.188);
    }

    @Test
    void coordinates_equality_basedOnAllFields() {
        // Given
        Coordinates coord1 = TestDataFactory.createTestCoordinates();
        Coordinates coord2 = TestDataFactory.createTestCoordinates();

        // Then
        assertThat(coord1).isEqualTo(coord2);
        assertThat(coord1.hashCode()).isEqualTo(coord2.hashCode());
    }

    @Test
    void coordinates_allArgsConstructor_setsAllFields() {
        // When
        Coordinates coordinates = new Coordinates(40.7128, -74.0060);

        // Then
        assertThat(coordinates.getLatitude()).isEqualTo(40.7128);
        assertThat(coordinates.getLongitude()).isEqualTo(-74.0060);
    }

    @Test
    void coordinates_handlesNegativeValues() {
        // Given
        Coordinates coordinates = new Coordinates(-94.764, 31.1897);

        // Then
        assertThat(coordinates.getLatitude()).isEqualTo(-94.764);
        assertThat(coordinates.getLongitude()).isEqualTo(31.1897);
    }
}
