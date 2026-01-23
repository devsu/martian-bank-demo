package com.martianbank.atmlocator.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO validation annotations using Jakarta Bean Validation.
 * Tests cover required field validation, range constraints, and nested field paths.
 */
class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("AtmCreateRequest validation")
    class AtmCreateRequestValidationTests {

        @Test
        @DisplayName("should have validation error when name is missing")
        void shouldHaveValidationErrorWhenNameIsMissing() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    null,  // name is null
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    37.7749,
                    -122.4194,
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    "Closed",
                    "24 hours",
                    2,
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("name") &&
                    v.getMessage().equals("ATM name is required"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should have validation error when name is blank or empty")
        void shouldHaveValidationErrorWhenNameIsBlankOrEmpty(String name) {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    name,
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    37.7749,
                    -122.4194,
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    "Closed",
                    "24 hours",
                    2,
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        }

        @Test
        @DisplayName("should have no validation errors when all required fields are valid")
        void shouldHaveNoValidationErrorsWhenAllRequiredFieldsAreValid() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    "Test ATM",
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    37.7749,
                    -122.4194,
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    "Closed",  // holidays is optional
                    "24 hours",
                    2,
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should have no validation errors when optional holidays field is null")
        void shouldHaveNoValidationErrorsWhenOptionalHolidaysIsNull() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    "Test ATM",
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    37.7749,
                    -122.4194,
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    null,  // holidays is optional
                    "24 hours",
                    2,
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should have validation error when latitude is null")
        void shouldHaveValidationErrorWhenLatitudeIsNull() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    "Test ATM",
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    null,  // latitude is null
                    -122.4194,
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    "Closed",
                    "24 hours",
                    2,
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("latitude") &&
                    v.getMessage().equals("Latitude is required"));
        }

        @Test
        @DisplayName("should have validation error when longitude is null")
        void shouldHaveValidationErrorWhenLongitudeIsNull() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    "Test ATM",
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    37.7749,
                    null,  // longitude is null
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    "Closed",
                    "24 hours",
                    2,
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("longitude") &&
                    v.getMessage().equals("Longitude is required"));
        }

        @Test
        @DisplayName("should have validation error when numberOfATMs is less than 1")
        void shouldHaveValidationErrorWhenNumberOfATMsIsLessThanOne() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    "Test ATM",
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102",
                    37.7749,
                    -122.4194,
                    "9:00 AM - 5:00 PM",
                    "10:00 AM - 3:00 PM",
                    "Closed",
                    "24 hours",
                    0,  // numberOfATMs is less than 1
                    true,
                    false
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("numberOfATMs") &&
                    v.getMessage().equals("Number of ATMs must be at least 1"));
        }

        @Test
        @DisplayName("should have multiple validation errors when multiple fields are invalid")
        void shouldHaveMultipleValidationErrorsWhenMultipleFieldsAreInvalid() {
            // Arrange
            AtmCreateRequest request = new AtmCreateRequest(
                    null,  // name is null
                    null,  // street is null
                    null,  // city is null
                    null,  // state is null
                    null,  // zip is null
                    null,  // latitude is null
                    null,  // longitude is null
                    null,  // monFri is null
                    null,  // satSun is null
                    null,  // holidays is optional
                    null,  // atmHours is null
                    null,  // numberOfATMs is null
                    null,  // isOpen is null
                    null   // interPlanetary is null
            );

            // Act
            Set<ConstraintViolation<AtmCreateRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).hasSize(13);  // All required fields except holidays
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("street"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("city"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("state"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("zip"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("latitude"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("longitude"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("monFri"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("satSun"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("atmHours"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("numberOfATMs"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("isOpen"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("interPlanetary"));
        }
    }

    @Nested
    @DisplayName("CoordinatesRequest validation")
    class CoordinatesRequestValidationTests {

        @Test
        @DisplayName("should have no validation errors when latitude and longitude are within valid range")
        void shouldHaveNoValidationErrorsWhenCoordinatesAreValid() {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(37.7749, -122.4194);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
                "-90.0, -180.0",    // Minimum valid values
                "90.0, 180.0",      // Maximum valid values
                "0.0, 0.0",         // Origin
                "-45.5, 120.75",    // Arbitrary valid values
                "89.999, 179.999"   // Near boundaries
        })
        @DisplayName("should have no validation errors for valid coordinate boundaries")
        void shouldHaveNoValidationErrorsForValidCoordinateBoundaries(double latitude, double longitude) {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(latitude, longitude);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(doubles = {-90.001, -100.0, -180.0, -1000.0})
        @DisplayName("should have validation error when latitude is below -90")
        void shouldHaveValidationErrorWhenLatitudeIsBelowMinimum(double latitude) {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(latitude, 0.0);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("latitude") &&
                    v.getMessage().equals("Latitude must be between -90 and 90"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {90.001, 100.0, 180.0, 1000.0})
        @DisplayName("should have validation error when latitude is above 90")
        void shouldHaveValidationErrorWhenLatitudeIsAboveMaximum(double latitude) {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(latitude, 0.0);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("latitude") &&
                    v.getMessage().equals("Latitude must be between -90 and 90"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {-180.001, -200.0, -360.0, -1000.0})
        @DisplayName("should have validation error when longitude is below -180")
        void shouldHaveValidationErrorWhenLongitudeIsBelowMinimum(double longitude) {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(0.0, longitude);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("longitude") &&
                    v.getMessage().equals("Longitude must be between -180 and 180"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {180.001, 200.0, 360.0, 1000.0})
        @DisplayName("should have validation error when longitude is above 180")
        void shouldHaveValidationErrorWhenLongitudeIsAboveMaximum(double longitude) {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(0.0, longitude);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("longitude") &&
                    v.getMessage().equals("Longitude must be between -180 and 180"));
        }

        @Test
        @DisplayName("should have validation error when latitude is null")
        void shouldHaveValidationErrorWhenLatitudeIsNull() {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(null, -122.4194);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("latitude") &&
                    v.getMessage().equals("Latitude is required"));
        }

        @Test
        @DisplayName("should have validation error when longitude is null")
        void shouldHaveValidationErrorWhenLongitudeIsNull() {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(37.7749, null);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("longitude") &&
                    v.getMessage().equals("Longitude is required"));
        }

        @Test
        @DisplayName("should have multiple validation errors when both latitude and longitude are invalid")
        void shouldHaveMultipleValidationErrorsWhenBothCoordinatesAreInvalid() {
            // Arrange
            CoordinatesRequest request = new CoordinatesRequest(100.0, -200.0);

            // Act
            Set<ConstraintViolation<CoordinatesRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).hasSize(2);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("latitude"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("longitude"));
        }
    }

    @Nested
    @DisplayName("AddressRequest validation")
    class AddressRequestValidationTests {

        @Test
        @DisplayName("should have no validation errors when all fields are valid")
        void shouldHaveNoValidationErrorsWhenAllFieldsAreValid() {
            // Arrange
            AddressRequest request = new AddressRequest(
                    "123 Main St",
                    "San Francisco",
                    "CA",
                    "94102"
            );

            // Act
            Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should have validation error when street is missing")
        void shouldHaveValidationErrorWhenStreetIsMissing() {
            // Arrange
            AddressRequest request = new AddressRequest(null, "San Francisco", "CA", "94102");

            // Act
            Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("street") &&
                    v.getMessage().equals("Street address is required"));
        }

        @Test
        @DisplayName("should have multiple validation errors when multiple fields are missing")
        void shouldHaveMultipleValidationErrorsWhenMultipleFieldsAreMissing() {
            // Arrange
            AddressRequest request = new AddressRequest(null, null, null, null);

            // Act
            Set<ConstraintViolation<AddressRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).hasSize(4);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("street"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("city"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("state"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("zip"));
        }
    }

    @Nested
    @DisplayName("LocationRequest nested validation")
    class LocationRequestNestedValidationTests {

        @Test
        @DisplayName("should have no validation errors when all nested fields are valid")
        void shouldHaveNoValidationErrorsWhenAllNestedFieldsAreValid() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(37.7749, -122.4194);
            AddressRequest address = new AddressRequest("123 Main St", "San Francisco", "CA", "94102");
            LocationRequest request = new LocationRequest(coordinates, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should have validation error when coordinates is null")
        void shouldHaveValidationErrorWhenCoordinatesIsNull() {
            // Arrange
            AddressRequest address = new AddressRequest("123 Main St", "San Francisco", "CA", "94102");
            LocationRequest request = new LocationRequest(null, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("coordinates") &&
                    v.getMessage().equals("Coordinates are required"));
        }

        @Test
        @DisplayName("should have validation error when address is null")
        void shouldHaveValidationErrorWhenAddressIsNull() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(37.7749, -122.4194);
            LocationRequest request = new LocationRequest(coordinates, null);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("address") &&
                    v.getMessage().equals("Address is required"));
        }

        @Test
        @DisplayName("should have nested field path in violation when coordinates latitude is invalid")
        void shouldHaveNestedFieldPathWhenCoordinatesLatitudeIsInvalid() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(100.0, -122.4194);  // Invalid latitude
            AddressRequest address = new AddressRequest("123 Main St", "San Francisco", "CA", "94102");
            LocationRequest request = new LocationRequest(coordinates, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("coordinates.latitude") &&
                    v.getMessage().equals("Latitude must be between -90 and 90"));
        }

        @Test
        @DisplayName("should have nested field path in violation when coordinates longitude is invalid")
        void shouldHaveNestedFieldPathWhenCoordinatesLongitudeIsInvalid() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(37.7749, -200.0);  // Invalid longitude
            AddressRequest address = new AddressRequest("123 Main St", "San Francisco", "CA", "94102");
            LocationRequest request = new LocationRequest(coordinates, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("coordinates.longitude") &&
                    v.getMessage().equals("Longitude must be between -180 and 180"));
        }

        @Test
        @DisplayName("should have nested field path in violation when address city is missing")
        void shouldHaveNestedFieldPathWhenAddressCityIsMissing() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(37.7749, -122.4194);
            AddressRequest address = new AddressRequest("123 Main St", null, "CA", "94102");  // City is null
            LocationRequest request = new LocationRequest(coordinates, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v ->
                    v.getPropertyPath().toString().equals("address.city") &&
                    v.getMessage().equals("City is required"));
        }

        @Test
        @DisplayName("should have multiple nested field paths in violations when multiple nested fields are invalid")
        void shouldHaveMultipleNestedFieldPathsWhenMultipleNestedFieldsAreInvalid() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(100.0, -200.0);  // Both invalid
            AddressRequest address = new AddressRequest(null, null, "CA", "94102");  // Street and city missing
            LocationRequest request = new LocationRequest(coordinates, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).hasSize(4);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("coordinates.latitude"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("coordinates.longitude"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address.street"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address.city"));
        }

        @Test
        @DisplayName("should correctly report nested field paths for all nested object violations")
        void shouldCorrectlyReportNestedFieldPathsForAllViolations() {
            // Arrange
            CoordinatesRequest coordinates = new CoordinatesRequest(null, null);  // Both null
            AddressRequest address = new AddressRequest(null, null, null, null);  // All null
            LocationRequest request = new LocationRequest(coordinates, address);

            // Act
            Set<ConstraintViolation<LocationRequest>> violations = validator.validate(request);

            // Assert - verify nested paths are correctly formed
            assertThat(violations).hasSize(6);

            // Verify coordinates nested paths
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("coordinates.latitude"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("coordinates.longitude"));

            // Verify address nested paths
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address.street"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address.city"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address.state"));
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("address.zip"));
        }
    }
}
