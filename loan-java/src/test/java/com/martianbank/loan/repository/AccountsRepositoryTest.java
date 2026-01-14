package com.martianbank.loan.repository;

import com.martianbank.loan.model.AccountDocument;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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

    @Mock
    MongoCursor<Document> mongoCursor;

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

        when(mongoCollection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(true, false);
        when(mongoCursor.next()).thenReturn(doc);

        AccountDocument account = accountsRepository.getAccountByAccountNumber("12345");

        assertNotNull(account);
        assertEquals("12345", account.getAccountNumber());
        assertEquals("test@email.com", account.getEmailId());
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void getAccountByAccountNumber_returnsNullWhenNotFound() {
        when(mongoCollection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(mongoCursor);
        when(mongoCursor.hasNext()).thenReturn(false);

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
