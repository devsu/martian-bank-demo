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
- **Integration Tests**: Not adding integration tests with real MongoDB (unit tests with mocks are in scope)
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

## Testing Strategy

### Framework and Tools
| Tool | Purpose |
|------|---------|
| JUnit 5 | Test framework (via `quarkus-junit5`) |
| Mockito | Mocking MongoDB clients and external dependencies |
| REST Assured | HTTP endpoint testing |
| JaCoCo | Code coverage measurement and reporting |

### Coverage Requirements
- **Minimum Line Coverage**: 90%
- **Enforcement**: JaCoCo Gradle plugin with `violationRules` to fail build if coverage drops below threshold
- **Reporting**: HTML and XML reports generated in `build/reports/jacoco/`

### Testing Approach
1. **Unit Tests Only**: All tests use mocks for external dependencies (MongoDB, etc.)
2. **No Integration Tests**: Tests do not connect to real databases or external services
3. **Phase-Based Testing**: Each coding phase is followed by a dedicated testing phase
4. **Incremental Coverage**: Each testing phase must achieve 90% coverage for the code introduced in its corresponding coding phase

### Test Directory Structure
```
loan-java/src/test/java/com/martianbank/loan/
├── model/           # DTO/Document model tests
├── repository/      # Repository tests with mocked MongoClient
├── service/         # Business logic tests with mocked repositories
├── resource/        # REST endpoint tests
└── grpc/            # gRPC service tests
```

### Running Tests
```bash
# Run all tests with coverage
cd loan-java && ./gradlew test jacocoTestReport

# Check coverage threshold (fails if below 90%)
cd loan-java && ./gradlew jacocoTestCoverageVerification

# View coverage report
open loan-java/build/reports/jacoco/test/html/index.html
```

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
    jacoco
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
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
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

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 1.5 for testing.

---

## Phase 1.5: Project Setup Tests

### Overview
Create unit tests for the project setup to verify the application context loads correctly and basic configuration is properly applied. This phase establishes the test infrastructure.

### Changes Required:

#### 1. Application Context Test
**File**: `loan-java/src/test/java/com/martianbank/loan/LoanApplicationTest.java`

```java
package com.martianbank.loan;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class LoanApplicationTest {

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.http.port")
    int httpPort;

    @Test
    void applicationContextLoads() {
        // If we get here, the application context loaded successfully
        assertTrue(true, "Application context should load");
    }

    @Test
    void applicationNameIsConfigured() {
        assertEquals("loan-service", applicationName);
    }

    @Test
    void httpPortIsConfigured() {
        assertEquals(50053, httpPort);
    }
}
```

#### 2. Test Resources Configuration
**File**: `loan-java/src/test/resources/application.properties`

```properties
# Test configuration - override production settings
quarkus.application.name=loan-service

# Use test port to avoid conflicts
quarkus.http.port=50053
quarkus.http.test-port=8081

# Mock MongoDB - disable actual connection for unit tests
quarkus.mongodb.connection-string=mongodb://localhost:27017
quarkus.mongodb.database=bank

# Disable gRPC server in tests (will be tested separately)
quarkus.grpc.server.use-separate-server=false

# Reduce logging noise in tests
quarkus.log.level=WARN
quarkus.log.category."com.martianbank".level=DEBUG
```

### Success Criteria:

#### Automated Verification:
- [ ] Tests pass: `cd loan-java && ./gradlew test`
- [ ] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [ ] Phase 1 code coverage ≥ 90%: `cd loan-java && ./gradlew jacocoTestCoverageVerification`

#### Manual Verification:
- [ ] Test report viewable at `loan-java/build/reports/tests/test/index.html`
- [ ] Coverage report viewable at `loan-java/build/reports/jacoco/test/html/index.html`
- [ ] LoanApplication class shows ≥ 90% line coverage

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

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 2.5 for testing.

---

## Phase 2.5: Model and Repository Tests

### Overview
Create unit tests for DTOs, document models, and repository classes. Repository tests use mocked MongoDB clients to verify query construction and data mapping.

### Changes Required:

#### 1. LoanRequestDto Test
**File**: `loan-java/src/test/java/com/martianbank/loan/model/LoanRequestDtoTest.java`

```java
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
```

#### 2. LoanResponseDto Test
**File**: `loan-java/src/test/java/com/martianbank/loan/model/LoanResponseDtoTest.java`

```java
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
```

#### 3. LoanHistoryRequestDto Test
**File**: `loan-java/src/test/java/com/martianbank/loan/model/LoanHistoryRequestDtoTest.java`

```java
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
```

#### 4. LoanDocument Test
**File**: `loan-java/src/test/java/com/martianbank/loan/model/LoanDocumentTest.java`

```java
package com.martianbank.loan.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanDocumentTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
```

#### 5. AccountDocument Test
**File**: `loan-java/src/test/java/com/martianbank/loan/model/AccountDocumentTest.java`

