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
