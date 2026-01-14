# Phase 2: Domain Models and MongoDB Repository

## Overview
Create Java domain models that match the MongoDB document structure and implement repository classes for database operations with exact query parity.

## Changes Required:

### 1. Loan Request DTO
**File**: `loan-java/src/main/java/com/martianbank/loan/model/LoanRequestDto.java`

```java
package com.martianbank.loan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanRequestDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("govt_id_type")
    private String govtIdType;

    @JsonProperty("govt_id_number")
    private String govtIdNumber;

    @JsonProperty("loan_type")
    private String loanType;

    @JsonProperty("loan_amount")
    private double loanAmount;

    @JsonProperty("interest_rate")
    private double interestRate;

    @JsonProperty("time_period")
    private String timePeriod;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getGovtIdType() { return govtIdType; }
    public void setGovtIdType(String govtIdType) { this.govtIdType = govtIdType; }

    public String getGovtIdNumber() { return govtIdNumber; }
    public void setGovtIdNumber(String govtIdNumber) { this.govtIdNumber = govtIdNumber; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public String getTimePeriod() { return timePeriod; }
    public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }
}
```

### 2. Loan Response DTO
**File**: `loan-java/src/main/java/com/martianbank/loan/model/LoanResponseDto.java`

```java
package com.martianbank.loan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanResponseDto {

    @JsonProperty("approved")
    private boolean approved;

    @JsonProperty("message")
    private String message;

    public LoanResponseDto() {}

    public LoanResponseDto(boolean approved, String message) {
        this.approved = approved;
        this.message = message;
    }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
```

### 3. Loan History Request DTO
**File**: `loan-java/src/main/java/com/martianbank/loan/model/LoanHistoryRequestDto.java`

```java
package com.martianbank.loan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanHistoryRequestDto {

    @JsonProperty("email")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### 4. Loan Document Model
**File**: `loan-java/src/main/java/com/martianbank/loan/model/LoanDocument.java`

```java
package com.martianbank.loan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.time.LocalDateTime;

public class LoanDocument {

    @BsonProperty("name")
    @JsonProperty("name")
    private String name;

    @BsonProperty("email")
    @JsonProperty("email")
    private String email;

    @BsonProperty("account_type")
    @JsonProperty("account_type")
    private String accountType;

    @BsonProperty("account_number")
    @JsonProperty("account_number")
    private String accountNumber;

    @BsonProperty("govt_id_type")
    @JsonProperty("govt_id_type")
    private String govtIdType;

    @BsonProperty("govt_id_number")
    @JsonProperty("govt_id_number")
    private String govtIdNumber;

    @BsonProperty("loan_type")
    @JsonProperty("loan_type")
    private String loanType;

    @BsonProperty("loan_amount")
    @JsonProperty("loan_amount")
    private double loanAmount;

    @BsonProperty("interest_rate")
    @JsonProperty("interest_rate")
    private double interestRate;

    @BsonProperty("time_period")
    @JsonProperty("time_period")
    private String timePeriod;

    @BsonProperty("status")
    @JsonProperty("status")
    private String status;

    @BsonProperty("timestamp")
    private LocalDateTime timestampDate;

    // For JSON serialization - returns timestamp as string like Python does
    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestampDate != null ? timestampDate.toString() : null;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getGovtIdType() { return govtIdType; }
    public void setGovtIdType(String govtIdType) { this.govtIdType = govtIdType; }

    public String getGovtIdNumber() { return govtIdNumber; }
    public void setGovtIdNumber(String govtIdNumber) { this.govtIdNumber = govtIdNumber; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public String getTimePeriod() { return timePeriod; }
    public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestampDate() { return timestampDate; }
    public void setTimestampDate(LocalDateTime timestampDate) { this.timestampDate = timestampDate; }
}
```

### 5. Account Document Model
**File**: `loan-java/src/main/java/com/martianbank/loan/model/AccountDocument.java`

```java
package com.martianbank.loan.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class AccountDocument {

    @BsonProperty("account_number")
    private String accountNumber;

    @BsonProperty("email_id")
    private String emailId;

    @BsonProperty("balance")
    private double balance;

    // Additional fields that may exist in account document
    @BsonProperty("name")
    private String name;

    @BsonProperty("account_type")
    private String accountType;

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
}
```

### 6. Accounts Repository
**File**: `loan-java/src/main/java/com/martianbank/loan/repository/AccountsRepository.java`

```java
package com.martianbank.loan.repository;

import com.martianbank.loan.model.AccountDocument;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AccountsRepository {

    private static final Logger LOG = Logger.getLogger(AccountsRepository.class);

    @Inject
    MongoClient mongoClient;

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("bank").getCollection("accounts");
    }

