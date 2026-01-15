/**
 * Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package com.martianbank.atmlocator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.dto.*;
import com.martianbank.atmlocator.exception.AtmNotFoundException;
import com.martianbank.atmlocator.exception.GlobalExceptionHandler;
import com.martianbank.atmlocator.model.Atm;
import com.martianbank.atmlocator.service.AtmService;
import com.martianbank.atmlocator.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AtmControllerTest {

    @Mock
    private AtmService atmService;

    @InjectMocks
    private AtmController atmController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        // Set production mode to hide stack traces
        ReflectionTestUtils.setField(exceptionHandler, "nodeEnv", "production");
        mockMvc = MockMvcBuilders.standaloneSetup(atmController)
                .setControllerAdvice(exceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    class GetATMsEndpointTests {

        @Test
        void getATMs_withValidRequest_returns200() throws Exception {
            // Given
            List<AtmListResponse> atms = List.of(
                    AtmListResponse.fromEntity(TestDataFactory.createTestAtm())
            );
            when(atmService.getATMs(any())).thenReturn(atms);

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"isOpenNow\": true}"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        void getATMs_withEmptyBody_returns200() throws Exception {
            // Given
            List<AtmListResponse> atms = List.of(
                    AtmListResponse.fromEntity(TestDataFactory.createTestAtm())
            );
            when(atmService.getATMs(any())).thenReturn(atms);

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }

        @Test
        void getATMs_withNullBody_returns200() throws Exception {
            // Given
            List<AtmListResponse> atms = List.of(
                    AtmListResponse.fromEntity(TestDataFactory.createTestAtm())
            );
            when(atmService.getATMs(any())).thenReturn(atms);

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        void getATMs_responseContainsCorrectFields() throws Exception {
            // Given
            AtmListResponse response = AtmListResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getATMs(any())).thenReturn(List.of(response));

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(jsonPath("$[0]._id").exists())
                    .andExpect(jsonPath("$[0].name").exists())
                    .andExpect(jsonPath("$[0].coordinates").exists())
                    .andExpect(jsonPath("$[0].coordinates.latitude").exists())
                    .andExpect(jsonPath("$[0].coordinates.longitude").exists())
                    .andExpect(jsonPath("$[0].address").exists())
                    .andExpect(jsonPath("$[0].address.street").exists())
                    .andExpect(jsonPath("$[0].isOpen").exists());
        }

        @Test
        void getATMs_responseExcludesTimingsField() throws Exception {
            // Given
            AtmListResponse response = AtmListResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getATMs(any())).thenReturn(List.of(response));

            // When/Then
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(jsonPath("$[0].timings").doesNotExist())
                    .andExpect(jsonPath("$[0].atmHours").doesNotExist())
                    .andExpect(jsonPath("$[0].numberOfATMs").doesNotExist())
                    .andExpect(jsonPath("$[0].interPlanetary").doesNotExist());
        }

        @Test
        void getATMs_whenNoResults_returns404WithPlainString() throws Exception {
            // Given
            when(atmService.getATMs(any()))
                    .thenThrow(new AtmNotFoundException("No results found"));

            // When/Then
            // Note: Controller catches the exception and returns plain string "No ATMs found"
            // This matches legacy behavior: res.status(404).json("No ATMs found")
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("No ATMs found"));
        }

        @Test
        void getATMs_passesFilterToService() throws Exception {
            // Given
            when(atmService.getATMs(any())).thenReturn(List.of());

            // When
            mockMvc.perform(post("/api/atm/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"isOpenNow\": true, \"isInterPlanetary\": true}"));

            // Then
            verify(atmService).getATMs(any(AtmFilterRequest.class));
        }

        @Test
        void getATMs_withTrailingSlash_works() throws Exception {
            // Given
            when(atmService.getATMs(any())).thenReturn(List.of());

            // When/Then - both with and without trailing slash should work
            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class AddATMEndpointTests {

        @Test
        void addATM_withValidRequest_returns201() throws Exception {
            // Given
            Atm createdAtm = TestDataFactory.createTestAtm();
            when(atmService.addATM(any())).thenReturn(createdAtm);

            String requestBody = createValidAtmRequest();

            // When/Then
            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void addATM_responseContainsAllFields() throws Exception {
            // Given
            Atm createdAtm = TestDataFactory.createTestAtm();
            when(atmService.addATM(any())).thenReturn(createdAtm);

            String requestBody = createValidAtmRequest();

            // When/Then
            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.address").exists())
                    .andExpect(jsonPath("$.coordinates").exists())
                    .andExpect(jsonPath("$.timings").exists())
                    .andExpect(jsonPath("$.atmHours").exists())
                    .andExpect(jsonPath("$.numberOfATMs").exists())
                    .andExpect(jsonPath("$.isOpen").exists())
                    .andExpect(jsonPath("$.interPlanetary").exists());
        }

        @Test
        void addATM_whenServiceFails_returns404() throws Exception {
            // Given
            when(atmService.addATM(any()))
                    .thenThrow(new AtmNotFoundException("Could not create ATM"));

            String requestBody = createValidAtmRequest();

            // When/Then
            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Could not create ATM"));
        }

        private String createValidAtmRequest() {
            return """
                {
                    "name": "Test ATM",
                    "street": "123 Test St",
                    "city": "Test City",
                    "state": "Test State",
                    "zip": "12345",
                    "latitude": 37.775,
                    "longitude": -81.188,
                    "monFri": "9-5",
                    "satSun": "10-3",
                    "holidays": "Closed",
                    "atmHours": "24 hours",
                    "numberOfATMs": 2,
                    "isOpen": true,
                    "interPlanetary": false
                }
                """;
        }
    }

    @Nested
    class GetSpecificATMEndpointTests {

        @Test
        void getSpecificATM_withValidId_returns200() throws Exception {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getSpecificATM(id)).thenReturn(response);

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void getSpecificATM_responseContainsCorrectFields() throws Exception {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getSpecificATM(id)).thenReturn(response);

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(jsonPath("$.coordinates").exists())
                    .andExpect(jsonPath("$.coordinates.latitude").exists())
                    .andExpect(jsonPath("$.coordinates.longitude").exists())
                    .andExpect(jsonPath("$.timings").exists())
                    .andExpect(jsonPath("$.timings.monFri").exists())
                    .andExpect(jsonPath("$.timings.satSun").exists())
                    .andExpect(jsonPath("$.atmHours").exists())
                    .andExpect(jsonPath("$.numberOfATMs").exists())
                    .andExpect(jsonPath("$.isOpen").exists());
        }

        @Test
        void getSpecificATM_responseExcludesIdNameAddress() throws Exception {
            // Given
            String id = TestDataFactory.TEST_ATM_ID;
            AtmDetailResponse response = AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm());
            when(atmService.getSpecificATM(id)).thenReturn(response);

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(jsonPath("$._id").doesNotExist())
                    .andExpect(jsonPath("$.id").doesNotExist())
                    .andExpect(jsonPath("$.name").doesNotExist())
                    .andExpect(jsonPath("$.address").doesNotExist())
                    .andExpect(jsonPath("$.interPlanetary").doesNotExist());
        }

        @Test
        void getSpecificATM_whenNotFound_returns404() throws Exception {
            // Given
            String id = "nonexistent-id";
            when(atmService.getSpecificATM(id))
                    .thenThrow(new AtmNotFoundException("ATM not found"));

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("ATM information not found"));
        }

        @Test
        void getSpecificATM_errorResponseHasStackFieldNull() throws Exception {
            // Given
            String id = "nonexistent-id";
            when(atmService.getSpecificATM(id))
                    .thenThrow(new AtmNotFoundException("ATM not found"));

            // When/Then
            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(jsonPath("$.stack").value(nullValue()));
        }
    }

    @Nested
    class EndpointPathTests {

        @Test
        void getATMs_mappedToCorrectPath() throws Exception {
            when(atmService.getATMs(any())).thenReturn(List.of());

            mockMvc.perform(post("/api/atm/")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        void addATM_mappedToCorrectPath() throws Exception {
            when(atmService.addATM(any())).thenReturn(TestDataFactory.createTestAtm());

            mockMvc.perform(post("/api/atm/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void getSpecificATM_mappedToCorrectPath() throws Exception {
            String id = "test-id";
            when(atmService.getSpecificATM(id))
                    .thenReturn(AtmDetailResponse.fromEntity(TestDataFactory.createTestAtm()));

            mockMvc.perform(get("/api/atm/{id}", id))
                    .andExpect(status().isOk());
        }
    }
}
