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
