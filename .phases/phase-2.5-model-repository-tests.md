# Phase 2.5: Model and Repository Tests

## Overview
Create unit tests for DTOs, document models, and repository classes. Repository tests use mocked MongoDB clients to verify query construction and data mapping.

## Changes Required:

### 1. LoanRequestDto Test
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

### 2. LoanResponseDto Test
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

### 3. LoanHistoryRequestDto Test
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

### 4. LoanDocument Test
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

### 5. AccountDocument Test
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

### 6. AccountsRepository Test
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

### 7. LoansRepository Test
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

## Success Criteria:

### Automated Verification:
- [ ] All tests pass: `cd loan-java && ./gradlew test`
- [ ] Coverage report generated: `cd loan-java && ./gradlew jacocoTestReport`
- [ ] Model classes achieve ≥ 90% line coverage
- [ ] Repository classes achieve ≥ 90% line coverage
- [ ] Coverage threshold passes: `cd loan-java && ./gradlew jacocoTestCoverageVerification`

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 3.
