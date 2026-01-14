package com.martianbank.loan.resource;

import com.martianbank.loan.model.*;
import com.martianbank.loan.service.LoanService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class LoanResourceTest {

    @InjectMock
    LoanService loanService;

    @Test
    void processLoanRequest_returnsApprovedResponse() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(true, "Loan Approved"));

        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "John Doe",
                    "email": "john@test.com",
                    "account_type": "savings",
                    "account_number": "12345",
                    "govt_id_type": "passport",
                    "govt_id_number": "ABC123",
                    "loan_type": "personal",
                    "loan_amount": 5000,
                    "interest_rate": 5.5,
                    "time_period": "12 months"
                }
                """)
        .when()
            .post("/loan/request")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("approved", is(true))
            .body("message", equalTo("Loan Approved"));
    }

    @Test
    void processLoanRequest_returnsRejectedResponse() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(false, "Email or Account number not found."));

        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Jane Doe",
                    "email": "jane@invalid.com",
                    "account_type": "savings",
                    "account_number": "99999",
                    "govt_id_type": "passport",
                    "govt_id_number": "XYZ789",
                    "loan_type": "personal",
                    "loan_amount": 1000,
                    "interest_rate": 4.5,
                    "time_period": "6 months"
                }
                """)
        .when()
            .post("/loan/request")
        .then()
            .statusCode(200)
            .body("approved", is(false))
            .body("message", equalTo("Email or Account number not found."));
    }

    @Test
    void processLoanRequest_callsServiceWithCorrectDto() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(true, "Loan Approved"));

        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "account_type": "checking",
                    "account_number": "67890",
                    "govt_id_type": "license",
                    "govt_id_number": "DRV456",
                    "loan_type": "auto",
                    "loan_amount": 15000,
                    "interest_rate": 3.9,
                    "time_period": "36 months"
                }
                """)
        .when()
            .post("/loan/request")
        .then()
            .statusCode(200);

        verify(loanService).processLoanRequest(argThat(dto ->
            "Test User".equals(dto.getName()) &&
            "test@example.com".equals(dto.getEmail()) &&
            "checking".equals(dto.getAccountType()) &&
            "67890".equals(dto.getAccountNumber()) &&
            dto.getLoanAmount() == 15000.0
        ));
    }

    @Test
    void getLoanHistory_returnsLoansArray() {
        LoanDocument loan1 = new LoanDocument();
        loan1.setName("John Doe");
        loan1.setEmail("john@test.com");
        loan1.setAccountType("savings");
        loan1.setAccountNumber("12345");
        loan1.setGovtIdType("passport");
        loan1.setGovtIdNumber("ABC123");
        loan1.setLoanType("personal");
        loan1.setLoanAmount(5000.0);
        loan1.setInterestRate(5.5);
        loan1.setTimePeriod("12 months");
        loan1.setStatus("Approved");
        loan1.setTimestampDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        when(loanService.getLoanHistory("john@test.com")).thenReturn(Arrays.asList(loan1));

        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"john@test.com\"}")
        .when()
            .post("/loan/history")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(1))
            .body("[0].name", equalTo("John Doe"))
            .body("[0].email", equalTo("john@test.com"))
            .body("[0].account_type", equalTo("savings"))
            .body("[0].account_number", equalTo("12345"))
            .body("[0].loan_amount", equalTo(5000.0f))
            .body("[0].status", equalTo("Approved"))
            .body("[0].timestamp", equalTo("2024-01-15T10:30"));
    }

    @Test
    void getLoanHistory_returnsEmptyArrayWhenNoLoans() {
        when(loanService.getLoanHistory("nobody@test.com")).thenReturn(Collections.emptyList());

        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"nobody@test.com\"}")
        .when()
            .post("/loan/history")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    void getLoanHistory_returnsMultipleLoans() {
        LoanDocument loan1 = new LoanDocument();
        loan1.setName("John Doe");
        loan1.setEmail("john@test.com");
        loan1.setStatus("Approved");
        loan1.setLoanAmount(5000.0);
        loan1.setTimestampDate(LocalDateTime.now());

        LoanDocument loan2 = new LoanDocument();
        loan2.setName("John Doe");
        loan2.setEmail("john@test.com");
        loan2.setStatus("Declined");
        loan2.setLoanAmount(100000.0);
        loan2.setTimestampDate(LocalDateTime.now());

        when(loanService.getLoanHistory("john@test.com")).thenReturn(Arrays.asList(loan1, loan2));

        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"john@test.com\"}")
        .when()
            .post("/loan/history")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].status", equalTo("Approved"))
            .body("[1].status", equalTo("Declined"));
    }

    @Test
    void processLoanRequest_endpointPathIsCorrect() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(true, "Success"));

        // Verify exact path /loan/request
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test\",\"email\":\"test@test.com\",\"account_type\":\"savings\",\"account_number\":\"123\",\"govt_id_type\":\"id\",\"govt_id_number\":\"123\",\"loan_type\":\"personal\",\"loan_amount\":100,\"interest_rate\":5,\"time_period\":\"12\"}")
        .when()
            .post("/loan/request")
        .then()
            .statusCode(200);
    }

    @Test
    void getLoanHistory_endpointPathIsCorrect() {
        when(loanService.getLoanHistory(anyString())).thenReturn(Collections.emptyList());

        // Verify exact path /loan/history
        given()
            .contentType(ContentType.JSON)
            .body("{\"email\": \"test@test.com\"}")
        .when()
            .post("/loan/history")
        .then()
            .statusCode(200);
    }
}