```java
package com.martianbank.loan.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountDocumentTest {

    @Test
    void gettersAndSettersWorkCorrectly() {
        AccountDocument doc = new AccountDocument();

        doc.setAccountNumber("ACC123");
        doc.setEmailId("user@test.com");
        doc.setBalance(1000.50);
        doc.setName("Test User");
        doc.setAccountType("savings");

        assertEquals("ACC123", doc.getAccountNumber());
        assertEquals("user@test.com", doc.getEmailId());
        assertEquals(1000.50, doc.getBalance());
        assertEquals("Test User", doc.getName());
        assertEquals("savings", doc.getAccountType());
    }

    @Test
    void defaultBalanceIsZero() {
        AccountDocument doc = new AccountDocument();
        doc.setBalance(0.0);

        assertEquals(0.0, doc.getBalance());
    }
}
```

#### 6. AccountsRepository Test
**File**: `loan-java/src/test/java/com/martianbank/loan/repository/AccountsRepositoryTest.java`

```java
package com.martianbank.loan.repository;

import com.martianbank.loan.model.AccountDocument;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsRepositoryTest {

    @Mock
    MongoClient mongoClient;

    @Mock
    MongoDatabase mongoDatabase;

    @Mock
    MongoCollection<Document> mongoCollection;

    @Mock
    FindIterable<Document> findIterable;

    @InjectMocks
    AccountsRepository accountsRepository;

    @BeforeEach
    void setUp() {
        when(mongoClient.getDatabase("bank")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("accounts")).thenReturn(mongoCollection);
    }

    @Test
    void countByEmailIdAndAccountNumber_returnsCount() {
        when(mongoCollection.countDocuments(any(Bson.class))).thenReturn(1L);

        long count = accountsRepository.countByEmailIdAndAccountNumber("test@email.com", "12345");

        assertEquals(1L, count);
        verify(mongoCollection).countDocuments(any(Bson.class));
    }

    @Test
    void countByEmailIdAndAccountNumber_returnsZeroWhenNotFound() {
        when(mongoCollection.countDocuments(any(Bson.class))).thenReturn(0L);

        long count = accountsRepository.countByEmailIdAndAccountNumber("notfound@email.com", "99999");

        assertEquals(0L, count);
    }

    @Test
    void getAccountByAccountNumber_returnsAccountWhenFound() {
        Document doc = new Document()
            .append("account_number", "12345")
            .append("email_id", "test@email.com")
            .append("balance", 1000.0)
            .append("name", "Test User")
            .append("account_type", "savings");

        Iterator<Document> iterator = Arrays.asList(doc).iterator();
        when(mongoCollection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(iterator);

        AccountDocument account = accountsRepository.getAccountByAccountNumber("12345");

        assertNotNull(account);
        assertEquals("12345", account.getAccountNumber());
        assertEquals("test@email.com", account.getEmailId());
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void getAccountByAccountNumber_returnsNullWhenNotFound() {
        Iterator<Document> iterator = Arrays.<Document>asList().iterator();
        when(mongoCollection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(iterator);

        AccountDocument account = accountsRepository.getAccountByAccountNumber("nonexistent");

        assertNull(account);
    }

    @Test
    void updateBalance_updatesSuccessfully() {
        UpdateResult updateResult = mock(UpdateResult.class);
        when(mongoCollection.updateOne(any(Bson.class), any(Bson.class))).thenReturn(updateResult);

        accountsRepository.updateBalance("12345", 2000.0);

        verify(mongoCollection).updateOne(any(Bson.class), any(Bson.class));
    }
}
```

#### 7. LoansRepository Test
**File**: `loan-java/src/test/java/com/martianbank/loan/repository/LoansRepositoryTest.java`

