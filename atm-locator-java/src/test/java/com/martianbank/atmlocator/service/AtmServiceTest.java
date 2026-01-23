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
        @DisplayName("should return non-interplanetary ATMs when request is null")
        void shouldReturnNonInterplanetaryAtmsWhenRequestIsNull() {
            when(atmRepository.findAll()).thenReturn(sampleAtms);

            List<AtmResponse> result = atmService.findAtms(null);

            // By default, should filter to non-interplanetary ATMs
            assertThat(result).isNotNull();
            assertThat(result.size()).isLessThanOrEqualTo(AtmServiceImpl.MAX_RESULTS);
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
            assertThat(result.size()).isLessThanOrEqualTo(AtmServiceImpl.MAX_RESULTS);
            // All returned ATMs should be non-interplanetary
            assertThat(result.stream().map(AtmResponse::id).toList())
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
            assertThat(result.size()).isLessThanOrEqualTo(AtmServiceImpl.MAX_RESULTS);
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
            assertThat(result.size()).isLessThanOrEqualTo(AtmServiceImpl.MAX_RESULTS);
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
                    buildAtm("open-interplanetary", "Open Interplanetary", true, true),
                    buildAtm("closed-interplanetary", "Closed Interplanetary", false, true),
                    buildAtm("open-local", "Open Local", true, false),
                    buildAtm("closed-local", "Closed Local", false, false)
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
        @DisplayName("should throw AtmNotFoundException when repository returns empty list")
        void shouldThrowAtmNotFoundExceptionWhenRepositoryReturnsEmpty() {
            when(atmRepository.findAll()).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> atmService.findAtms(new AtmSearchRequest()))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No ATMs found");
        }

        @Test
        @DisplayName("should throw AtmNotFoundException when no ATMs match isOpenNow filter")
        void shouldThrowAtmNotFoundExceptionWhenNoAtmsMatchIsOpenNowFilter() {
            // All ATMs are closed
            List<Atm> closedAtms = List.of(
                    buildAtm("1", "Closed ATM 1", false, false),
                    buildAtm("2", "Closed ATM 2", false, false)
            );
            when(atmRepository.findAll()).thenReturn(closedAtms);
            AtmSearchRequest request = new AtmSearchRequest(true, false);

            assertThatThrownBy(() -> atmService.findAtms(request))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No ATMs found");
        }

        @Test
        @DisplayName("should throw AtmNotFoundException when no ATMs match isInterPlanetary filter")
        void shouldThrowAtmNotFoundExceptionWhenNoAtmsMatchIsInterPlanetaryFilter() {
            // All ATMs are non-interplanetary
            List<Atm> localAtms = List.of(buildAtm("1", "Local ATM 1", true, false));
            when(atmRepository.findAll()).thenReturn(localAtms);
            AtmSearchRequest request = new AtmSearchRequest(null, true);

            assertThatThrownBy(() -> atmService.findAtms(request))
                    .isInstanceOf(AtmNotFoundException.class)
                    .hasMessage("No ATMs found");
        }

        @Test
        @DisplayName("should throw AtmNotFoundException when no ATMs match combined filters")
        void shouldThrowAtmNotFoundExceptionWhenNoAtmsMatchCombinedFilters() {
            // No open interplanetary ATMs
            List<Atm> atms = List.of(
                    buildAtm("1", "Closed Interplanetary", false, true),
                    buildAtm("2", "Open Local", true, false)
            );
            when(atmRepository.findAll()).thenReturn(atms);
            AtmSearchRequest request = new AtmSearchRequest(true, true);

            assertThatThrownBy(() -> atmService.findAtms(request))
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
            @DisplayName("should return ATM details when valid existing ID is provided")
            void shouldReturnAtmDetailsWhenValidExistingIdProvided() {
                // Arrange
                Atm atm = buildAtmWithAllDetails(VALID_OBJECT_ID);
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atm));

                // Act
                AtmDetailsResponse result = atmService.findById(VALID_OBJECT_ID);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.isOpen()).isTrue();
                assertThat(result.atmHours()).isEqualTo("24 hours");
                assertThat(result.numberOfATMs()).isEqualTo(3);
                verify(atmRepository).findById(VALID_OBJECT_ID);
            }

            @Test
            @DisplayName("should correctly map coordinates to response")
            void shouldCorrectlyMapCoordinatesToResponse() {
                // Arrange
                Atm atm = buildAtmWithAllDetails(VALID_OBJECT_ID);
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atm));

                // Act
                AtmDetailsResponse result = atmService.findById(VALID_OBJECT_ID);

                // Assert
                assertThat(result.coordinates()).isNotNull();
                assertThat(result.coordinates().latitude()).isEqualTo(37.7749);
                assertThat(result.coordinates().longitude()).isEqualTo(-122.4194);
            }

            @Test
            @DisplayName("should correctly map timings to response")
            void shouldCorrectlyMapTimingsToResponse() {
                // Arrange
                Atm atm = buildAtmWithAllDetails(VALID_OBJECT_ID);
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atm));

                // Act
                AtmDetailsResponse result = atmService.findById(VALID_OBJECT_ID);

                // Assert
                assertThat(result.timings()).isNotNull();
                assertThat(result.timings().monFri()).isEqualTo("9:00 AM - 6:00 PM");
                assertThat(result.timings().satSun()).isEqualTo("10:00 AM - 4:00 PM");
                assertThat(result.timings().holidays()).isEqualTo("Closed");
            }

            @Test
            @DisplayName("should handle ATM with null coordinates")
            void shouldHandleAtmWithNullCoordinates() {
                // Arrange
                Atm atm = Atm.builder()
                        .id(VALID_OBJECT_ID)
                        .name("Test ATM")
                        .isOpenNow(true)
                        .atmHours("24 hours")
                        .numberOfATMs(2)
                        .timings(Timings.builder()
                                .monFri("9:00 AM - 5:00 PM")
                                .satSun("10:00 AM - 3:00 PM")
                                .build())
                        .build();
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atm));

                // Act
                AtmDetailsResponse result = atmService.findById(VALID_OBJECT_ID);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.coordinates()).isNull();
                assertThat(result.timings()).isNotNull();
            }

            @Test
            @DisplayName("should handle ATM with null timings")
            void shouldHandleAtmWithNullTimings() {
                // Arrange
                Atm atm = Atm.builder()
                        .id(VALID_OBJECT_ID)
                        .name("Test ATM")
                        .isOpenNow(false)
                        .atmHours("9:00 AM - 5:00 PM")
                        .numberOfATMs(1)
                        .coordinates(Coordinates.builder()
                                .latitude(40.7128)
                                .longitude(-74.0060)
                                .build())
                        .build();
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.of(atm));

                // Act
                AtmDetailsResponse result = atmService.findById(VALID_OBJECT_ID);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.timings()).isNull();
                assertThat(result.coordinates()).isNotNull();
            }
        }

        @Nested
        @DisplayName("when ID is valid but ATM does not exist")
        class ValidNonExistentIdTests {

            @Test
            @DisplayName("should throw AtmNotFoundException when ATM does not exist")
            void shouldThrowAtmNotFoundExceptionWhenAtmDoesNotExist() {
                // Arrange
                when(atmRepository.findById(VALID_OBJECT_ID)).thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> atmService.findById(VALID_OBJECT_ID))
                        .isInstanceOf(AtmNotFoundException.class)
                        .hasMessage("ATM information not found");
                verify(atmRepository).findById(VALID_OBJECT_ID);
            }

            @Test
            @DisplayName("should throw AtmNotFoundException with correct message for different valid IDs")
            void shouldThrowAtmNotFoundExceptionForDifferentValidIds() {
                // Arrange
                when(atmRepository.findById(ANOTHER_VALID_OBJECT_ID)).thenReturn(Optional.empty());

                // Act & Assert
                assertThatThrownBy(() -> atmService.findById(ANOTHER_VALID_OBJECT_ID))
                        .isInstanceOf(AtmNotFoundException.class)
                        .hasMessage("ATM information not found");
            }
        }

        @Nested
        @DisplayName("when ID format is invalid")
        class InvalidObjectIdFormatTests {

            @ParameterizedTest
            @ValueSource(strings = {
                    "invalid-id",
                    "123",
                    "xyz",
                    "507f1f77bcf86cd79943901",   // 23 chars (too short)
                    "507f1f77bcf86cd7994390111",  // 25 chars (too long)
                    "ZZZZZZZZZZZZZZZZZZZZZZZZ",   // 24 chars but invalid hex
                    "507f1f77bcf86cd79943901G",   // Contains non-hex character
                    "   507f1f77bcf86cd799439011", // Leading spaces
                    "507f1f77bcf86cd799439011   "  // Trailing spaces
            })
            @DisplayName("should throw InvalidObjectIdException for invalid ObjectId formats")
            void shouldThrowInvalidObjectIdExceptionForInvalidFormats(String invalidId) {
                // Act & Assert
                assertThatThrownBy(() -> atmService.findById(invalidId))
                        .isInstanceOf(InvalidObjectIdException.class)
                        .hasMessage("Resource not found");
                verify(atmRepository, never()).findById(invalidId);
            }

            @Test
            @DisplayName("should not call repository when ObjectId format is invalid")
            void shouldNotCallRepositoryWhenObjectIdFormatIsInvalid() {
                // Act & Assert
                assertThatThrownBy(() -> atmService.findById("invalid"))
                        .isInstanceOf(InvalidObjectIdException.class);
                verify(atmRepository, never()).findById("invalid");
            }
        }

        @Nested
        @DisplayName("when ID is null or empty")
        class NullAndEmptyIdTests {

            @Test
            @DisplayName("should throw IllegalArgumentException for null ID")
            void shouldThrowIllegalArgumentExceptionForNullId() {
                // Note: ObjectId.isValid(null) throws IllegalArgumentException
                // This is handled at the controller/framework level with @PathVariable validation
                // At service level, null input propagates the underlying MongoDB driver behavior
                assertThatThrownBy(() -> atmService.findById(null))
                        .isInstanceOf(IllegalArgumentException.class);
                verify(atmRepository, never()).findById(null);
            }

            @Test
            @DisplayName("should throw InvalidObjectIdException for empty ID")
            void shouldThrowInvalidObjectIdExceptionForEmptyId() {
                // Act & Assert
                assertThatThrownBy(() -> atmService.findById(""))
                        .isInstanceOf(InvalidObjectIdException.class)
                        .hasMessage("Resource not found");
                verify(atmRepository, never()).findById("");
            }

            @Test
            @DisplayName("should throw InvalidObjectIdException for whitespace-only ID")
            void shouldThrowInvalidObjectIdExceptionForWhitespaceOnlyId() {
                // Act & Assert
                assertThatThrownBy(() -> atmService.findById("   "))
                        .isInstanceOf(InvalidObjectIdException.class)
                        .hasMessage("Resource not found");
                verify(atmRepository, never()).findById("   ");
            }
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
