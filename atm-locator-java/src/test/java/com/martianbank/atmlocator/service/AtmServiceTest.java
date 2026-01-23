package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AtmServiceImpl.
 * Tests cover all filter scenarios: no filters, isOpenNow, isInterPlanetary,
 * combined filters (AND logic), and empty results.
 */
@ExtendWith(MockitoExtension.class)
class AtmServiceTest {

    @Mock
    private AtmRepository atmRepository;

    @InjectMocks
    private AtmServiceImpl atmService;

    private List<Atm> sampleAtms;

    @BeforeEach
    void setUp() {
        sampleAtms = createSampleAtms();
    }

    /**
     * Creates a list of sample ATMs for testing.
     * Includes various combinations of isOpen and isInterPlanetary states.
     */
    private List<Atm> createSampleAtms() {
        List<Atm> atms = new ArrayList<>();

        // ATM 1: Open, Non-interplanetary (Mars)
        atms.add(Atm.builder()
                .id("1")
                .name("Martian ATM (Highway)")
                .address(Address.builder()
                        .street("14th Street, Martian Way")
                        .city("Musk City")
                        .state("Mars")
                        .zip("40411")
                        .build())
                .coordinates(Coordinates.builder()
                        .latitude(37.775)
                        .longitude(-81.188)
                        .build())
                .isOpenNow(true)
                .isInterPlanetary(false)
                .build());

        // ATM 2: Closed, Non-interplanetary (Mars)
        atms.add(Atm.builder()
                .id("2")
                .name("Martian ATM (Theater)")
                .address(Address.builder()
                        .street("25th Street, Theater Lane")
                        .city("Musk City")
                        .state("Mars")
                        .zip("40412")
                        .build())
                .coordinates(Coordinates.builder()
                        .latitude(37.780)
                        .longitude(-81.190)
                        .build())
                .isOpenNow(false)
                .isInterPlanetary(false)
                .build());

        // ATM 3: Open, Interplanetary (Earth)
        atms.add(Atm.builder()
                .id("3")
                .name("Earthern ATM (Georgia Tech)")
                .address(Address.builder()
                        .street("North Avenue NW")
                        .city("Atlanta")
                        .state("Georgia")
                        .zip("30332")
                        .build())
                .coordinates(Coordinates.builder()
                        .latitude(33.775)
                        .longitude(-84.398)
                        .build())
                .isOpenNow(true)
                .isInterPlanetary(true)
                .build());

        // ATM 4: Closed, Interplanetary (Saturn)
        atms.add(Atm.builder()
                .id("4")
                .name("Saturn ATM (Mimas Moon)")
                .address(Address.builder()
                        .street("Crater Street")
                        .city("Mimas")
                        .state("Saturn")
                        .zip("00001")
                        .build())
                .coordinates(Coordinates.builder()
                        .latitude(0.0)
                        .longitude(0.0)
                        .build())
                .isOpenNow(false)
                .isInterPlanetary(true)
                .build());

        // ATM 5: Open, Non-interplanetary (Mars)
        atms.add(Atm.builder()
                .id("5")
                .name("Martian ATM (Courthouse)")
                .address(Address.builder()
                        .street("Main Street")
                        .city("Musk City")
                        .state("Mars")
                        .zip("40413")
                        .build())
                .coordinates(Coordinates.builder()
                        .latitude(37.790)
                        .longitude(-81.200)
                        .build())
                .isOpenNow(true)
                .isInterPlanetary(false)
                .build());

        // ATM 6: Open, Interplanetary (Neptune)
        atms.add(Atm.builder()
                .id("6")
                .name("Neptune ATM (Triton Base)")
                .address(Address.builder()
                        .street("Triton Base Road")
                        .city("Triton")
                        .state("Neptune")
                        .zip("00002")
                        .build())
                .coordinates(Coordinates.builder()
                        .latitude(1.0)
                        .longitude(1.0)
                        .build())
                .isOpenNow(true)
                .isInterPlanetary(true)
                .build());

        return atms;
    }

