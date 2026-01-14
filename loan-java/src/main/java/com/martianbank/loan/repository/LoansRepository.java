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