    /**
     * Count documents matching email_id AND account_number
     * Matches Python: collection_accounts.count_documents({"email_id": email, 'account_number': account_number})
     */
    public long countByEmailIdAndAccountNumber(String emailId, String accountNumber) {
        return getCollection().countDocuments(
            Filters.and(
                Filters.eq("email_id", emailId),
                Filters.eq("account_number", accountNumber)
            )
        );
    }

    /**
     * Get account by account number (scans all accounts like Python does)
     * Matches Python: Linear search through collection_accounts.find()
     */
    public AccountDocument getAccountByAccountNumber(String accountNumber) {
        // Replicating Python's inefficient full scan behavior
        for (Document doc : getCollection().find()) {
            if (accountNumber.equals(doc.getString("account_number"))) {
                AccountDocument account = new AccountDocument();
                account.setAccountNumber(doc.getString("account_number"));
                account.setEmailId(doc.getString("email_id"));
                account.setBalance(doc.getDouble("balance") != null ? doc.getDouble("balance") : 0.0);
                account.setName(doc.getString("name"));
                account.setAccountType(doc.getString("account_type"));
                LOG.debugf("Found account: %s", accountNumber);
                return account;
            }
        }
        return null;
    }

    /**
     * Update account balance
     * Matches Python: collection_accounts.update_one({"account_number": ...}, {"$set": {"balance": ...}})
     */
    public void updateBalance(String accountNumber, double newBalance) {
        getCollection().updateOne(
            Filters.eq("account_number", accountNumber),
            Updates.set("balance", newBalance)
        );
        LOG.debugf("Updated balance for account %s to %f", accountNumber, newBalance);
    }
}
```

### 7. Loans Repository
**File**: `loan-java/src/main/java/com/martianbank/loan/repository/LoansRepository.java`

```java
package com.martianbank.loan.repository;

import com.martianbank.loan.model.LoanDocument;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class LoansRepository {

    private static final Logger LOG = Logger.getLogger(LoansRepository.class);

    @Inject
    MongoClient mongoClient;

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("bank").getCollection("loans");
    }

    /**
     * Insert a new loan document
     * Matches Python: collection_loans.insert_one(loan_request)
     */
    public void insertLoan(LoanDocument loan) {
        Document doc = new Document()
            .append("name", loan.getName())
            .append("email", loan.getEmail())
            .append("account_type", loan.getAccountType())
            .append("account_number", loan.getAccountNumber())
            .append("govt_id_type", loan.getGovtIdType())
            .append("govt_id_number", loan.getGovtIdNumber())
            .append("loan_type", loan.getLoanType())
            .append("loan_amount", loan.getLoanAmount())
            .append("interest_rate", loan.getInterestRate())
            .append("time_period", loan.getTimePeriod())
            .append("status", loan.getStatus())
            .append("timestamp", Date.from(loan.getTimestampDate().atZone(ZoneId.systemDefault()).toInstant()));

        getCollection().insertOne(doc);
        LOG.debugf("Inserted loan for account: %s", loan.getAccountNumber());
    }

    /**
     * Find all loans by email
     * Matches Python: collection_loans.find({"email": email})
     */
    public List<LoanDocument> findByEmail(String email) {
        List<LoanDocument> loans = new ArrayList<>();

        for (Document doc : getCollection().find(Filters.eq("email", email))) {
            LoanDocument loan = new LoanDocument();
            loan.setName(doc.getString("name"));
            loan.setEmail(doc.getString("email"));
            loan.setAccountType(doc.getString("account_type"));
            loan.setAccountNumber(doc.getString("account_number"));
            loan.setGovtIdType(doc.getString("govt_id_type"));
            loan.setGovtIdNumber(doc.getString("govt_id_number"));
            loan.setLoanType(doc.getString("loan_type"));
            loan.setLoanAmount(doc.getDouble("loan_amount") != null ? doc.getDouble("loan_amount") : 0.0);
            loan.setInterestRate(doc.getDouble("interest_rate") != null ? doc.getDouble("interest_rate") : 0.0);
            loan.setTimePeriod(doc.getString("time_period"));
            loan.setStatus(doc.getString("status"));

            // Convert Date to LocalDateTime
            Date timestamp = doc.getDate("timestamp");
            if (timestamp != null) {
                loan.setTimestampDate(LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault()));
            }

            loans.add(loan);
        }

        return loans;
    }
}
```

## Success Criteria:

### Automated Verification:
- [ ] Build succeeds with all model classes: `cd loan-java && ./gradlew compileJava`
- [ ] No compilation errors

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 2.5 for testing.
