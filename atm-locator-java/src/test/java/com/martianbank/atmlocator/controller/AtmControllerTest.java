package com.martianbank.atmlocator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.dto.AtmCreateRequest;
import com.martianbank.atmlocator.dto.AtmFullResponse;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.service.AtmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockitoBean
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
    @DisplayName("POST /api/atm/add - Flat Structure Validation Tests")
    class AddAtmValidationTests {

        @Test
        @DisplayName("should return 400 with validation errors when required fields are missing")
        void shouldReturn400WhenRequiredFieldsMissing() throws Exception {
            String requestWithMissingFields = "{}";

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingFields))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.name").value("ATM name is required"))
                    .andExpect(jsonPath("$.errors.street").value("Street address is required"))
                    .andExpect(jsonPath("$.errors.city").value("City is required"))
                    .andExpect(jsonPath("$.errors.state").value("State is required"))
                    .andExpect(jsonPath("$.errors.zip").value("ZIP code is required"))
                    .andExpect(jsonPath("$.errors.latitude").value("Latitude is required"))
                    .andExpect(jsonPath("$.errors.longitude").value("Longitude is required"))
                    .andExpect(jsonPath("$.errors.monFri").value("Monday-Friday hours are required"))
                    .andExpect(jsonPath("$.errors.satSun").value("Saturday-Sunday hours are required"))
                    .andExpect(jsonPath("$.errors.atmHours").value("ATM hours are required"))
                    .andExpect(jsonPath("$.errors.numberOfATMs").value("Number of ATMs is required"))
                    .andExpect(jsonPath("$.errors.isOpen").value("Open status is required"))
                    .andExpect(jsonPath("$.errors.interPlanetary").value("Interplanetary status is required"));
        }

        @Test
        @DisplayName("should return 400 when latitude is missing")
        void shouldReturn400WhenLatitudeMissing() throws Exception {
            String requestWithMissingLatitude = """
                    {
                        "name": "Test ATM",
                        "street": "123 Main St",
                        "city": "San Francisco",
                        "state": "CA",
                        "zip": "94102",
                        "longitude": -122.4194,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "atmHours": "24 hours",
                        "numberOfATMs": 2,
                        "isOpen": true,
                        "interPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingLatitude))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.latitude").value("Latitude is required"));
        }

        @Test
        @DisplayName("should accept interplanetary coordinates (latitude outside Earth range)")
        void shouldAcceptInterplanetaryLatitude() throws Exception {
            String requestWithMarsLatitude = """
                    {
                        "name": "Mars ATM",
                        "street": "123 Red Planet Ave",
                        "city": "Olympus City",
                        "state": "Tharsis",
                        "zip": "00001",
                        "latitude": -18.65,
                        "longitude": -226.2,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "atmHours": "24 hours",
                        "numberOfATMs": 2,
                        "isOpen": true,
                        "interPlanetary": true
                    }
                    """;

            AtmFullResponse mockResponse = new AtmFullResponse(
                    "507f1f77bcf86cd799439011",
                    "Mars ATM",
                    null, null, null,
                    "24 hours",
                    2,
                    true,
                    true,
                    null, null, 0
            );
            when(atmService.create(any())).thenReturn(mockResponse);

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMarsLatitude))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$._id").value("507f1f77bcf86cd799439011"))
                    .andExpect(jsonPath("$.name").value("Mars ATM"));
        }

        @Test
        @DisplayName("should return 400 when city is missing")
        void shouldReturn400WhenCityMissing() throws Exception {
            String requestWithMissingCity = """
                    {
                        "name": "Test ATM",
                        "street": "123 Main St",
                        "state": "CA",
                        "zip": "94102",
                        "latitude": 37.7749,
                        "longitude": -122.4194,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "atmHours": "24 hours",
                        "numberOfATMs": 2,
                        "isOpen": true,
                        "interPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMissingCity))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.city").value("City is required"));
        }

        @Test
        @DisplayName("should return 400 when numberOfATMs is less than 1")
        void shouldReturn400WhenNumberOfAtmsLessThanOne() throws Exception {
            String requestWithInvalidNumberOfATMs = """
                    {
                        "name": "Test ATM",
                        "street": "123 Main St",
                        "city": "San Francisco",
                        "state": "CA",
                        "zip": "94102",
                        "latitude": 37.7749,
                        "longitude": -122.4194,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "atmHours": "24 hours",
                        "numberOfATMs": 0,
                        "isOpen": true,
                        "interPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithInvalidNumberOfATMs))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.numberOfATMs").value("Number of ATMs must be at least 1"));
        }

        @Test
        @DisplayName("should return 400 with multiple errors when multiple fields are invalid")
        void shouldReturn400WithMultipleErrorsWhenMultipleFieldsInvalid() throws Exception {
            String requestWithMultipleErrors = """
                    {
                        "name": "",
                        "street": "",
                        "city": "",
                        "state": "",
                        "zip": "",
                        "latitude": 100.0,
                        "longitude": -200.0,
                        "monFri": "",
                        "satSun": "",
                        "atmHours": "",
                        "numberOfATMs": 0,
                        "isOpen": true,
                        "interPlanetary": false
                    }
                    """;

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestWithMultipleErrors))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.name").value("ATM name is required"))
                    .andExpect(jsonPath("$.errors.street").value("Street address is required"))
                    .andExpect(jsonPath("$.errors.city").value("City is required"))
                    .andExpect(jsonPath("$.errors.state").value("State is required"))
                    .andExpect(jsonPath("$.errors.zip").value("ZIP code is required"))
                    .andExpect(jsonPath("$.errors.monFri").value("Monday-Friday hours are required"))
                    .andExpect(jsonPath("$.errors.satSun").value("Saturday-Sunday hours are required"))
                    .andExpect(jsonPath("$.errors.atmHours").value("ATM hours are required"))
                    .andExpect(jsonPath("$.errors.numberOfATMs").value("Number of ATMs must be at least 1"));
        }

        @Test
        @DisplayName("should return 400 with correct response format containing message and errors")
        void shouldReturn400WithCorrectResponseFormat() throws Exception {
            String requestWithMissingName = """
                    {
                        "name": "",
                        "street": "123 Main St",
                        "city": "San Francisco",
                        "state": "CA",
                        "zip": "94102",
                        "latitude": 37.7749,
                        "longitude": -122.4194,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "atmHours": "24 hours",
                        "numberOfATMs": 2,
                        "isOpen": true,
                        "interPlanetary": false
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

        @Test
        @DisplayName("should return 201 when all required fields are provided with valid values")
        void shouldReturn201WhenValidRequest() throws Exception {
            String validRequest = """
                    {
                        "name": "Test ATM",
                        "street": "123 Main St",
                        "city": "San Francisco",
                        "state": "CA",
                        "zip": "94102",
                        "latitude": 37.7749,
                        "longitude": -122.4194,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "holidays": "Closed",
                        "atmHours": "24 hours",
                        "numberOfATMs": 2,
                        "isOpen": true,
                        "interPlanetary": false
                    }
                    """;

            AtmFullResponse mockResponse = new AtmFullResponse(
                    "507f1f77bcf86cd799439011",
                    "Test ATM",
                    null, null, null,
                    "24 hours",
                    2,
                    true,
                    false,
                    null, null, 0
            );
            when(atmService.create(any())).thenReturn(mockResponse);

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$._id").value("507f1f77bcf86cd799439011"))
                    .andExpect(jsonPath("$.name").value("Test ATM"));
        }

        @Test
        @DisplayName("should accept request without optional holidays field")
        void shouldAcceptRequestWithoutOptionalHolidays() throws Exception {
            String validRequestWithoutHolidays = """
                    {
                        "name": "Test ATM",
                        "street": "123 Main St",
                        "city": "San Francisco",
                        "state": "CA",
                        "zip": "94102",
                        "latitude": 37.7749,
                        "longitude": -122.4194,
                        "monFri": "9:00 AM - 5:00 PM",
                        "satSun": "10:00 AM - 3:00 PM",
                        "atmHours": "24 hours",
                        "numberOfATMs": 2,
                        "isOpen": true,
                        "interPlanetary": false
                    }
                    """;

            AtmFullResponse mockResponse = new AtmFullResponse(
                    "507f1f77bcf86cd799439011",
                    "Test ATM",
                    null, null, null,
                    "24 hours",
                    2,
                    true,
                    false,
                    null, null, 0
            );
            when(atmService.create(any())).thenReturn(mockResponse);

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestWithoutHolidays))
                    .andExpect(status().isCreated());
        }
    }
}
