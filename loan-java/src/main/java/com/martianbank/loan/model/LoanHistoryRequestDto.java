package com.martianbank.loan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanHistoryRequestDto {

    @JsonProperty("email")
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
