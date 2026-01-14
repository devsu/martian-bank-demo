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
