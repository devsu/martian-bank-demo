package com.martianbank.atmlocator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.dto.AddressRequest;
import com.martianbank.atmlocator.dto.AtmCreateRequest;
import com.martianbank.atmlocator.dto.AtmFullResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.dto.CoordinatesRequest;
import com.martianbank.atmlocator.dto.LocationRequest;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.service.AtmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AtmController verifying HTTP response codes and bodies.
 */
@WebMvcTest(AtmController.class)
class AtmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AtmService atmService;

    @Nested
    @DisplayName("POST /api/atm")
    class PostAtmEndpointTests {

        @Test
        @DisplayName("should return 200 with ATM list when ATMs are found")
        void shouldReturn200WhenAtmsFound() throws Exception {
            List<AtmResponse> atms = List.of(
                    new AtmResponse("1", "Test ATM", null, null, true)
            );
            when(atmService.findAtms(any())).thenReturn(atms);

            mockMvc.perform(post("/api/atm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]._id").value("1"))
                    .andExpect(jsonPath("$[0].name").value("Test ATM"));
        }

        @Test
        @DisplayName("should return 404 when AtmNotFoundException is thrown")
        void shouldReturn404WhenNoAtmsFound() throws Exception {
            when(atmService.findAtms(any())).thenThrow(new AtmNotFoundException());

            mockMvc.perform(post("/api/atm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("No ATMs found"))
                    .andExpect(jsonPath("$.stack").isEmpty());
        }

        @Test
        @DisplayName("should return 400 for malformed JSON request")
        void shouldReturn400ForMalformedJson() throws Exception {
            mockMvc.perform(post("/api/atm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Malformed JSON request"))
                    .andExpect(jsonPath("$.stack").isEmpty());
        }

        @Test
        @DisplayName("should accept request with valid filter parameters")
        void shouldAcceptValidFilterParameters() throws Exception {
            List<AtmResponse> atms = List.of(
                    new AtmResponse("1", "Open ATM", null, null, true)
            );
            when(atmService.findAtms(any())).thenReturn(atms);

            AtmSearchRequest request = new AtmSearchRequest(true, false);

            mockMvc.perform(post("/api/atm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].isOpen").value(true));
        }

        @Test
        @DisplayName("should accept request without body")
        void shouldAcceptRequestWithoutBody() throws Exception {
            List<AtmResponse> atms = List.of(
                    new AtmResponse("1", "Default ATM", null, null, false)
            );
            when(atmService.findAtms(any())).thenReturn(atms);

            mockMvc.perform(post("/api/atm")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /api/atm/add - Nested Validation Tests")
    class AddAtmNestedValidationTests {

        @Test
        @DisplayName("should return 400 with validation errors when required top-level fields are missing")
        void shouldReturn400WhenRequiredTopLevelFieldsMissing() throws Exception {
            String requestWithMissingFields = "{}";

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingFields))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.name").value("ATM name is required"))
                    .andExpect(jsonPath("$.errors.location").value("Location information is required"))
                    .andExpect(jsonPath("$.errors.isOpenNow").value("Open status is required"))
                    .andExpect(jsonPath("$.errors.isInterPlanetary").value("Interplanetary status is required"));
        }

        @Test
        @DisplayName("should return 400 with nested field path when coordinates are missing")
        void shouldReturn400WithNestedFieldPathWhenCoordinatesMissing() throws Exception {
            String requestWithMissingCoordinates = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingCoordinates))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.coordinates']").value("Coordinates are required"));
        }

        @Test
        @DisplayName("should return 400 with nested field path when address is missing")
        void shouldReturn400WithNestedFieldPathWhenAddressMissing() throws Exception {
            String requestWithMissingAddress = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "latitude": 37.7749,
                                "longitude": -122.4194
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingAddress))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.address']").value("Address is required"));
        }

        @Test
        @DisplayName("should return 400 with deeply nested field path when latitude is missing")
        void shouldReturn400WithDeeplyNestedFieldPathWhenLatitudeMissing() throws Exception {
            String requestWithMissingLatitude = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "longitude": -122.4194
                            },
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingLatitude))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.coordinates.latitude']").value("Latitude is required"));
        }

        @Test
        @DisplayName("should return 400 with descriptive message when latitude is below minimum")
        void shouldReturn400WithDescriptiveMessageWhenLatitudeBelowMinimum() throws Exception {
            String requestWithInvalidLatitude = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "latitude": -91.0,
                                "longitude": -122.4194
                            },
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithInvalidLatitude))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.coordinates.latitude']").value("Latitude must be between -90 and 90"));
        }

        @Test
        @DisplayName("should return 400 with descriptive message when latitude is above maximum")
        void shouldReturn400WithDescriptiveMessageWhenLatitudeAboveMaximum() throws Exception {
            String requestWithInvalidLatitude = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "latitude": 91.0,
                                "longitude": -122.4194
                            },
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithInvalidLatitude))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.coordinates.latitude']").value("Latitude must be between -90 and 90"));
        }

        @Test
        @DisplayName("should return 400 with descriptive message when longitude is below minimum")
        void shouldReturn400WithDescriptiveMessageWhenLongitudeBelowMinimum() throws Exception {
            String requestWithInvalidLongitude = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "latitude": 37.7749,
                                "longitude": -181.0
                            },
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithInvalidLongitude))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.coordinates.longitude']").value("Longitude must be between -180 and 180"));
        }

        @Test
        @DisplayName("should return 400 with descriptive message when longitude is above maximum")
        void shouldReturn400WithDescriptiveMessageWhenLongitudeAboveMaximum() throws Exception {
            String requestWithInvalidLongitude = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "latitude": 37.7749,
                                "longitude": 181.0
                            },
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithInvalidLongitude))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.coordinates.longitude']").value("Longitude must be between -180 and 180"));
        }

        @Test
        @DisplayName("should return 400 with nested field path when city is missing in address")
        void shouldReturn400WithNestedFieldPathWhenCityMissing() throws Exception {
            String requestWithMissingCity = """
                    {
                        "name": "Test ATM",
                        "location": {
                            "coordinates": {
                                "latitude": 37.7749,
                                "longitude": -122.4194
                            },
                            "address": {
                                "street": "123 Main St",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingCity))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors['location.address.city']").value("City is required"));
        }

        @Test
        @DisplayName("should return 400 with multiple nested errors when multiple fields are invalid")
        void shouldReturn400WithMultipleNestedErrorsWhenMultipleFieldsInvalid() throws Exception {
            String requestWithMultipleErrors = """
                    {
                        "name": "",
                        "location": {
                            "coordinates": {
                                "latitude": 100.0,
                                "longitude": -200.0
                            },
                            "address": {
                                "street": "",
                                "city": "",
                                "state": "",
                                "zip": ""
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMultipleErrors))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.name").value("ATM name is required"))
                    .andExpect(jsonPath("$.errors['location.coordinates.latitude']").value("Latitude must be between -90 and 90"))
                    .andExpect(jsonPath("$.errors['location.coordinates.longitude']").value("Longitude must be between -180 and 180"))
                    .andExpect(jsonPath("$.errors['location.address.street']").value("Street address is required"))
                    .andExpect(jsonPath("$.errors['location.address.city']").value("City is required"))
                    .andExpect(jsonPath("$.errors['location.address.state']").value("State is required"))
                    .andExpect(jsonPath("$.errors['location.address.zip']").value("ZIP code is required"));
        }

        @Test
        @DisplayName("should return 400 with correct response format containing message and errors")
        void shouldReturn400WithCorrectResponseFormat() throws Exception {
            String requestWithMissingName = """
                    {
                        "name": "",
                        "location": {
                            "coordinates": {
                                "latitude": 37.7749,
                                "longitude": -122.4194
                            },
                            "address": {
                                "street": "123 Main St",
                                "city": "San Francisco",
                                "state": "CA",
                                "zip": "94102"
                            }
                        },
                        "isOpenNow": true,
                        "isInterPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingName))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors").isMap());
        }
    }
}
