package com.martianbank.atmlocator.service;

import com.martianbank.atmlocator.dto.AtmDetailsResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.exception.InvalidObjectIdException;
import com.martianbank.atmlocator.model.Address;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.model.Coordinates;
import com.martianbank.atmlocator.model.Timings;
import com.martianbank.atmlocator.repository.AtmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    private Atm buildAtm(String id, String name, boolean open, boolean interplanetary) {
        return Atm.builder()
                .id(id)
                .name(name)
                .isOpenNow(open)
                .isInterPlanetary(interplanetary)
                .build();
    }

    private Atm buildAtmWithAddress(String id, String name, boolean open, boolean interplanetary,
                                     String street, String city, String state, String zip,
                                     double lat, double lng) {
        return Atm.builder()
                .id(id)
                .name(name)
                .address(Address.builder().street(street).city(city).state(state).zip(zip).build())
                .coordinates(Coordinates.builder().latitude(lat).longitude(lng).build())
                .isOpenNow(open)
                .isInterPlanetary(interplanetary)
                .build();
    }

    /**
     * Creates a list of sample ATMs for testing.
     * Includes various combinations of isOpen and isInterPlanetary states.
     */
    private List<Atm> createSampleAtms() {
        return List.of(
            buildAtm("1", "Martian ATM (Highway)", true, false),      // Open, Non-interplanetary
            buildAtm("2", "Martian ATM (Theater)", false, false),     // Closed, Non-interplanetary
            buildAtm("3", "Earthern ATM (Georgia Tech)", true, true), // Open, Interplanetary
            buildAtm("4", "Saturn ATM (Mimas Moon)", false, true),    // Closed, Interplanetary
            buildAtm("5", "Martian ATM (Courthouse)", true, false),   // Open, Non-interplanetary
            buildAtm("6", "Neptune ATM (Triton Base)", true, true)    // Open, Interplanetary
        );
    }

    @Nested
    @DisplayName("findAtms with no filters")
    class NoFiltersTests {

        @Test
        @DisplayName("should return non-interplanetary ATMs when request is null or has null filters")
        void shouldReturnNonInterplanetaryAtmsWhenNoFiltersProvided() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);

            // Test null request
            List<AtmResponse> resultNullRequest = atmService.findAtms(null);
            assertThat(resultNullRequest).isNotNull();
            assertThat(resultNullRequest.size()).isLessThanOrEqualTo(AtmServiceImpl.MAX_RESULTS);
            assertThat(resultNullRequest.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));

            // Test request with null filters
            List<AtmResponse> resultNullFilters = atmService.findAtms(new AtmSearchRequest(null, null));
            assertThat(resultNullFilters).isNotNull();
            assertThat(resultNullFilters.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "2", "5"));
        }

        @Test
        @DisplayName("should return up to MAX_RESULTS ATMs maximum")
        void shouldReturnUpToFourAtmsMaximum() {
            // Add more non-interplanetary ATMs to test max limit
            List<Atm> manyAtms = new ArrayList<>(sampleAtms);
            for (int i = 7; i <= 15; i++) {
                manyAtms.add(buildAtm(String.valueOf(i), "ATM " + i, true, false));
            }
            when(atmRepository.findAll()).thenReturn(manyAtms);

            List<AtmResponse> result = atmService.findAtms(new AtmSearchRequest());

            assertThat(result).hasSize(AtmServiceImpl.MAX_RESULTS);
        }
    }

    @Nested
    @DisplayName("findAtms with isOpenNow filter")
    class IsOpenNowFilterTests {

        @Test
        @DisplayName("should filter ATMs by open/closed status")
        void shouldFilterAtmsByOpenStatus() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);

            // Test isOpenNow=true: should return only open, non-interplanetary ATMs (IDs 1, 5)
            List<AtmResponse> openResult = atmService.findAtms(new AtmSearchRequest(true, false));
            assertThat(openResult).allSatisfy(atm -> assertThat(atm.isOpen()).isTrue());
            assertThat(openResult.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("1", "5"));

            // Test isOpenNow=false: should return only closed, non-interplanetary ATMs (ID 2)
            List<AtmResponse> closedResult = atmService.findAtms(new AtmSearchRequest(false, false));
            assertThat(closedResult).allSatisfy(atm -> assertThat(atm.isOpen()).isFalse());
            assertThat(closedResult.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isEqualTo("2"));
        }
    }

    @Nested
    @DisplayName("findAtms with isInterPlanetary filter")
    class IsInterPlanetaryFilterTests {

        @Test
        @DisplayName("should return interplanetary ATMs when isInterPlanetary is true")
        void shouldReturnInterplanetaryAtmsWhenIsInterPlanetaryIsTrue() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);

            List<AtmResponse> result = atmService.findAtms(new AtmSearchRequest(null, true));

            // Should return interplanetary ATMs (IDs 3, 4, 6)
            assertThat(result).isNotNull();
            assertThat(result.stream().map(AtmResponse::id).toList())
                    .allSatisfy(id -> assertThat(id).isIn("3", "4", "6"));
        }
        // Note: isInterPlanetary=false and isInterPlanetary=null are covered by NoFiltersTests
    }

    @Nested
    @DisplayName("findAtms with combined filters (AND logic)")
    class CombinedFiltersTests {

        @Test
        @DisplayName("should apply AND logic correctly for all filter combinations")
        void shouldApplyAndLogicCorrectlyForAllCombinations() {
            // Create a minimal dataset to verify AND logic precisely
            List<Atm> limitedAtms = List.of(
                    buildAtm("open-interplanetary", "Open Interplanetary", true, true),
                    buildAtm("closed-interplanetary", "Closed Interplanetary", false, true),
                    buildAtm("open-local", "Open Local", true, false),
                    buildAtm("closed-local", "Closed Local", false, false)
            );
            when(atmRepository.findAll()).thenReturn(limitedAtms);

            // Test: Open AND Interplanetary
            List<AtmResponse> result1 = atmService.findAtms(new AtmSearchRequest(true, true));
            assertThat(result1).hasSize(1);
            assertThat(result1.get(0).id()).isEqualTo("open-interplanetary");

            // Test: Closed AND Interplanetary
            List<AtmResponse> result2 = atmService.findAtms(new AtmSearchRequest(false, true));
            assertThat(result2).hasSize(1);
            assertThat(result2.get(0).id()).isEqualTo("closed-interplanetary");

            // Test: Open AND Non-Interplanetary
            List<AtmResponse> result3 = atmService.findAtms(new AtmSearchRequest(true, false));
            assertThat(result3).hasSize(1);
            assertThat(result3.get(0).id()).isEqualTo("open-local");

            // Test: Closed AND Non-Interplanetary
            List<AtmResponse> result4 = atmService.findAtms(new AtmSearchRequest(false, false));
            assertThat(result4).hasSize(1);
            assertThat(result4.get(0).id()).isEqualTo("closed-local");
        }
    }

    @Nested
    @DisplayName("findAtms with empty results")
    class EmptyResultsTests {

        @Test
        @DisplayName("should throw AtmNotFoundException when no ATMs match filters")
        void shouldThrowAtmNotFoundExceptionWhenNoAtmsMatchFilters() {
            // Test 1: Empty repository
            when(atmRepository.findAll()).thenReturn(Collections.emptyList());
            assertThatThrownBy(() -> atmService.findAtms(new AtmSearchRequest()))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No ATMs found");

            // Test 2: No open ATMs when filtering for open
            when(atmRepository.findAll()).thenReturn(List.of(
                    buildAtm("1", "Closed ATM", false, false)
            ));
            assertThatThrownBy(() -> atmService.findAtms(new AtmSearchRequest(true, false)))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No ATMs found");

            // Test 3: No interplanetary ATMs when filtering for interplanetary
            when(atmRepository.findAll()).thenReturn(List.of(
                    buildAtm("1", "Local ATM", true, false)
            ));
            assertThatThrownBy(() -> atmService.findAtms(new AtmSearchRequest(null, true)))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No ATMs found");
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
            // buildAtm doesn't set address/coordinates, so they'll be null
            List<Atm> atmWithNulls = List.of(buildAtm("null-test", "ATM with nulls", true, false));
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

    @Nested
    @DisplayName("findById scenarios")
    class FindByIdTests {

        private static final String VALID_OBJECT_ID = "507f1f77bcf86cd799439011";
        private static final String ANOTHER_VALID_OBJECT_ID = "507f1f77bcf86cd799439012";

        @Nested
        @DisplayName("when ID is valid and ATM exists")
        class ValidExistingIdTests {

            @Test
            @DisplayName("should return fully mapped ATM details")
            void shouldReturnFullyMappedAtmDetails() {
                Atm atm = buildAtmWithAllDetails(VALID_OBJECT_ID);
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atm));

                AtmDetailsResponse result = atmService.findById(VALID_OBJECT_ID);

                // Verify basic fields
                assertThat(result).isNotNull();
                assertThat(result.isOpen()).isTrue();
                assertThat(result.atmHours()).isEqualTo("24 hours");
                assertThat(result.numberOfATMs()).isEqualTo(3);

                // Verify coordinates mapping
                assertThat(result.coordinates()).isNotNull();
                assertThat(result.coordinates().latitude()).isEqualTo(37.7749);
                assertThat(result.coordinates().longitude()).isEqualTo(-122.4194);

                // Verify timings mapping
                assertThat(result.timings()).isNotNull();
                assertThat(result.timings().monFri()).isEqualTo("9:00 AM - 6:00 PM");
                assertThat(result.timings().satSun()).isEqualTo("10:00 AM - 4:00 PM");
                assertThat(result.timings().holidays()).isEqualTo("Closed");

                verify(atmRepository).findById(VALID_OBJECT_ID);
            }

            @Test
            @DisplayName("should handle ATM with null optional fields")
            void shouldHandleAtmWithNullOptionalFields() {
                // ATM with null coordinates
                Atm atmNoCoords = Atm.builder()
                        .id(VALID_OBJECT_ID)
                        .name("Test ATM")
                        .isOpenNow(true)
                        .atmHours("24 hours")
                        .numberOfATMs(2)
                        .timings(Timings.builder().monFri("9-5").build())
                        .build();
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atmNoCoords));

                AtmDetailsResponse result1 = atmService.findById(VALID_OBJECT_ID);
                assertThat(result1.coordinates()).isNull();
                assertThat(result1.timings()).isNotNull();

                // ATM with null timings
                Atm atmNoTimings = Atm.builder()
                        .id(VALID_OBJECT_ID)
                        .name("Test ATM")
                        .isOpenNow(false)
                        .atmHours("9-5")
                        .numberOfATMs(1)
                        .coordinates(Coordinates.builder().latitude(40.0).longitude(-74.0).build())
                        .build();
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atmNoTimings));

                AtmDetailsResponse result2 = atmService.findById(VALID_OBJECT_ID);
                assertThat(result2.timings()).isNull();
                assertThat(result2.coordinates()).isNotNull();
            }
        }

        @Test
        @DisplayName("should throw AtmNotFoundException when ATM does not exist")
        void shouldThrowAtmNotFoundExceptionWhenAtmDoesNotExist() {
            when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> atmService.findById(VALID_OBJECT_ID))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("ATM information not found");
            verify(atmRepository).findById(VALID_OBJECT_ID);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",                              // Empty string
                "   ",                           // Whitespace only
                "invalid-id",
                "123",
                "xyz",
                "507f1f77bcf86cd79943901",       // 23 chars (too short)
                "507f1f77bcf86cd7994390111",     // 25 chars (too long)
                "ZZZZZZZZZZZZZZZZZZZZZZZZ",      // 24 chars but invalid hex
                "507f1f77bcf86cd79943901G",      // Contains non-hex character
                "   507f1f77bcf86cd799439011",   // Leading spaces
                "507f1f77bcf86cd799439011   "    // Trailing spaces
        })
        @DisplayName("should throw InvalidObjectIdException for invalid ObjectId formats")
        void shouldThrowInvalidObjectIdExceptionForInvalidFormats(String invalidId) {
            assertThatThrownBy(() -> atmService.findById(invalidId))
                    .isInstanceOf(InvalidObjectIdException.class)
                    .hasMessage("Resource not found");
            verify(atmRepository, never()).findById(invalidId);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for null ID")
        void shouldThrowIllegalArgumentExceptionForNullId() {
            // ObjectId.isValid(null) throws IllegalArgumentException
            assertThatThrownBy(() -> atmService.findById(null))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(atmRepository, never()).findById(null);
        }

        private Atm buildAtmWithAllDetails(String id) {
            return Atm.builder()
                    .id(id)
                    .name("Test ATM - Full Details")
                    .address(Address.builder()
                            .street("123 Main St")
                            .city("San Francisco")
                            .state("CA")
                            .zip("94102")
                            .build())
                    .coordinates(Coordinates.builder()
                            .latitude(37.7749)
                            .longitude(-122.4194)
                            .build())
                    .timings(Timings.builder()
                            .monFri("9:00 AM - 6:00 PM")
                            .satSun("10:00 AM - 4:00 PM")
                            .holidays("Closed")
                            .build())
                    .atmHours("24 hours")
                    .numberOfATMs(3)
                    .isOpenNow(true)
                    .isInterPlanetary(false)
                    .build();
        }
    }
}