```java
package com.martianbank.loan.repository;

import com.martianbank.loan.model.LoanDocument;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoansRepositoryTest {

    @Mock
    MongoClient mongoClient;

    @Mock
    MongoDatabase mongoDatabase;

    @Mock
    MongoCollection<Document> mongoCollection;

    @Mock
    FindIterable<Document> findIterable;

    @InjectMocks
    LoansRepository loansRepository;

    @BeforeEach
    void setUp() {
        when(mongoClient.getDatabase("bank")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("loans")).thenReturn(mongoCollection);
    }

    @Test
    void insertLoan_insertsDocumentWithAllFields() {
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
        loan.setTimestampDate(LocalDateTime.now());

        InsertOneResult insertResult = mock(InsertOneResult.class);
        when(mongoCollection.insertOne(any(Document.class))).thenReturn(insertResult);

        loansRepository.insertLoan(loan);

        ArgumentCaptor<Document> docCaptor = ArgumentCaptor.forClass(Document.class);
        verify(mongoCollection).insertOne(docCaptor.capture());

        Document insertedDoc = docCaptor.getValue();
        assertEquals("John Doe", insertedDoc.getString("name"));
        assertEquals("john@test.com", insertedDoc.getString("email"));
        assertEquals("12345", insertedDoc.getString("account_number"));
        assertEquals("Approved", insertedDoc.getString("status"));
        assertEquals(5000.0, insertedDoc.getDouble("loan_amount"));
    }

    @Test
    void findByEmail_returnsLoansWhenFound() {
        Document doc = new Document()
            .append("name", "John Doe")
            .append("email", "john@test.com")
            .append("account_type", "savings")
            .append("account_number", "12345")
            .append("govt_id_type", "passport")
            .append("govt_id_number", "ABC123")
            .append("loan_type", "personal")
            .append("loan_amount", 5000.0)
            .append("interest_rate", 5.5)
            .append("time_period", "12 months")
            .append("status", "Approved")
            .append("timestamp", new Date());

        Iterator<Document> iterator = Arrays.asList(doc).iterator();
        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(iterator);

        List<LoanDocument> loans = loansRepository.findByEmail("john@test.com");

        assertEquals(1, loans.size());
        assertEquals("John Doe", loans.get(0).getName());
        assertEquals("john@test.com", loans.get(0).getEmail());
        assertEquals("Approved", loans.get(0).getStatus());
    }

    @Test
    void findByEmail_returnsEmptyListWhenNoLoansFound() {
        Iterator<Document> iterator = Arrays.<Document>asList().iterator();
        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(iterator);

        List<LoanDocument> loans = loansRepository.findByEmail("nobody@test.com");

        assertTrue(loans.isEmpty());
    }
}
```

### Success Criteria:

#### Automated Verification:
- [ ] All tests pass: `cd loan-java && ./gradlew test`
- [ ] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [ ] Model classes achieve ≥ 90% line coverage
- [ ] Repository classes achieve ≥ 90% line coverage
- [ ] Coverage threshold passes: `cd loan-java && ./gradlew jacocoTestCoverageVerification`

#### Manual Verification:
- [ ] All model tests verify JSON serialization with snake_case field names
- [ ] Repository tests verify correct MongoDB query construction
- [ ] Mocks properly isolate tests from actual database

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

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 3.5 for testing.

---

## Phase 3.5: Business Logic Tests

### Overview
Create unit tests for the LoanService class, testing all business logic paths with mocked repository dependencies.

### Changes Required:

#### 1. LoanService Test
**File**: `loan-java/src/test/java/com/martianbank/loan/service/LoanServiceTest.java`

```java
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
```

### Success Criteria:

#### Automated Verification:
- [ ] All tests pass: `cd loan-java && ./gradlew test`
- [ ] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [ ] LoanService class achieves ≥ 90% line coverage
- [ ] Coverage threshold passes: `cd loan-java && ./gradlew jacocoTestCoverageVerification`

#### Manual Verification:
- [ ] Tests cover all business logic branches (approval, rejection reasons)
- [ ] Tests verify exact behavior match with Python implementation
- [ ] All repository interactions are properly verified

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

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 4.5 for testing.

---

## Phase 4.5: REST Endpoint Tests

### Overview
Create unit tests for REST endpoints using REST Assured and mocked service layer.

### Changes Required:

#### 1. LoanResource Test
**File**: `loan-java/src/test/java/com/martianbank/loan/resource/LoanResourceTest.java`

```java
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
```

### Success Criteria:

#### Automated Verification:
- [ ] All tests pass: `cd loan-java && ./gradlew test`
- [ ] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [ ] LoanResource class achieves ≥ 90% line coverage
- [ ] Coverage threshold passes: `cd loan-java && ./gradlew jacocoTestCoverageVerification`

#### Manual Verification:
- [ ] Tests verify correct endpoint paths (`/loan/request`, `/loan/history`)
- [ ] Tests verify correct HTTP methods (POST)
- [ ] Tests verify JSON response format matches Python implementation
- [ ] Tests verify snake_case field names in responses

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

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 5.5 for testing.

---

## Phase 5.5: gRPC Service Tests

### Overview
Create unit tests for the gRPC service implementation, testing request/response mapping and service delegation.

### Changes Required:

#### 1. LoanGrpcService Test
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

### Success Criteria:

#### Automated Verification:
- [ ] All tests pass: `cd loan-java && ./gradlew test`
- [ ] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [ ] LoanGrpcService class achieves ≥ 90% line coverage
- [ ] Coverage threshold passes: `cd loan-java && ./gradlew jacocoTestCoverageVerification`
- [ ] Overall project coverage ≥ 90%: Check `build/reports/jacoco/test/html/index.html`

#### Manual Verification:
- [ ] Tests verify correct field mapping from gRPC request to DTO
- [ ] Tests verify correct field mapping from LoanDocument to gRPC Loan
- [ ] Tests verify stream completion behavior

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
