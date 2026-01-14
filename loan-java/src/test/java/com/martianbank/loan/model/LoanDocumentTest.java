package com.martianbank.loan.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanDocumentTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void timestampReturnsStringFormat() {
        LoanDocument doc = new LoanDocument();
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        doc.setTimestampDate(timestamp);

        assertEquals("2024-01-15T10:30", doc.getTimestamp());
    }

    @Test
    void timestampReturnsNullWhenNotSet() {
        LoanDocument doc = new LoanDocument();

        assertNull(doc.getTimestamp());
    }

    @Test
    void allFieldsSerializeCorrectly() throws Exception {
        LoanDocument doc = new LoanDocument();
        doc.setName("John");
        doc.setEmail("john@test.com");
        doc.setAccountType("savings");
        doc.setAccountNumber("12345");
        doc.setGovtIdType("passport");
        doc.setGovtIdNumber("ABC123");
        doc.setLoanType("personal");
        doc.setLoanAmount(5000.0);
        doc.setInterestRate(5.5);
        doc.setTimePeriod("12 months");
        doc.setStatus("Approved");
        doc.setTimestampDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        String json = objectMapper.writeValueAsString(doc);

        assertTrue(json.contains("\"account_type\":\"savings\""));
        assertTrue(json.contains("\"account_number\":\"12345\""));
        assertTrue(json.contains("\"status\":\"Approved\""));
        assertTrue(json.contains("\"timestamp\":\"2024-01-15T10:30\""));
    }
}
