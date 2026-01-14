package com.martianbank.loan.service;

import com.martianbank.loan.model.*;
import com.martianbank.loan.repository.AccountsRepository;
import com.martianbank.loan.repository.LoansRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    AccountsRepository accountsRepository;

    @Mock
    LoansRepository loansRepository;

    @InjectMocks
    LoanService loanService;

    private LoanRequestDto validRequest;
    private AccountDocument validAccount;

    @BeforeEach
    void setUp() {
        validRequest = new LoanRequestDto();
        validRequest.setName("John Doe");
        validRequest.setEmail("john@test.com");
        validRequest.setAccountType("savings");
        validRequest.setAccountNumber("12345");
        validRequest.setGovtIdType("passport");
        validRequest.setGovtIdNumber("ABC123");
        validRequest.setLoanType("personal");
        validRequest.setLoanAmount(5000.0);
        validRequest.setInterestRate(5.5);
        validRequest.setTimePeriod("12 months");

        validAccount = new AccountDocument();
        validAccount.setAccountNumber("12345");
        validAccount.setEmailId("john@test.com");
        validAccount.setBalance(1000.0);
    }

    @Test
    void processLoanRequest_approvesLoanWhenAccountExistsAndAmountValid() {
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        LoanResponseDto response = loanService.processLoanRequest(validRequest);

        assertTrue(response.isApproved());
        assertEquals("Loan Approved", response.getMessage());
        verify(accountsRepository).updateBalance("12345", 6000.0);
        verify(loansRepository).insertLoan(any(LoanDocument.class));
    }

    @Test
    void processLoanRequest_rejectsWhenAccountNotFound() {
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(0L);

        LoanResponseDto response = loanService.processLoanRequest(validRequest);

        assertFalse(response.isApproved());
        assertEquals("Email or Account number not found.", response.getMessage());
        verify(accountsRepository, never()).updateBalance(anyString(), anyDouble());
        verify(loansRepository, never()).insertLoan(any());
    }

    @Test
    void processLoanRequest_rejectsWhenAmountBelowOne() {
        validRequest.setLoanAmount(0.5);
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        LoanResponseDto response = loanService.processLoanRequest(validRequest);

        assertFalse(response.isApproved());
        assertEquals("Loan Rejected", response.getMessage());
        verify(accountsRepository, never()).updateBalance(anyString(), anyDouble());
        verify(loansRepository).insertLoan(argThat(loan -> "Declined".equals(loan.getStatus())));
    }

    @Test
    void processLoanRequest_rejectsWhenAmountIsZero() {
        validRequest.setLoanAmount(0.0);
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        LoanResponseDto response = loanService.processLoanRequest(validRequest);

        assertFalse(response.isApproved());
        assertEquals("Loan Rejected", response.getMessage());
    }

    @Test
    void processLoanRequest_rejectsWhenAmountIsNegative() {
        validRequest.setLoanAmount(-100.0);
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        LoanResponseDto response = loanService.processLoanRequest(validRequest);

        assertFalse(response.isApproved());
        assertEquals("Loan Rejected", response.getMessage());
    }

    @Test
    void processLoanRequest_approvesWhenAmountIsExactlyOne() {
        validRequest.setLoanAmount(1.0);
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        LoanResponseDto response = loanService.processLoanRequest(validRequest);

        assertTrue(response.isApproved());
        assertEquals("Loan Approved", response.getMessage());
        verify(accountsRepository).updateBalance("12345", 1001.0);
    }

    @Test
    void processLoanRequest_savesLoanWithApprovedStatus() {
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        loanService.processLoanRequest(validRequest);

        verify(loansRepository).insertLoan(argThat(loan ->
            "Approved".equals(loan.getStatus()) &&
            "John Doe".equals(loan.getName()) &&
            "john@test.com".equals(loan.getEmail()) &&
            loan.getLoanAmount() == 5000.0
        ));
    }

    @Test
    void processLoanRequest_savesLoanWithDeclinedStatus() {
        validRequest.setLoanAmount(0.5);
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        loanService.processLoanRequest(validRequest);

        verify(loansRepository).insertLoan(argThat(loan ->
            "Declined".equals(loan.getStatus())
        ));
    }

    @Test
    void getLoanHistory_returnsLoansFromRepository() {
        LoanDocument loan1 = new LoanDocument();
        loan1.setEmail("john@test.com");
        loan1.setLoanAmount(5000.0);

        LoanDocument loan2 = new LoanDocument();
        loan2.setEmail("john@test.com");
        loan2.setLoanAmount(3000.0);

        when(loansRepository.findByEmail("john@test.com")).thenReturn(Arrays.asList(loan1, loan2));

        List<LoanDocument> history = loanService.getLoanHistory("john@test.com");

        assertEquals(2, history.size());
        verify(loansRepository).findByEmail("john@test.com");
    }

    @Test
    void getLoanHistory_returnsEmptyListWhenNoLoans() {
        when(loansRepository.findByEmail("nobody@test.com")).thenReturn(Collections.emptyList());

        List<LoanDocument> history = loanService.getLoanHistory("nobody@test.com");

        assertTrue(history.isEmpty());
    }

    @Test
    void processLoanRequest_setsTimestampOnLoan() {
        when(accountsRepository.getAccountByAccountNumber("12345")).thenReturn(validAccount);
        when(accountsRepository.countByEmailIdAndAccountNumber("john@test.com", "12345")).thenReturn(1L);

        loanService.processLoanRequest(validRequest);

        verify(loansRepository).insertLoan(argThat(loan ->
            loan.getTimestampDate() != null
        ));
    }
}
