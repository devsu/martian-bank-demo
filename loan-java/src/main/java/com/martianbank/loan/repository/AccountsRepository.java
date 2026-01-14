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