    @Nested
    @DisplayName("findAtms with no filters")
    class NoFiltersTests {

        @Test
        @DisplayName("should return non-interplanetary ATMs when request is null")
        void shouldReturnNonInterplanetaryAtmsWhenRequestIsNull() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);

            List<AtmResponse> result = atmService.findAtms(null);

            // By default, should filter to non-interplanetary ATMs
            assertThat(result).isNotNull();
            assertThat(result.size()).isLessThanOrEqualTo(4);
            // All returned ATMs should be non-interplanetary (IDs 1, 2, 5)
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));
        }

        @Test
        @DisplayName("should return non-interplanetary ATMs when request has null filters")
        void shouldReturnNonInterplanetaryAtmsWhenFiltersAreNull() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, null);

            List<AtmResponse> result = atmService.findAtms(request);

            assertThat(result).isNotNull();
            assertThat(result.size()).isLessThanOrEqualTo(4);
            // All returned ATMs should be non-interplanetary
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));
        }

        @Test
        @DisplayName("should return up to 4 ATMs maximum")
        void shouldReturnUpToFourAtmsMaximum() {
            // Add more non-interplanetary ATMs to test max limit
            List<Atm> manyAtms = new ArrayList<>(sampleAtms);
            for (int i = 7; i <= 15; i++) {
                manyAtms.add(Atm.builder()
                        .id(String.valueOf(i))
                        .name("ATM " + i)
                        .isOpenNow(true)
                        .isInterPlanetary(false)
                        .build());
            }
            when(atmRepository.findAll()).thenReturn(manyAtms);

            List<AtmResponse> result = atmService.findAtms(new AtmSearchRequest());

            assertThat(result).hasSize(4);
        }
    }

    @Nested
    @DisplayName("findAtms with isOpenNow filter")
    class IsOpenNowFilterTests {

        @Test
        @DisplayName("should return only open ATMs when isOpenNow is true")
        void shouldReturnOnlyOpenAtmsWhenIsOpenNowIsTrue() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(true, false);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return only open, non-interplanetary ATMs (IDs 1, 5)
            assertThat(result).isNotNull();
            assertThat(result).allSatisfy(atm -> assertThat(atm.isOpen()).isTrue());
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "5"));
        }

        @Test
        @DisplayName("should return all ATMs (open and closed) when isOpenNow is false")
        void shouldReturnAllAtmsWhenIsOpenNowIsFalse() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(false, false);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return non-interplanetary ATMs regardless of open status
            assertThat(result).isNotNull();
            assertThat(result.size()).isLessThanOrEqualTo(4);
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));
        }

        @Test
        @DisplayName("should return all ATMs when isOpenNow is null")
        void shouldReturnAllAtmsWhenIsOpenNowIsNull() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, false);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return non-interplanetary ATMs regardless of open status
            assertThat(result).isNotNull();
            assertThat(result.size()).isLessThanOrEqualTo(4);
        }
    }

    @Nested
    @DisplayName("findAtms with isInterPlanetary filter")
    class IsInterPlanetaryFilterTests {

        @Test
        @DisplayName("should return interplanetary ATMs when isInterPlanetary is true")
        void shouldReturnInterplanetaryAtmsWhenIsInterPlanetaryIsTrue() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, true);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return interplanetary ATMs (IDs 3, 4, 6)
            assertThat(result).isNotNull();
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("3", "4", "6"));
        }

        @Test
        @DisplayName("should return non-interplanetary ATMs when isInterPlanetary is false")
        void shouldReturnNonInterplanetaryAtmsWhenIsInterPlanetaryIsFalse() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, false);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return non-interplanetary ATMs (IDs 1, 2, 5)
            assertThat(result).isNotNull();
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));
        }

        @Test
        @DisplayName("should return non-interplanetary ATMs when isInterPlanetary is null (default)")
        void shouldReturnNonInterplanetaryAtmsWhenIsInterPlanetaryIsNull() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, null);

            List<AtmResponse> result = atmService.findAtms(request);

            // Default behavior: return non-interplanetary ATMs
            assertThat(result).isNotNull();
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));
        }
    }

    @Nested
    @DisplayName("findAtms with combined filters (AND logic)")
    class CombinedFiltersTests {

        @Test
        @DisplayName("should return open interplanetary ATMs when both filters are true")
        void shouldReturnOpenInterplanetaryAtmsWhenBothFiltersAreTrue() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(true, true);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return open AND interplanetary ATMs (IDs 3, 6)
            assertThat(result).isNotNull();
            assertThat(result).allSatisfy(atm -> assertThat(atm.isOpen()).isTrue());
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("3", "6"));
        }

        @Test
        @DisplayName("should return open non-interplanetary ATMs when isOpenNow=true and isInterPlanetary=false")
        void shouldReturnOpenNonInterplanetaryAtms() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(true, false);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return open AND non-interplanetary ATMs (IDs 1, 5)
            assertThat(result).isNotNull();
            assertThat(result).allSatisfy(atm -> assertThat(atm.isOpen()).isTrue());
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "5"));
        }

        @Test
        @DisplayName("should return all interplanetary ATMs when isOpenNow=false and isInterPlanetary=true")
        void shouldReturnAllInterplanetaryAtmsWhenIsOpenNowIsFalse() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);
            AtmSearchRequest request = new AtmSearchRequest(false, true);

            List<AtmResponse> result = atmService.findAtms(request);

            // Should return all interplanetary ATMs (open and closed - IDs 3, 4, 6)
            assertThat(result).isNotNull();
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("3", "4", "6"));
        }

        @Test
        @DisplayName("should apply AND logic correctly with limited dataset")
        void shouldApplyAndLogicCorrectlyWithLimitedDataset() {
            // Create a minimal dataset to verify AND logic precisely
            List<Atm> limitedAtms = List.of(
                    Atm.builder()
                            .id("open-interplanetary")
                            .name("Open Interplanetary")
                            .isOpenNow(true)
                            .isInterPlanetary(true)
                            .build(),
                    Atm.builder()
                            .id("closed-interplanetary")
                            .name("Closed Interplanetary")
                            .isOpenNow(false)
                            .isInterPlanetary(true)
                            .build(),
                    Atm.builder()
                            .id("open-local")
                            .name("Open Local")
                            .isOpenNow(true)
                            .isInterPlanetary(false)
                            .build(),
                    Atm.builder()
                            .id("closed-local")
                            .name("Closed Local")
                            .isOpenNow(false)
                            .isInterPlanetary(false)
                            .build()
            );
            when(atmRepository.findAll()).thenReturn(limitedAtms);

            // Test: Open AND Interplanetary
            AtmSearchRequest openInterplanetary = new AtmSearchRequest(true, true);
            List<AtmResponse> result = atmService.findAtms(openInterplanetary);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo("open-interplanetary");
        }
    }

    @Nested
    @DisplayName("findAtms with empty results")
    class EmptyResultsTests {

        @Test
        @DisplayName("should return empty list when repository returns empty list")
        void shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
            when(atmRepository.findAll()).thenReturn(Collections.emptyList());

            List<AtmResponse> result = atmService.findAtms(new AtmSearchRequest());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when no ATMs match isOpenNow filter")
        void shouldReturnEmptyListWhenNoAtmsMatchIsOpenNowFilter() {
            // All ATMs are closed
            List<Atm> closedAtms = List.of(
                    Atm.builder()
                            .id("1")
                            .name("Closed ATM 1")
                            .isOpenNow(false)
                            .isInterPlanetary(false)
                            .build(),
                    Atm.builder()
                            .id("2")
                            .name("Closed ATM 2")
                            .isOpenNow(false)
                            .isInterPlanetary(false)
                            .build()
            );
            when(atmRepository.findAll()).thenReturn(closedAtms);
            AtmSearchRequest request = new AtmSearchRequest(true, false);

            List<AtmResponse> result = atmService.findAtms(request);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when no ATMs match isInterPlanetary filter")
        void shouldReturnEmptyListWhenNoAtmsMatchIsInterPlanetaryFilter() {
            // All ATMs are non-interplanetary
            List<Atm> localAtms = List.of(
                    Atm.builder()
                            .id("1")
                            .name("Local ATM 1")
                            .isOpenNow(true)
                            .isInterPlanetary(false)
                            .build()
            );
            when(atmRepository.findAll()).thenReturn(localAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, true);

            List<AtmResponse> result = atmService.findAtms(request);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when no ATMs match combined filters")
        void shouldReturnEmptyListWhenNoAtmsMatchCombinedFilters() {
            // No open interplanetary ATMs
            List<Atm> atms = List.of(
                    Atm.builder()
                            .id("1")
                            .name("Closed Interplanetary")
                            .isOpenNow(false)
                            .isInterPlanetary(true)
                            .build(),
                    Atm.builder()
                            .id("2")
                            .name("Open Local")
                            .isOpenNow(true)
                            .isInterPlanetary(false)
                            .build()
            );
            when(atmRepository.findAll()).thenReturn(atms);
            AtmSearchRequest request = new AtmSearchRequest(true, true);

            List<AtmResponse> result = atmService.findAtms(request);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAtms response mapping")
    class ResponseMappingTests {

        @Test
        @DisplayName("should correctly map ATM entity to response DTO")
        void shouldCorrectlyMapAtmEntityToResponseDto() {
            List<Atm> singleAtm = List.of(
                    Atm.builder()
                            .id("test-id")
                            .name("Test ATM")
                            .address(Address.builder()
                                    .street("123 Test Street")
                                    .city("Test City")
                                    .state("Test State")
                                    .zip("12345")
                                    .build())
                            .coordinates(Coordinates.builder()
                                    .latitude(40.7128)
                                    .longitude(-74.0060)
                                    .build())
                            .isOpenNow(true)
                            .isInterPlanetary(false)
                            .build()
            );
            when(atmRepository.findAll()).thenReturn(singleAtm);

            List<AtmResponse> result = atmService.findAtms(new AtmSearchRequest(null, false));

            assertThat(result).hasSize(1);
            AtmResponse response = result.get(0);
            assertThat(response.id()).isEqualTo("test-id");
            assertThat(response.name()).isEqualTo("Test ATM");
            assertThat(response.isOpen()).isTrue();
            assertThat(response.coordinates()).isNotNull();
            assertThat(response.coordinates().latitude()).isEqualTo(40.7128);
            assertThat(response.coordinates().longitude()).isEqualTo(-74.0060);
            assertThat(response.address()).isNotNull();
            assertThat(response.address().street()).isEqualTo("123 Test Street");
            assertThat(response.address().city()).isEqualTo("Test City");
            assertThat(response.address().state()).isEqualTo("Test State");
            assertThat(response.address().zip()).isEqualTo("12345");
        }

        @Test
        @DisplayName("should handle ATM with null coordinates and address")
        void shouldHandleAtmWithNullCoordinatesAndAddress() {
            List<Atm> atmWithNulls = List.of(
                    Atm.builder()
                            .id("null-test")
                            .name("ATM with nulls")
                            .address(null)
                            .coordinates(null)
                            .isOpenNow(true)
                            .isInterPlanetary(false)
                            .build()
            );
            when(atmRepository.findAll()).thenReturn(atmWithNulls);

            List<AtmResponse> result = atmService.findAtms(new AtmSearchRequest(null, false));

            assertThat(result).hasSize(1);
            AtmResponse response = result.get(0);
            assertThat(response.id()).isEqualTo("null-test");
            assertThat(response.name()).isEqualTo("ATM with nulls");
            assertThat(response.coordinates()).isNull();
            assertThat(response.address()).isNull();
        }
    }
}
