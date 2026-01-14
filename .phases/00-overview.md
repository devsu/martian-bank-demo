# Loan Microservice Migration to Java 25 - Implementation Plan

## Overview

Migrate the existing Python Loan microservice to Java 25 using Quarkus framework and Gradle build system. The new service will be created in a `loan-java/` directory, maintaining exact functional parity with the Python implementation including identical endpoints, request/response formats, validations, and database operations.

## Current State Analysis

### Existing Python Implementation (`loan/loan.py`)

**Technology Stack:**
- Python 3.x with Flask (HTTP) and grpcio (gRPC)
- MongoDB via pymongo
- Port: 50053
- Protocol switching via `SERVICE_PROTOCOL` environment variable

**HTTP Endpoints:**
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/loan/request` | Process loan application |
| POST | `/loan/history` | Get loan history by email |

**gRPC Service (from `protobufs/loan.proto`):**
```protobuf
service LoanService {
  rpc ProcessLoanRequest(LoanRequest) returns (LoanResponse);
  rpc getLoanHistory(LoansHistoryRequest) returns (LoansHistoryResponse);
}
```

**Database Operations:**
| Collection | Operations | Purpose |
|------------|------------|---------|
| `accounts` | `count_documents`, `find`, `update_one` | Validate account, update balance |
| `loans` | `insert_one`, `find` | Store loan records, query history |

**Business Logic:**
1. Validate account exists by matching `email_id` AND `account_number` in accounts collection
2. Loan approval: auto-approve if `loan_amount >= 1`
3. On approval: add `loan_amount` to account `balance`
4. Store loan record with status "Approved" or "Declined" plus timestamp

### Key Implementation Details to Preserve

**Request Validation (`loan.py:63-68`):**
```python
count = collection_accounts.count_documents({"email_id": email, 'account_number': account_number})
if count == 0:
    return {"approved": False, "message": "Email or Account number not found."}
```

**Approval Logic (`loan.py:132-143`):**
```python
def __approveLoan(self, account, amount):
    if amount < 1:
        return False
    account["balance"] += amount
    collection_accounts.update_one(
        {"account_number": account["account_number"]},
        {"$set": {"balance": account["balance"]}},
    )
    return True
```

**Response Formats:**
- Loan Request: `{"approved": boolean, "message": string}`
- Loan History: Array of loan objects with all fields including `timestamp` as string

## Desired End State

A fully functional Java 25 Quarkus-based Loan microservice that:

1. Exposes identical HTTP endpoints (`/loan/request`, `/loan/history`)
2. Implements identical gRPC service methods
3. Uses the same MongoDB collections with identical queries
4. Returns identical response formats
5. Integrates with Docker Compose alongside existing services
6. Can be swapped with Python version by changing docker-compose configuration

### Verification Criteria:
- All HTTP endpoints return identical JSON responses for identical inputs
- gRPC service works with existing dashboard service (no changes to dashboard)
- Docker container builds and runs successfully
- Service connects to MongoDB and performs identical operations

## What We're NOT Doing

- **Tech Debt**: Not refactoring the inefficient `__getAccount()` full collection scan
- **Security**: Not adding authentication, authorization, or input sanitization
- **Performance**: Not adding indexes or query optimizations
- **Transactions**: Not adding MongoDB transaction support for atomic operations
- **Error Handling**: Not adding comprehensive error handling beyond what exists
- **Integration Tests**: Not adding integration tests with real MongoDB (unit tests with mocks are in scope)
- **API Changes**: Not modifying any endpoint paths, methods, or response formats

## Implementation Approach

Create a new `loan-java/` directory with a Quarkus application using:
- Java 25 (latest LTS features)
- Quarkus 3.x framework
- Gradle (Kotlin DSL) build system
- quarkus-mongodb-panache for MongoDB operations
- quarkus-grpc for gRPC support
- quarkus-rest (RESTEasy Reactive) for HTTP endpoints
