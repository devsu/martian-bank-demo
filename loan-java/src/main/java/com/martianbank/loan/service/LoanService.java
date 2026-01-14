package com.martianbank.loan.service;

import com.martianbank.loan.model.*;
import com.martianbank.loan.repository.AccountsRepository;
import com.martianbank.loan.repository.LoansRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Core loan business logic - exact replica of Python LoanGeneric class
 * Reference: loan/loan.py:48-143
 */
@ApplicationScoped
public class LoanService {

    private static final Logger LOG = Logger.getLogger(LoanService.class);

    @Inject
    AccountsRepository accountsRepository;

    @Inject
    LoansRepository loansRepository;

    /**
     * Process a loan request
     * Exact replica of Python: LoanGeneric.ProcessLoanRequest()
     * Reference: loan/loan.py:49-95
     */
    public LoanResponseDto processLoanRequest(LoanRequestDto request) {
        String name = request.getName();
        String email = request.getEmail();
        String accountType = request.getAccountType();
        String accountNumber = request.getAccountNumber();
        String govtIdType = request.getGovtIdType();
        String govtIdNumber = request.getGovtIdNumber();
        String loanType = request.getLoanType();
        double loanAmount = request.getLoanAmount();
        double interestRate = request.getInterestRate();
        String timePeriod = request.getTimePeriod();

        // Get account by account number (matches Python __getAccount)
        AccountDocument userAccount = accountsRepository.getAccountByAccountNumber(accountNumber);

        // Count matching accounts by email_id AND account_number
        // Matches Python: collection_accounts.count_documents({"email_id": email, 'account_number': account_number})
        long count = accountsRepository.countByEmailIdAndAccountNumber(email, accountNumber);

        LOG.debugf("user account only based on account number search : %s", userAccount);
        LOG.debugf("Count whether the email and account exist or not : %d", count);

        // Validation: check if account exists
        // Matches Python: loan.py:67-68
        if (count == 0) {
            return new LoanResponseDto(false, "Email or Account number not found.");
        }

        // Approve loan (includes balance update)
        // Matches Python: loan.py:69
        boolean result = approveLoan(userAccount, loanAmount);
        LOG.debugf("Result %b", result);

        // Set message based on result
        // Matches Python: loan.py:71
        String message = result ? "Loan Approved" : "Loan Rejected";

        // Create loan document
        // Matches Python: loan.py:74-88
        LoanDocument loanDocument = new LoanDocument();
        loanDocument.setName(name);
        loanDocument.setEmail(email);
        loanDocument.setAccountType(accountType);
        loanDocument.setAccountNumber(accountNumber);
        loanDocument.setGovtIdType(govtIdType);
        loanDocument.setGovtIdNumber(govtIdNumber);
        loanDocument.setLoanType(loanType);
        loanDocument.setLoanAmount(loanAmount);
        loanDocument.setInterestRate(interestRate);
        loanDocument.setTimePeriod(timePeriod);
        loanDocument.setStatus(result ? "Approved" : "Declined");
        loanDocument.setTimestampDate(LocalDateTime.now());

        // Insert loan into database
        // Matches Python: loan.py:90
        loansRepository.insertLoan(loanDocument);

        // Create response
        // Matches Python: loan.py:92-95
        LoanResponseDto response = new LoanResponseDto(result, message);
        LOG.debugf("Account: %s", accountNumber);
        LOG.debugf("Response: %s", response);

        return response;
    }

    /**
     * Get loan history by email
     * Exact replica of Python: LoanGeneric.getLoanHistory()
     * Reference: loan/loan.py:97-120
     */
    public List<LoanDocument> getLoanHistory(String email) {
        return loansRepository.findByEmail(email);
    }

    /**
     * Approve loan and update account balance
     * Exact replica of Python: LoanGeneric.__approveLoan()
     * Reference: loan/loan.py:132-143
     */
    private boolean approveLoan(AccountDocument account, double amount) {
        // Validation: amount must be >= 1
        // Matches Python: loan.py:133-134
        if (amount < 1) {
            return false;
        }

        // Add loan amount to balance
        // Matches Python: loan.py:136
        double newBalance = account.getBalance() + amount;

        // Update account in database
        // Matches Python: loan.py:138-141
        accountsRepository.updateBalance(account.getAccountNumber(), newBalance);

        // Matches Python: loan.py:143
        return true;
    }
}
