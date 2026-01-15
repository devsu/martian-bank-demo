/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.*;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.repository.AtmRepository;
import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtmServiceTest {

    @Mock
    private AtmRepository atmRepository;

    @InjectMocks
    private AtmService atmService;

    @Captor
    private ArgumentCaptor<Atm> atmCaptor;

    @Nested
    class GetATMsTests {

        @Test
        void getATMs_withNullRequest_queriesNonInterplanetaryATMs() {
            // Given
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(TestDataFactory.createMarsAtms(5));

            // When
            List<AtmListResponse> result = atmService.getATMs(null);

            // Then
            verify(atmRepository).findByInterPlanetary(false);
            verify(atmRepository, never()).findByInterPlanetaryAndIsOpen(any(), any());
        }

        @Test
        void getATMs_withEmptyRequest_queriesNonInterplanetaryATMs() {
            // Given
            AtmFilterRequest request = new AtmFilterRequest();
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(TestDataFactory.createMarsAtms(5));

            // When
            List<AtmListResponse> result = atmService.getATMs(request);

            // Then
            verify(atmRepository).findByInterPlanetary(false);
        }

        @Test
        void getATMs_withIsOpenNowTrue_queriesOpenNonInterplanetaryATMs() {
            // Given
            AtmFilterRequest request = new AtmFilterRequest();
            request.setIsOpenNow(true);
            when(atmRepository.findByInterPlanetaryAndIsOpen(false, true))
                    .thenReturn(TestDataFactory.createOpenMarsAtms(3));

            // When
            List<AtmListResponse> result = atmService.getATMs(request);

            // Then
            verify(atmRepository).findByInterPlanetaryAndIsOpen(false, true);
        }

        @Test
        void getATMs_withIsInterPlanetaryTrue_queriesInterplanetaryATMs() {
            // Given
            AtmFilterRequest request = new AtmFilterRequest();
            request.setIsInterPlanetary(true);
            when(atmRepository.findByInterPlanetary(true))
                    .thenReturn(List.of(TestDataFactory.createInterplanetaryAtm()));

            // When
            List<AtmListResponse> result = atmService.getATMs(request);

            // Then
            verify(atmRepository).findByInterPlanetary(true);
        }

        @Test
        void getATMs_withBothFiltersTrue_queriesOpenInterplanetaryATMs() {
            // Given
            AtmFilterRequest request = new AtmFilterRequest();
            request.setIsOpenNow(true);
            request.setIsInterPlanetary(true);
            when(atmRepository.findByInterPlanetaryAndIsOpen(true, true))
                    .thenReturn(List.of(TestDataFactory.createInterplanetaryAtm()));

            // When
            List<AtmListResponse> result = atmService.getATMs(request);

            // Then
            verify(atmRepository).findByInterPlanetaryAndIsOpen(true, true);
        }

        @Test
        void getATMs_limitsResultsToFour() {
            // Given
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(TestDataFactory.createMarsAtms(10));

            // When
            List<AtmListResponse> result = atmService.getATMs(new AtmFilterRequest());

            // Then
            assertThat(result).hasSize(4);
        }

        @Test
        void getATMs_returnsAllIfLessThanFour() {
            // Given
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(TestDataFactory.createMarsAtms(2));

            // When
            List<AtmListResponse> result = atmService.getATMs(new AtmFilterRequest());

            // Then
            assertThat(result).hasSize(2);
        }

        @Test
        void getATMs_throwsExceptionWhenNoResults() {
            // Given
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(Collections.emptyList());

            // When/Then
            assertThatThrownBy(() -> atmService.getATMs(new AtmFilterRequest()))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No results found");
        }

        @Test
        void getATMs_throwsExceptionWhenNullResults() {
            // Given
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(null);

            // When/Then
            assertThatThrownBy(() -> atmService.getATMs(new AtmFilterRequest()))
                    .isInstanceOf(AtmNotFoundException.class);
        }

        @Test
        void getATMs_projectsCorrectFields() {
            // Given
            Atm atm = TestDataFactory.createTestAtm();
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(List.of(atm));

            // When
            List<AtmListResponse> result = atmService.getATMs(new AtmFilterRequest());

            // Then
            AtmListResponse response = result.get(0);
            assertThat(response.getId()).isEqualTo(atm.getId());
            assertThat(response.getName()).isEqualTo(atm.getName());
            assertThat(response.getCoordinates()).isEqualTo(atm.getCoordinates());
            assertThat(response.getAddress()).isEqualTo(atm.getAddress());
            assertThat(response.getIsOpen()).isEqualTo(atm.getIsOpen());
        }

        @Test
        void getATMs_shufflesResults() {
            // Given - create list with predictable order
            List<Atm> orderedAtms = TestDataFactory.createMarsAtms(10);
            when(atmRepository.findByInterPlanetary(false))
                    .thenReturn(orderedAtms);

            // When - run multiple times
            boolean foundDifferentOrder = false;
            List<String> firstRunIds = null;

            for (int i = 0; i < 20; i++) {
                List<AtmListResponse> result = atmService.getATMs(new AtmFilterRequest());
                List<String> currentIds = result.stream()
                        .map(AtmListResponse::getId)
                        .toList();

                if (firstRunIds == null) {
                    firstRunIds = currentIds;
                } else if (!currentIds.equals(firstRunIds)) {
                    foundDifferentOrder = true;
                    break;
                }
            }

            // Then - with 10 items limited to 4, randomization should produce different results
            // Note: This test may rarely fail due to random chance, but is statistically reliable
            assertThat(foundDifferentOrder).isTrue();
        }
    }

    @Nested
    class AddATMTests {

        @Test
        void addATM_savesAtmToRepository() {
            // Given
            AtmCreateRequest request = createValidRequest();
            Atm savedAtm = TestDataFactory.createTestAtm();
            when(atmRepository.save(any(Atm.class))).thenReturn(savedAtm);

            // When
            atmService.addATM(request);

            // Then
            verify(atmRepository).save(atmCaptor.capture());
            Atm captured = atmCaptor.getValue();
            assertThat(captured.getName()).isEqualTo(request.getName());
        }

        @Test
        void addATM_buildsNestedAddressFromFlatFields() {
            // Given
            AtmCreateRequest request = createValidRequest();
            when(atmRepository.save(any(Atm.class))).thenReturn(TestDataFactory.createTestAtm());

            // When
            atmService.addATM(request);

            // Then
            verify(atmRepository).save(atmCaptor.capture());
            Atm captured = atmCaptor.getValue();
            assertThat(captured.getAddress().getStreet()).isEqualTo(request.getStreet());
            assertThat(captured.getAddress().getCity()).isEqualTo(request.getCity());
            assertThat(captured.getAddress().getState()).isEqualTo(request.getState());
            assertThat(captured.getAddress().getZip()).isEqualTo(request.getZip());
        }

        @Test
        void addATM_buildsNestedCoordinatesFromFlatFields() {
            // Given
            AtmCreateRequest request = createValidRequest();
            when(atmRepository.save(any(Atm.class))).thenReturn(TestDataFactory.createTestAtm());

            // When
            atmService.addATM(request);

            // Then
            verify(atmRepository).save(atmCaptor.capture());
            Atm captured = atmCaptor.getValue();
            assertThat(captured.getCoordinates().getLatitude()).isEqualTo(request.getLatitude());
            assertThat(captured.getCoordinates().getLongitude()).isEqualTo(request.getLongitude());
        }

        @Test
        void addATM_buildsNestedTimingsFromFlatFields() {
            // Given
            AtmCreateRequest request = createValidRequest();
            when(atmRepository.save(any(Atm.class))).thenReturn(TestDataFactory.createTestAtm());

            // When
            atmService.addATM(request);

            // Then
            verify(atmRepository).save(atmCaptor.capture());
            Atm captured = atmCaptor.getValue();
            assertThat(captured.getTimings().getMonFri()).isEqualTo(request.getMonFri());
            assertThat(captured.getTimings().getSatSun()).isEqualTo(request.getSatSun());
            assertThat(captured.getTimings().getHolidays()).isEqualTo(request.getHolidays());
        }

        @Test
        void addATM_returnsCreatedAtm() {
            // Given
            AtmCreateRequest request = createValidRequest();
            Atm savedAtm = TestDataFactory.createTestAtm();
            when(atmRepository.save(any(Atm.class))).thenReturn(savedAtm);

            // When
            Atm result = atmService.addATM(request);

            // Then
            assertThat(result).isEqualTo(savedAtm);
        }

        @Test
        void addATM_throwsExceptionOnSaveFailure() {
            // Given
            AtmCreateRequest request = createValidRequest();
            when(atmRepository.save(any(Atm.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When/Then
            assertThatThrownBy(() -> atmService.addATM(request))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("Could not create ATM");
        }

        @Test
        void addATM_defaultsInterPlanetaryToFalseWhenNull() {
            // Given
            AtmCreateRequest request = createValidRequest();
            request.setInterPlanetary(null);
            when(atmRepository.save(any(Atm.class))).thenReturn(TestDataFactory.createTestAtm());

            // When
            atmService.addATM(request);

            // Then
            verify(atmRepository).save(atmCaptor.capture());
            assertThat(atmCaptor.getValue().getInterPlanetary()).isFalse();
        }

        private AtmCreateRequest createValidRequest() {
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
            return request;
        }
    }

    @Nested
    class GetSpecificATMTests {

        @Test
        void getSpecificATM_returnsAtmDetailResponse() {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            Atm atm = TestDataFactory.createTestAtm();
            when(atmRepository.findById(id)).thenReturn(Optional.of(atm));

            // When
            AtmDetailResponse result = atmService.getSpecificATM(id);

            // Then
            assertThat(result).isNotNull();
        }

        @Test
        void getSpecificATM_projectsCorrectFields() {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            Atm atm = TestDataFactory.createTestAtm();
            when(atmRepository.findById(id)).thenReturn(Optional.of(atm));

            // When
            AtmDetailResponse result = atmService.getSpecificATM(id);

            // Then
            assertThat(result.getCoordinates()).isEqualTo(atm.getCoordinates());
            assertThat(result.getTimings()).isEqualTo(atm.getTimings());
            assertThat(result.getAtmHours()).isEqualTo(atm.getAtmHours());
            assertThat(result.getNumberOfATMs()).isEqualTo(atm.getNumberOfATMs());
            assertThat(result.getIsOpen()).isEqualTo(atm.getIsOpen());
        }

        @Test
        void getSpecificATM_throwsExceptionWhenNotFound() {
            // Given
            String id = "nonexistent-id";
            when(atmRepository.findById(id)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> atmService.getSpecificATM(id))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("ATM not found");
        }

        @Test
        void getSpecificATM_callsRepositoryWithCorrectId() {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            when(atmRepository.findById(id)).thenReturn(Optional.of(TestDataFactory.createTestAtm()));

            // When
            atmService.getSpecificATM(id);

            // Then
            verify(atmRepository).findById(id);
        }
    }
}
