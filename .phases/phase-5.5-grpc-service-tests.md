# Phase 5.5: gRPC Service Tests

## Overview
Create unit tests for the gRPC service implementation, testing request/response mapping and service delegation.

## Changes Required:

### 1. LoanGrpcService Test
**File**: `loan-java/src/test/java/com/martianbank/loan/grpc/LoanGrpcServiceTest.java`

```java
package com.martianbank.loan.grpc;

import com.martianbank.loan.model.*;
import com.martianbank.loan.service.LoanService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanGrpcServiceTest {

    @Mock
    LoanService loanService;

    @Mock
    StreamObserver<LoanResponse> loanResponseObserver;

    @Mock
    StreamObserver<LoansHistoryResponse> historyResponseObserver;

    @InjectMocks
    LoanGrpcService loanGrpcService;

    private LoanRequest validGrpcRequest;

    @BeforeEach
    void setUp() {
        validGrpcRequest = LoanRequest.newBuilder()
            .setName("John Doe")
            .setEmail("john@test.com")
            .setAccountType("savings")
            .setAccountNumber("12345")
            .setGovtIdType("passport")
            .setGovtIdNumber("ABC123")
            .setLoanType("personal")
            .setLoanAmount(5000.0)
            .setInterestRate(5.5)
            .setTimePeriod("12 months")
            .build();
    }

    @Test
    void processLoanRequest_returnsApprovedResponse() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(true, "Loan Approved"));

        loanGrpcService.processLoanRequest(validGrpcRequest, loanResponseObserver);

        ArgumentCaptor<LoanResponse> responseCaptor = ArgumentCaptor.forClass(LoanResponse.class);
        verify(loanResponseObserver).onNext(responseCaptor.capture());
        verify(loanResponseObserver).onCompleted();

        LoanResponse response = responseCaptor.getValue();
        assertTrue(response.getApproved());
        assertEquals("Loan Approved", response.getMessage());
    }

    @Test
    void processLoanRequest_returnsRejectedResponse() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(false, "Email or Account number not found."));

        loanGrpcService.processLoanRequest(validGrpcRequest, loanResponseObserver);

        ArgumentCaptor<LoanResponse> responseCaptor = ArgumentCaptor.forClass(LoanResponse.class);
        verify(loanResponseObserver).onNext(responseCaptor.capture());

        LoanResponse response = responseCaptor.getValue();
        assertFalse(response.getApproved());
        assertEquals("Email or Account number not found.", response.getMessage());
    }

    @Test
    void processLoanRequest_mapsAllFieldsToDto() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(true, "Success"));

        loanGrpcService.processLoanRequest(validGrpcRequest, loanResponseObserver);

        ArgumentCaptor<LoanRequestDto> dtoCaptor = ArgumentCaptor.forClass(LoanRequestDto.class);
        verify(loanService).processLoanRequest(dtoCaptor.capture());

        LoanRequestDto dto = dtoCaptor.getValue();
        assertEquals("John Doe", dto.getName());
        assertEquals("john@test.com", dto.getEmail());
        assertEquals("savings", dto.getAccountType());
        assertEquals("12345", dto.getAccountNumber());
        assertEquals("passport", dto.getGovtIdType());
        assertEquals("ABC123", dto.getGovtIdNumber());
        assertEquals("personal", dto.getLoanType());
        assertEquals(5000.0, dto.getLoanAmount());
        assertEquals(5.5, dto.getInterestRate());
        assertEquals("12 months", dto.getTimePeriod());
    }

    @Test
    void processLoanRequest_completesStreamAfterResponse() {
        when(loanService.processLoanRequest(any(LoanRequestDto.class)))
            .thenReturn(new LoanResponseDto(true, "Success"));

        loanGrpcService.processLoanRequest(validGrpcRequest, loanResponseObserver);

        verify(loanResponseObserver).onNext(any());
        verify(loanResponseObserver).onCompleted();
        verify(loanResponseObserver, never()).onError(any());
    }

    @Test
    void getLoanHistory_returnsLoansInResponse() {
        LoanDocument loan = new LoanDocument();
        loan.setName("John Doe");
        loan.setEmail("john@test.com");
        loan.setAccountType("savings");
        loan.setAccountNumber("12345");
        loan.setGovtIdType("passport");
        loan.setGovtIdNumber("ABC123");
        loan.setLoanType("personal");
        loan.setLoanAmount(5000.0);
        loan.setInterestRate(5.5);
        loan.setTimePeriod("12 months");
        loan.setStatus("Approved");
        loan.setTimestampDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        when(loanService.getLoanHistory("john@test.com")).thenReturn(Arrays.asList(loan));

        LoansHistoryRequest request = LoansHistoryRequest.newBuilder()
            .setEmail("john@test.com")
            .build();

        loanGrpcService.getLoanHistory(request, historyResponseObserver);

        ArgumentCaptor<LoansHistoryResponse> responseCaptor = ArgumentCaptor.forClass(LoansHistoryResponse.class);
        verify(historyResponseObserver).onNext(responseCaptor.capture());
        verify(historyResponseObserver).onCompleted();

        LoansHistoryResponse response = responseCaptor.getValue();
        assertEquals(1, response.getLoansCount());

        Loan grpcLoan = response.getLoans(0);
        assertEquals("John Doe", grpcLoan.getName());
        assertEquals("john@test.com", grpcLoan.getEmail());
        assertEquals("savings", grpcLoan.getAccountType());
        assertEquals("12345", grpcLoan.getAccountNumber());
        assertEquals("Approved", grpcLoan.getStatus());
        assertEquals("2024-01-15T10:30", grpcLoan.getTimestamp());
    }

    @Test
    void getLoanHistory_returnsEmptyWhenNoLoans() {
        when(loanService.getLoanHistory("nobody@test.com")).thenReturn(Collections.emptyList());

        LoansHistoryRequest request = LoansHistoryRequest.newBuilder()
            .setEmail("nobody@test.com")
            .build();

        loanGrpcService.getLoanHistory(request, historyResponseObserver);

        ArgumentCaptor<LoansHistoryResponse> responseCaptor = ArgumentCaptor.forClass(LoansHistoryResponse.class);
        verify(historyResponseObserver).onNext(responseCaptor.capture());

        LoansHistoryResponse response = responseCaptor.getValue();
        assertEquals(0, response.getLoansCount());
    }

    @Test
    void getLoanHistory_returnsMultipleLoans() {
        LoanDocument loan1 = new LoanDocument();
        loan1.setName("John");
        loan1.setStatus("Approved");
        loan1.setTimestampDate(LocalDateTime.now());

        LoanDocument loan2 = new LoanDocument();
        loan2.setName("John");
        loan2.setStatus("Declined");
        loan2.setTimestampDate(LocalDateTime.now());

        when(loanService.getLoanHistory("john@test.com")).thenReturn(Arrays.asList(loan1, loan2));

        LoansHistoryRequest request = LoansHistoryRequest.newBuilder()
            .setEmail("john@test.com")
            .build();

        loanGrpcService.getLoanHistory(request, historyResponseObserver);

        ArgumentCaptor<LoansHistoryResponse> responseCaptor = ArgumentCaptor.forClass(LoansHistoryResponse.class);
        verify(historyResponseObserver).onNext(responseCaptor.capture());

        LoansHistoryResponse response = responseCaptor.getValue();
        assertEquals(2, response.getLoansCount());
        assertEquals("Approved", response.getLoans(0).getStatus());
        assertEquals("Declined", response.getLoans(1).getStatus());
    }

    @Test
    void getLoanHistory_mapsAllLoanFieldsToGrpc() {
        LoanDocument loan = new LoanDocument();
        loan.setName("Test User");
        loan.setEmail("test@test.com");
        loan.setAccountType("checking");
        loan.setAccountNumber("99999");
        loan.setGovtIdType("license");
        loan.setGovtIdNumber("DRV789");
        loan.setLoanType("auto");
        loan.setLoanAmount(25000.0);
        loan.setInterestRate(3.9);
        loan.setTimePeriod("60 months");
        loan.setStatus("Approved");
        loan.setTimestampDate(LocalDateTime.of(2024, 6, 1, 14, 0, 0));

        when(loanService.getLoanHistory("test@test.com")).thenReturn(Arrays.asList(loan));

        LoansHistoryRequest request = LoansHistoryRequest.newBuilder()
            .setEmail("test@test.com")
            .build();

        loanGrpcService.getLoanHistory(request, historyResponseObserver);

        ArgumentCaptor<LoansHistoryResponse> responseCaptor = ArgumentCaptor.forClass(LoansHistoryResponse.class);
        verify(historyResponseObserver).onNext(responseCaptor.capture());

        Loan grpcLoan = responseCaptor.getValue().getLoans(0);
        assertEquals("Test User", grpcLoan.getName());
        assertEquals("test@test.com", grpcLoan.getEmail());
        assertEquals("checking", grpcLoan.getAccountType());
        assertEquals("99999", grpcLoan.getAccountNumber());
        assertEquals("license", grpcLoan.getGovtIdType());
        assertEquals("DRV789", grpcLoan.getGovtIdNumber());
        assertEquals("auto", grpcLoan.getLoanType());
        assertEquals(25000.0, grpcLoan.getLoanAmount());
        assertEquals(3.9, grpcLoan.getInterestRate());
        assertEquals("60 months", grpcLoan.getTimePeriod());
        assertEquals("Approved", grpcLoan.getStatus());
        assertEquals("2024-06-01T14:00", grpcLoan.getTimestamp());
    }

    @Test
    void getLoanHistory_completesStreamAfterResponse() {
        when(loanService.getLoanHistory(anyString())).thenReturn(Collections.emptyList());

        LoansHistoryRequest request = LoansHistoryRequest.newBuilder()
            .setEmail("test@test.com")
            .build();

        loanGrpcService.getLoanHistory(request, historyResponseObserver);

        verify(historyResponseObserver).onNext(any());
        verify(historyResponseObserver).onCompleted();
        verify(historyResponseObserver, never()).onError(any());
    }
}
```

## Success Criteria:

### Automated Verification:
- [x] All tests pass: `cd loan-java && ./gradlew test`
- [x] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [x] LoanGrpcService class achieves ≥ 90% line coverage
- [x] Coverage threshold passes: `cd loan-java && ./gradlew jacocoTestCoverageVerification`
- [x] Overall project coverage ≥ 90%: Check `build/reports/jacoco/test/html/index.html`

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 6.
