package com.martianbank.loan.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesToJsonWithCorrectFieldNames() throws Exception {
        LoanRequestDto dto = new LoanRequestDto();
        dto.setName("John Doe");
        dto.setEmail("john@test.com");
        dto.setAccountType("savings");
        dto.setAccountNumber("12345");
        dto.setGovtIdType("passport");
        dto.setGovtIdNumber("ABC123");
        dto.setLoanType("personal");
        dto.setLoanAmount(5000.0);
        dto.setInterestRate(5.5);
        dto.setTimePeriod("12 months");

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"name\":\"John Doe\""));
        assertTrue(json.contains("\"email\":\"john@test.com\""));
        assertTrue(json.contains("\"account_type\":\"savings\""));
        assertTrue(json.contains("\"account_number\":\"12345\""));
        assertTrue(json.contains("\"govt_id_type\":\"passport\""));
        assertTrue(json.contains("\"govt_id_number\":\"ABC123\""));
        assertTrue(json.contains("\"loan_type\":\"personal\""));
        assertTrue(json.contains("\"loan_amount\":5000.0"));
        assertTrue(json.contains("\"interest_rate\":5.5"));
        assertTrue(json.contains("\"time_period\":\"12 months\""));
    }

    @Test
    void deserializesFromJsonWithSnakeCaseFields() throws Exception {
        String json = """
            {
                "name": "Jane Doe",
                "email": "jane@test.com",
                "account_type": "checking",
                "account_number": "67890",
                "govt_id_type": "license",
                "govt_id_number": "XYZ789",
                "loan_type": "mortgage",
                "loan_amount": 10000.0,
                "interest_rate": 4.5,
                "time_period": "24 months"
            }
            """;

        LoanRequestDto dto = objectMapper.readValue(json, LoanRequestDto.class);

        assertEquals("Jane Doe", dto.getName());
        assertEquals("jane@test.com", dto.getEmail());
        assertEquals("checking", dto.getAccountType());
        assertEquals("67890", dto.getAccountNumber());
        assertEquals("license", dto.getGovtIdType());
        assertEquals("XYZ789", dto.getGovtIdNumber());
        assertEquals("mortgage", dto.getLoanType());
        assertEquals(10000.0, dto.getLoanAmount());
        assertEquals(4.5, dto.getInterestRate());
        assertEquals("24 months", dto.getTimePeriod());
    }

    @Test
    void gettersAndSettersWorkCorrectly() {
        LoanRequestDto dto = new LoanRequestDto();

        dto.setName("Test Name");
        assertEquals("Test Name", dto.getName());

        dto.setEmail("test@email.com");
        assertEquals("test@email.com", dto.getEmail());

        dto.setLoanAmount(1000.50);
        assertEquals(1000.50, dto.getLoanAmount());
    }
}
