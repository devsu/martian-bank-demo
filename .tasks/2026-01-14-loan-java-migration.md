# Loan Microservice Migration to Java 25 - Implementation Plan

## Overview

Migrate the existing Python Loan microservice to Java 25 using Quarkus framework and Gradle build system. The new service will be created in a `loan-java/` directory, maintaining exact functional parity with the Python implementation including identical endpoints, request/response formats, validations, and database operations.

## Current State Analysis

### Existing Python Implementation (`loan/loan.py`)

**Technology Stack:**
- Python 3.x with Flask (HTTP) and grpcio (gRPC)
- MongoDB via pymongo
- Port: 50053
- Protocol switching via `SERVICE_PROTOCOL` environment variable

**HTTP Endpoints:**
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/loan/request` | Process loan application |
| POST | `/loan/history` | Get loan history by email |

**gRPC Service (from `protobufs/loan.proto`):**
```protobuf
service LoanService {
  rpc ProcessLoanRequest(LoanRequest) returns (LoanResponse);
  rpc getLoanHistory(LoansHistoryRequest) returns (LoansHistoryResponse);
}
```

**Database Operations:**
| Collection | Operations | Purpose |
|------------|------------|---------|
| `accounts` | `count_documents`, `find`, `update_one` | Validate account, update balance |
| `loans` | `insert_one`, `find` | Store loan records, query history |

**Business Logic:**
1. Validate account exists by matching `email_id` AND `account_number` in accounts collection
2. Loan approval: auto-approve if `loan_amount >= 1`
3. On approval: add `loan_amount` to account `balance`
4. Store loan record with status "Approved" or "Declined" plus timestamp

### Key Implementation Details to Preserve

**Request Validation (`loan.py:63-68`):**
```python
count = collection_accounts.count_documents({"email_id": email, 'account_number': account_number})
if count == 0:
    return {"approved": False, "message": "Email or Account number not found."}
```

**Approval Logic (`loan.py:132-143`):**
```python
def __approveLoan(self, account, amount):
    if amount < 1:
        return False
    account["balance"] += amount
    collection_accounts.update_one(
        {"account_number": account["account_number"]},
        {"$set": {"balance": account["balance"]}},
    )
    return True
```

**Response Formats:**
- Loan Request: `{"approved": boolean, "message": string}`
- Loan History: Array of loan objects with all fields including `timestamp` as string

## Desired End State

A fully functional Java 25 Quarkus-based Loan microservice that:

1. Exposes identical HTTP endpoints (`/loan/request`, `/loan/history`)
2. Implements identical gRPC service methods
3. Uses the same MongoDB collections with identical queries
4. Returns identical response formats
5. Integrates with Docker Compose alongside existing services
6. Can be swapped with Python version by changing docker-compose configuration

### Verification Criteria:
- All HTTP endpoints return identical JSON responses for identical inputs
- gRPC service works with existing dashboard service (no changes to dashboard)
- Docker container builds and runs successfully
- Service connects to MongoDB and performs identical operations

## What We're NOT Doing

- **Tech Debt**: Not refactoring the inefficient `__getAccount()` full collection scan
- **Security**: Not adding authentication, authorization, or input sanitization
- **Performance**: Not adding indexes or query optimizations
- **Transactions**: Not adding MongoDB transaction support for atomic operations
- **Error Handling**: Not adding comprehensive error handling beyond what exists
- **Testing**: Not adding unit/integration tests beyond basic verification
- **API Changes**: Not modifying any endpoint paths, methods, or response formats

## Implementation Approach

Create a new `loan-java/` directory with a Quarkus application using:
- Java 25 (latest LTS features)
- Quarkus 3.x framework
- Gradle (Kotlin DSL) build system
- quarkus-mongodb-panache for MongoDB operations
- quarkus-grpc for gRPC support
- quarkus-rest (RESTEasy Reactive) for HTTP endpoints

---

## Phase 1: Project Setup and Build Configuration

### Overview
Initialize the Java 25 Quarkus project with Gradle build configuration, establish project structure, and configure dependencies for MongoDB, gRPC, and REST.

### Changes Required:

#### 1. Create Project Directory Structure
**Directory**: `loan-java/`

Create the following directory structure:
```
loan-java/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── martianbank/
│       │           └── loan/
│       │               ├── LoanApplication.java
│       │               ├── service/
│       │               ├── resource/
│       │               ├── grpc/
│       │               ├── repository/
│       │               └── model/
│       ├── proto/
│       │   └── loan.proto
│       └── resources/
│           └── application.properties
└── Dockerfile
```

#### 2. Gradle Build Configuration
**File**: `loan-java/build.gradle.kts`

```kotlin
plugins {
    java
    id("io.quarkus") version "3.17.5"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    // REST API
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")

    // gRPC
    implementation("io.quarkus:quarkus-grpc")

    // MongoDB
    implementation("io.quarkus:quarkus-mongodb-client")

    // Health checks
    implementation("io.quarkus:quarkus-smallrye-health")

    // Arc (CDI)
    implementation("io.quarkus:quarkus-arc")

    // Testing
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
```

#### 3. Gradle Settings
**File**: `loan-java/settings.gradle.kts`

```kotlin
pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}

