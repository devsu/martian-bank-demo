package com.martianbank.loan.repository;

import com.martianbank.loan.model.LoanDocument;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
import java.util.Date;
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

    @Mock
    MongoCursor<Document> mongoCursor;

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

        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true, false);
        when(mongoCursor.next()).thenReturn(doc);

        List<LoanDocument> loans = loansRepository.findByEmail("john@test.com");

        assertEquals(1, loans.size());
        assertEquals("John Doe", loans.get(0).getName());
        assertEquals("john@test.com", loans.get(0).getEmail());
        assertEquals("Approved", loans.get(0).getStatus());
    }

    @Test
    void findByEmail_returnsEmptyListWhenNoLoansFound() {
        when(mongoCollection.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(false);

        List<LoanDocument> loans = loansRepository.findByEmail("nobody@test.com");

        assertTrue(loans.isEmpty());
    }
}
