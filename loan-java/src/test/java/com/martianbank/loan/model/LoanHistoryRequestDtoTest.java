package com.martianbank.loan.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanHistoryRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesToJson() throws Exception {
        LoanHistoryRequestDto dto = new LoanHistoryRequestDto();
        dto.setEmail("test@example.com");

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"email\":\"test@example.com\""));
    }

    @Test
    void deserializesFromJson() throws Exception {
        String json = "{\"email\":\"user@test.com\"}";

        LoanHistoryRequestDto dto = objectMapper.readValue(json, LoanHistoryRequestDto.class);

        assertEquals("user@test.com", dto.getEmail());
    }

    @Test
    void getterAndSetterWork() {
        LoanHistoryRequestDto dto = new LoanHistoryRequestDto();
        dto.setEmail("new@email.com");

        assertEquals("new@email.com", dto.getEmail());
    }
}
