/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private AtmRepository atmRepository;

    @Captor
    private ArgumentCaptor<List<Atm>> atmListCaptor;

    private DataSeeder dataSeeder;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        dataSeeder = new DataSeeder(atmRepository, objectMapper);
    }

    @Test
    void run_deletesExistingDataBeforeSeeding() throws Exception {
        // When
        dataSeeder.run();

        // Then
        verify(atmRepository).deleteAll();
        verify(atmRepository).saveAll(anyList());
    }

    @Test
    void run_callsDeleteBeforeSave() throws Exception {
        // When
        dataSeeder.run();

        // Then - verify order of operations
        var inOrder = inOrder(atmRepository);
        inOrder.verify(atmRepository).deleteAll();
        inOrder.verify(atmRepository).saveAll(anyList());
    }

    @Test
    void loadAndProcessSeedData_returns13Records() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        assertThat(atms).hasSize(13);
    }

    @Test
    void loadAndProcessSeedData_processesMongoOidFormat() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - first ATM should have correct ID from $oid
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getId()).isEqualTo("64a6f1cc8c1899820dbdf25a");
    }

    @Test
    void loadAndProcessSeedData_processesMongoDateFormat() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - timestamps should be parsed from $date format
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getCreatedAt()).isNotNull();
        assertThat(firstAtm.getUpdatedAt()).isNotNull();
    }

    @Test
    void loadAndProcessSeedData_parsesNestedAddressObject() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getAddress()).isNotNull();
        assertThat(firstAtm.getAddress().getStreet()).isEqualTo("14th Street, Martian Way");
        assertThat(firstAtm.getAddress().getCity()).isEqualTo("Musk City");
        assertThat(firstAtm.getAddress().getState()).isEqualTo("Mars");
        assertThat(firstAtm.getAddress().getZip()).isEqualTo("40411");
    }

    @Test
    void loadAndProcessSeedData_parsesNestedCoordinatesObject() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getCoordinates()).isNotNull();
        assertThat(firstAtm.getCoordinates().getLatitude()).isEqualTo(37.775);
        assertThat(firstAtm.getCoordinates().getLongitude()).isEqualTo(-81.188);
    }

    @Test
    void loadAndProcessSeedData_parsesNestedTimingsObject() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then
        Atm firstAtm = atms.get(0);
        assertThat(firstAtm.getTimings()).isNotNull();
        assertThat(firstAtm.getTimings().getMonFri()).isEqualTo("9:00 AM - 5:00 PM");
        assertThat(firstAtm.getTimings().getSatSun()).isEqualTo("10:00 AM - 3:00 PM");
        assertThat(firstAtm.getTimings().getHolidays()).isEqualTo("Closed on holidays");
    }

    @Test
    void loadAndProcessSeedData_parsesInterPlanetaryField() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - most ATMs should be non-interplanetary
        long nonInterplanetary = atms.stream()
                .filter(atm -> !atm.getInterPlanetary())
                .count();
        long interplanetary = atms.stream()
                .filter(Atm::getInterPlanetary)
                .count();

        assertThat(nonInterplanetary).isEqualTo(11);  // 11 Mars ATMs
        assertThat(interplanetary).isEqualTo(2);      // 2 interplanetary ATMs
    }

    @Test
    void loadAndProcessSeedData_parsesIsOpenField() throws Exception {
        // When
        List<Atm> atms = dataSeeder.loadAndProcessSeedData();

        // Then - verify isOpen field is correctly parsed
        long openAtms = atms.stream()
                .filter(Atm::getIsOpen)
                .count();

        assertThat(openAtms).isGreaterThan(0);
        assertThat(openAtms).isLessThan(13);  // Not all ATMs are open
    }

    @Test
    void run_savesAllLoadedAtms() throws Exception {
        // When
        dataSeeder.run();

        // Then
        verify(atmRepository).saveAll(atmListCaptor.capture());
        List<Atm> savedAtms = atmListCaptor.getValue();
        assertThat(savedAtms).hasSize(13);
    }

    @Test
    void run_continuesIfDeleteFails() throws Exception {
        // Given
        doThrow(new RuntimeException("Collection doesn't exist"))
                .when(atmRepository).deleteAll();

        // When - should not throw
        dataSeeder.run();

        // Then - save should still be attempted
        verify(atmRepository).saveAll(anyList());
    }
}
