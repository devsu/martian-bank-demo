package com.martianbank.atmlocator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martianbank.atmlocator.dto.AtmResponse;
import com.martianbank.atmlocator.dto.AtmSearchRequest;
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
                    .andExpect(content().string("No ATMs found"));
        }

        @Test
        @DisplayName("should return 400 for malformed JSON request")
        void shouldReturn400ForMalformedJson() throws Exception {
            mockMvc.perform(post("/api/atm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.detail").value("Malformed JSON request"));
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
}
