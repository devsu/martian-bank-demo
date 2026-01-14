package com.martianbank.loan.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void constructorSetsFields() {
        LoanResponseDto dto = new LoanResponseDto(true, "Loan Approved");

        assertTrue(dto.isApproved());
        assertEquals("Loan Approved", dto.getMessage());
    }

    @Test
    void defaultConstructorCreatesEmptyObject() {
        LoanResponseDto dto = new LoanResponseDto();

        assertFalse(dto.isApproved());
        assertNull(dto.getMessage());
    }

    @Test
    void serializesToJsonWithCorrectFieldNames() throws Exception {
        LoanResponseDto dto = new LoanResponseDto(true, "Success");

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"approved\":true"));
        assertTrue(json.contains("\"message\":\"Success\""));
    }

    @Test
    void deserializesFromJson() throws Exception {
        String json = "{\"approved\":false,\"message\":\"Loan Rejected\"}";

        LoanResponseDto dto = objectMapper.readValue(json, LoanResponseDto.class);

        assertFalse(dto.isApproved());
        assertEquals("Loan Rejected", dto.getMessage());
    }
}