rootProject.name = "loan-java"
```

#### 4. Gradle Properties
**File**: `loan-java/gradle.properties`

```properties
quarkusPluginId=io.quarkus
quarkusPluginVersion=3.17.5
quarkusPlatformGroupId=io.quarkus.platform
quarkusPlatformArtifactId=quarkus-bom
quarkusPlatformVersion=3.17.5
```

#### 5. Application Configuration
**File**: `loan-java/src/main/resources/application.properties`

```properties
# Application
quarkus.application.name=loan-service

# HTTP Server - matches Python Flask port
quarkus.http.port=50053
quarkus.http.host=0.0.0.0

# gRPC Server - same port for simplicity, Quarkus can handle both
quarkus.grpc.server.port=50053
quarkus.grpc.server.host=0.0.0.0

# MongoDB Configuration
quarkus.mongodb.connection-string=${DB_URL:mongodb://root:example@localhost:27017}
quarkus.mongodb.database=bank

# Service Protocol (http or grpc)
service.protocol=${SERVICE_PROTOCOL:http}

# Logging
quarkus.log.level=DEBUG
quarkus.log.category."com.martianbank".level=DEBUG
```

#### 6. Copy Proto File
**File**: `loan-java/src/main/proto/loan.proto`

Copy the existing proto file from `protobufs/loan.proto` with added Java package option:

```protobuf
// Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

syntax = "proto3";

option java_package = "com.martianbank.loan.grpc";
option java_outer_classname = "LoanProto";

package loan;

message LoanRequest {
  string name = 1;
  string email = 2;
  string account_type = 3;
  string account_number = 4;
  string govt_id_type = 5;
  string govt_id_number = 6;
  string loan_type = 7;
  double loan_amount = 8;
  double interest_rate = 9;
  string time_period = 10;
}

message LoanResponse {
  bool approved = 1;
  string message = 2;
}

message LoansHistoryRequest {
  string email = 1;
}

message Loan {
  string name = 1;
  string email = 2;
  string account_type = 3;
  string account_number = 4;
  string govt_id_type = 5;
  string govt_id_number = 6;
  string loan_type = 7;
  double loan_amount = 8;
  double interest_rate = 9;
  string time_period = 10;
  string status = 11;
  string timestamp = 12;
}

message LoansHistoryResponse {
  repeated Loan loans = 1;
}

service LoanService {
  rpc ProcessLoanRequest(LoanRequest) returns (LoanResponse);
  rpc getLoanHistory(LoansHistoryRequest) returns (LoansHistoryResponse);
}
```

#### 7. Main Application Class
**File**: `loan-java/src/main/java/com/martianbank/loan/LoanApplication.java`

```java
package com.martianbank.loan;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class LoanApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
```

### Success Criteria:

#### Automated Verification:
- [ ] Gradle build succeeds: `cd loan-java && ./gradlew build`
- [ ] Proto files compile: `cd loan-java && ./gradlew quarkusGenerateCode`
- [ ] Application starts without errors: `cd loan-java && ./gradlew quarkusDev`

#### Manual Verification:
- [ ] Project structure matches the specified layout
- [ ] All configuration files are in place
- [ ] No compilation errors in IDE

**Implementation Note**: After completing this phase and all automated verification passes, pause here for manual confirmation before proceeding to Phase 2.

---

## Phase 2: Domain Models and MongoDB Repository

### Overview
Create Java domain models that match the MongoDB document structure and implement repository classes for database operations with exact query parity.

### Changes Required:

#### 1. Loan Request DTO
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

#### 2. Loan Response DTO
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

#### 3. Loan History Request DTO
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

#### 4. Loan Document Model
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

#### 5. Account Document Model
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

#### 6. Accounts Repository
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

#### 7. Loans Repository
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

### Success Criteria:

#### Automated Verification:
- [ ] Build succeeds with all model classes: `cd loan-java && ./gradlew compileJava`
- [ ] No compilation errors

#### Manual Verification:
- [ ] Model field names match MongoDB document structure (snake_case)
- [ ] JSON annotations produce correct field names in responses
- [ ] Repository query logic matches Python implementation exactly

**Implementation Note**: After completing this phase and all automated verification passes, pause here for manual confirmation before proceeding to Phase 3.

---

## Phase 3: Business Logic Service

### Overview
Implement the core loan processing business logic that exactly replicates the Python `LoanGeneric` class behavior.

### Changes Required:

#### 1. Loan Service Implementation
**File**: `loan-java/src/main/java/com/martianbank/loan/service/LoanService.java`

```java
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
```

### Success Criteria:

#### Automated Verification:
- [ ] Build succeeds: `cd loan-java && ./gradlew compileJava`
- [ ] No compilation errors in service class

#### Manual Verification:
- [ ] Business logic flow matches Python implementation exactly
- [ ] Log messages match Python log format
- [ ] Approval condition (amount >= 1) matches Python

**Implementation Note**: After completing this phase and all automated verification passes, pause here for manual confirmation before proceeding to Phase 4.

---

## Phase 4: HTTP REST Endpoints

### Overview
Implement Flask-equivalent REST endpoints using Quarkus RESTEasy that match the exact paths, methods, and response formats.

### Changes Required:

#### 1. Loan REST Resource
**File**: `loan-java/src/main/java/com/martianbank/loan/resource/LoanResource.java`

```java
package com.martianbank.loan.resource;

import com.martianbank.loan.model.*;
import com.martianbank.loan.service.LoanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * REST endpoints for loan operations
 * Exact replica of Python Flask routes
 * Reference: loan/loan.py:185-201
 */
@Path("/loan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    private static final Logger LOG = Logger.getLogger(LoanResource.class);

    @Inject
    LoanService loanService;

    /**
     * Process loan request endpoint
     * Matches Python: @app.route("/loan/request", methods=["POST"])
     * Reference: loan/loan.py:187-192
     */
    @POST
    @Path("/request")
    public LoanResponseDto processLoanRequest(LoanRequestDto request) {
        LOG.debugf("Request: %s", request);
        return loanService.processLoanRequest(request);
    }

    /**
     * Get loan history endpoint
     * Matches Python: @app.route("/loan/history", methods=["POST"])
     * Reference: loan/loan.py:195-201
     */
    @POST
    @Path("/history")
    public List<LoanDocument> getLoanHistory(LoanHistoryRequestDto request) {
        LOG.debug("----------------> Request: /loan/history");
        LOG.debugf("Request: %s", request);
        return loanService.getLoanHistory(request.getEmail());
    }
}
```

### Success Criteria:

#### Automated Verification:
- [ ] Build succeeds: `cd loan-java && ./gradlew compileJava`
- [ ] Application starts: `cd loan-java && ./gradlew quarkusDev`
- [ ] Endpoint responds: `curl -X POST http://localhost:50053/loan/request -H "Content-Type: application/json" -d '{"name":"test","email":"test@test.com","account_type":"savings","account_number":"123","govt_id_type":"passport","govt_id_number":"ABC123","loan_type":"personal","loan_amount":100,"interest_rate":5.5,"time_period":"12 months"}'`

#### Manual Verification:
- [ ] Endpoint paths match Python exactly (`/loan/request`, `/loan/history`)
- [ ] HTTP methods match (POST for both)
- [ ] Response JSON format matches Python output

**Implementation Note**: After completing this phase and all automated verification passes, pause here for manual confirmation before proceeding to Phase 5.

---

## Phase 5: gRPC Service Implementation

### Overview
Implement the gRPC service that matches the proto definition and Python gRPC service behavior.

### Changes Required:

#### 1. gRPC Service Implementation
**File**: `loan-java/src/main/java/com/martianbank/loan/grpc/LoanGrpcService.java`

```java
package com.martianbank.loan.grpc;

import com.martianbank.loan.model.*;
import com.martianbank.loan.service.LoanService;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * gRPC service implementation
 * Exact replica of Python LoanService class
 * Reference: loan/loan.py:145-180
 */
@GrpcService
public class LoanGrpcService extends LoanServiceGrpc.LoanServiceImplBase {

    private static final Logger LOG = Logger.getLogger(LoanGrpcService.class);

    @Inject
    LoanService loanService;

    /**
     * Process loan request via gRPC
     * Exact replica of Python: LoanService.ProcessLoanRequest()
     * Reference: loan/loan.py:151-167
     */
    @Override
    public void processLoanRequest(LoanRequest request, StreamObserver<LoanResponse> responseObserver) {
        // Extract fields from gRPC request (matches Python: loan.py:152-161)
        LoanRequestDto dto = new LoanRequestDto();
        dto.setName(request.getName());
        dto.setEmail(request.getEmail());
        dto.setAccountType(request.getAccountType());
        dto.setAccountNumber(request.getAccountNumber());
        dto.setGovtIdType(request.getGovtIdType());
        dto.setGovtIdNumber(request.getGovtIdNumber());
        dto.setLoanType(request.getLoanType());
        dto.setLoanAmount(request.getLoanAmount());
        dto.setInterestRate(request.getInterestRate());
        dto.setTimePeriod(request.getTimePeriod());

        // Process loan request using shared business logic
        // Matches Python: loan.py:164
        LoanResponseDto result = loanService.processLoanRequest(dto);

        // Build gRPC response (matches Python: loan.py:166)
        LoanResponse response = LoanResponse.newBuilder()
            .setApproved(result.isApproved())
            .setMessage(result.getMessage())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Get loan history via gRPC
     * Exact replica of Python: LoanService.getLoanHistory()
     * Reference: loan/loan.py:169-180
     */
    @Override
    public void getLoanHistory(LoansHistoryRequest request, StreamObserver<LoansHistoryResponse> responseObserver) {
        // Extract email from request (matches Python: loan.py:171)
        String email = request.getEmail();

        // Get loan history using shared business logic
        // Matches Python: loan.py:175
        List<LoanDocument> loans = loanService.getLoanHistory(email);

        // Build gRPC response (matches Python: loan.py:177-180)
        LoansHistoryResponse.Builder responseBuilder = LoansHistoryResponse.newBuilder();

        for (LoanDocument loan : loans) {
            Loan grpcLoan = Loan.newBuilder()
                .setName(loan.getName())
                .setEmail(loan.getEmail())
                .setAccountType(loan.getAccountType())
                .setAccountNumber(loan.getAccountNumber())
                .setGovtIdType(loan.getGovtIdType())
                .setGovtIdNumber(loan.getGovtIdNumber())
                .setLoanType(loan.getLoanType())
                .setLoanAmount(loan.getLoanAmount())
                .setInterestRate(loan.getInterestRate())
                .setTimePeriod(loan.getTimePeriod())
                .setStatus(loan.getStatus())
                .setTimestamp(loan.getTimestamp())
                .build();
            responseBuilder.addLoans(grpcLoan);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
```

### Success Criteria:

#### Automated Verification:
- [ ] Build succeeds with gRPC code generation: `cd loan-java && ./gradlew build`
- [ ] Application starts with gRPC enabled: `cd loan-java && ./gradlew quarkusDev`
- [ ] gRPC reflection works: `grpcurl -plaintext localhost:50053 list`

#### Manual Verification:
- [ ] gRPC service methods match proto definition
- [ ] Response format matches Python gRPC implementation
- [ ] Can be called from existing dashboard service

**Implementation Note**: After completing this phase and all automated verification passes, pause here for manual confirmation before proceeding to Phase 6.

---

## Phase 6: Docker and Docker Compose Integration

### Overview
Create Dockerfile for the Java service and update docker-compose.yaml to include the new Java-based loan service.

### Changes Required:

#### 1. Multi-stage Dockerfile
**File**: `loan-java/Dockerfile`

```dockerfile
# Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file.

# Build stage
FROM --platform=linux/amd64 eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Copy source code
COPY src ./src

# Build the application
RUN chmod +x gradlew && ./gradlew build -Dquarkus.package.jar.type=uber-jar -x test

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:25-jre

WORKDIR /app

# Copy the built jar
COPY --from=build /app/build/*-runner.jar /app/loan-service.jar

# Expose port (matches Python service port)
EXPOSE 50053

# Set environment variables
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/loan-service.jar"]
```

#### 2. Gradle Wrapper Files
Create Gradle wrapper (run in loan-java directory):
```bash
cd loan-java && gradle wrapper --gradle-version 8.12
```

This will create:
- `loan-java/gradlew`
- `loan-java/gradlew.bat`
- `loan-java/gradle/wrapper/gradle-wrapper.jar`
- `loan-java/gradle/wrapper/gradle-wrapper.properties`

#### 3. Update Docker Compose
**File**: `docker-compose.yaml`

Add new service definition after the existing `loan` service (around line 115):

```yaml
  loan-java:
    build:
      context: ./loan-java
      dockerfile: Dockerfile
    container_name: loan-java
    hostname: loan
    image: martian-bank-loan-java
    restart: always
    environment:
      DB_URL: mongodb://root:example@mongo:27017
      SERVICE_PROTOCOL: http
    networks:
      - bankapp-network
    profiles:
      - java
```

**Note**: The `hostname: loan` ensures the Java service responds to the same DNS name, and the `profiles: java` allows selective activation.

#### 4. Alternative Docker Compose for Java Migration
**File**: `docker-compose.java.yaml` (override file)

```yaml
# Use with: docker-compose -f docker-compose.yaml -f docker-compose.java.yaml up
services:
  loan:
    # Disable Python loan service
    profiles:
      - disabled

  loan-java:
    build:
      context: ./loan-java
      dockerfile: Dockerfile
    container_name: loan
    hostname: loan
    image: martian-bank-loan-java
    restart: always
    environment:
      DB_URL: mongodb://root:example@mongo:27017
      SERVICE_PROTOCOL: http
    networks:
      - bankapp-network
```

### Success Criteria:

#### Automated Verification:
- [ ] Docker build succeeds: `docker build -t martian-bank-loan-java ./loan-java`
- [ ] Container starts: `docker run -e DB_URL=mongodb://root:example@localhost:27017 -p 50053:50053 martian-bank-loan-java`
- [ ] Full stack starts with Java loan: `docker-compose -f docker-compose.yaml -f docker-compose.java.yaml up --build`

#### Manual Verification:
- [ ] Java loan service responds on port 50053
- [ ] Dashboard can communicate with Java loan service
- [ ] Loan operations work end-to-end through the UI

**Implementation Note**: After completing this phase and all automated verification passes, pause here for final integration testing.

---

## Testing Strategy

### Unit Tests (Optional - Out of Scope)
Not implementing unit tests as per negative scope.

### Integration Tests

#### HTTP Endpoint Tests
Test the following scenarios by comparing Python vs Java responses:

1. **Loan Request - Valid Account**
```bash
curl -X POST http://localhost:50053/loan/request \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@test.com",
    "account_type": "savings",
    "account_number": "12345",
    "govt_id_type": "passport",
    "govt_id_number": "ABC123456",
    "loan_type": "personal",
    "loan_amount": 5000,
    "interest_rate": 5.5,
    "time_period": "12 months"
  }'
```

2. **Loan Request - Invalid Account**
```bash
curl -X POST http://localhost:50053/loan/request \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```
Expected: `{"approved": false, "message": "Email or Account number not found."}`

3. **Loan Request - Amount Below Threshold**
```bash
# (With valid account but loan_amount < 1)
# Expected: {"approved": false, "message": "Loan Rejected"}
```

4. **Loan History**
```bash
curl -X POST http://localhost:50053/loan/history \
  -H "Content-Type: application/json" \
  -d '{"email": "john@test.com"}'
```

### Manual Testing Steps

1. Start the full stack with Java loan service
2. Create a new user account through the UI
3. Apply for a loan through the UI
4. Verify loan appears in account dashboard
5. Check loan history shows the new loan
6. Verify account balance increased by loan amount

---

## Performance Considerations

The Java implementation maintains the same performance characteristics as Python:
- Full collection scan in `getAccountByAccountNumber()` (intentional - matching Python behavior)
- No connection pooling optimizations
- No query index requirements

---

## Migration Notes

### Switching Between Python and Java Services

**To use Python (default):**
```bash
docker-compose up --build
```

**To use Java:**
```bash
docker-compose -f docker-compose.yaml -f docker-compose.java.yaml up --build
```

### Rollback Procedure

If issues occur with Java service:
1. Stop Docker Compose: `docker-compose down`
2. Start with default (Python): `docker-compose up --build`

---

## References

- Original Python implementation: `loan/loan.py`
- Proto definition: `protobufs/loan.proto`
- Docker Compose: `docker-compose.yaml`
- Python Dockerfile: `loan/Dockerfile`
