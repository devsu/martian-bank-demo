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
