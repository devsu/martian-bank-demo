# Phase 7: Final Manual Verification

## Overview
This phase consolidates all manual verification steps that were previously distributed across phases. Complete this phase only after all implementation phases (1-6) have passed their automated verification.

## Project Structure Verification (Phase 1)
- [ ] Project structure matches the specified layout
- [ ] All configuration files are in place
- [ ] No compilation errors in IDE

## Test Reports Verification (Phase 1.5)
- [ ] Test report viewable at `loan-java/build/reports/tests/test/index.html`
- [ ] Coverage report viewable at `loan-java/build/reports/jacoco/test/html/index.html`
- [ ] LoanApplication class shows ≥ 90% line coverage

## Model and Repository Verification (Phase 2 & 2.5)
- [ ] Model field names match MongoDB document structure (snake_case)
- [ ] JSON annotations produce correct field names in responses
- [ ] Repository query logic matches Python implementation exactly
- [ ] All model tests verify JSON serialization with snake_case field names
- [ ] Repository tests verify correct MongoDB query construction
- [ ] Mocks properly isolate tests from actual database

## Business Logic Verification (Phase 3 & 3.5)
- [ ] Business logic flow matches Python implementation exactly
- [ ] Log messages match Python log format
- [ ] Approval condition (amount >= 1) matches Python
- [ ] Tests cover all business logic branches (approval, rejection reasons)
- [ ] Tests verify exact behavior match with Python implementation
- [ ] All repository interactions are properly verified

## REST Endpoint Verification (Phase 4 & 4.5)
- [ ] Endpoint paths match Python exactly (`/loan/request`, `/loan/history`)
- [ ] HTTP methods match (POST for both)
- [ ] Response JSON format matches Python output
- [ ] Tests verify correct endpoint paths (`/loan/request`, `/loan/history`)
- [ ] Tests verify correct HTTP methods (POST)
- [ ] Tests verify JSON response format matches Python implementation
- [ ] Tests verify snake_case field names in responses

## gRPC Service Verification (Phase 5 & 5.5)
- [ ] gRPC service methods match proto definition
- [ ] Response format matches Python gRPC implementation
- [ ] Can be called from existing dashboard service
- [ ] Tests verify correct field mapping from gRPC request to DTO
- [ ] Tests verify correct field mapping from LoanDocument to gRPC Loan
- [ ] Tests verify stream completion behavior

## Docker Integration Verification (Phase 6)
- [ ] Java loan service responds on port 50053
- [ ] Dashboard can communicate with Java loan service
- [ ] Loan operations work end-to-end through the UI

## End-to-End Testing
1. Start the full stack with Java loan service
2. Create a new user account through the UI
3. Apply for a loan through the UI
4. Verify loan appears in account dashboard
5. Check loan history shows the new loan
6. Verify account balance increased by loan amount

## API Parity Testing
Compare responses between Python and Java services:

### 1. Loan Request - Valid Account
```bash
curl -X POST http://localhost:50053/loan/request \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@test.com",
    "account_type": "savings",
    "account_number": "12345",
    "govt_id_type": "passport",
    "govt_id_number": "ABC123456",
    "loan_type": "personal",
    "loan_amount": 5000,
    "interest_rate": 5.5,
    "time_period": "12 months"
  }'
```

### 2. Loan Request - Invalid Account
```bash
curl -X POST http://localhost:50053/loan/request \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@invalid.com",
    "account_type": "savings",
    "account_number": "99999",
    "govt_id_type": "passport",
    "govt_id_number": "XYZ789",
    "loan_type": "personal",
    "loan_amount": 1000,
    "interest_rate": 4.5,
    "time_period": "6 months"
  }'
```
Expected: `{"approved": false, "message": "Email or Account number not found."}`

### 3. Loan History
```bash
curl -X POST http://localhost:50053/loan/history \
  -H "Content-Type: application/json" \
  -d '{"email": "john@test.com"}'
```
