# Backend Tasks

## Overview

The backend component implements the remaining public API endpoints and establishes centralized exception handling. This includes creating the GET /api/atm/{id} endpoint, enhancing the existing POST /api/atm/ filtering logic with in-memory AND filtering, and implementing GlobalExceptionHandler for consistent error responses across all endpoints.

## Prerequisites

- Phase-01 must be complete: Spring Boot project structure, MongoDB connection, data seeding, POST /api/atm/ endpoint, RandomizationUtils
- Existing Atm entity model and AtmRepository interface from Phase-01
- MongoDB instance running with seeded atm_data.json

## Tasks

### [TASK-001] - [AI] Create custom exception classes

**What**:
- Create AtmNotFoundException class to signal when requested ATM does not exist
- Create InvalidObjectIdException class to signal malformed MongoDB ObjectId format
- Both exceptions should extend RuntimeException
- Include meaningful error messages with ATM ID context

**Testing** (TDD - write tests first):
- Unit test: Verify exception messages include ATM ID
- Unit test: Verify exceptions are RuntimeException subclasses
- Test cases: constructor parameters, message formatting

**Dependencies**: None

---

### [TASK-002] - [AI] Implement GlobalExceptionHandler with @ControllerAdvice

**Why**: Centralized exception handling ensures all endpoints return consistent error responses matching the Node.js implementation format, eliminating duplicate error handling logic across controllers.

**What**:
- Create GlobalExceptionHandler class with @ControllerAdvice annotation
- Implement @ExceptionHandler for AtmNotFoundException returning 404 status
- Implement @ExceptionHandler for InvalidObjectIdException returning 404 status
- Implement @ExceptionHandler for HttpMessageNotReadableException returning 400 status
- Implement @ExceptionHandler for generic Exception returning 500 status
- Build error response with message and optional stack trace
- Include stack trace only in non-production profiles
- Error response format: `{"message": "...", "stack": "..."}`

**Testing** (TDD - write tests first):
- Unit test: Verify AtmNotFoundException returns 404 with correct message
- Unit test: Verify InvalidObjectIdException returns 404 with correct message
- Unit test: Verify generic Exception returns 500
- Unit test: Verify stack trace included in dev profile only
- Test cases: each exception type, profile-based stack trace inclusion

**Dependencies**: TASK-001 (custom exceptions must exist)

---

### [TASK-003] - [AI] Add GET /api/atm/{id} endpoint to AtmController

**What**:
- Add GET mapping for /api/atm/{id} in AtmController
- Accept id as path variable
- Delegate to AtmService.findById(id)
- Return 200 with ATM JSON if found
- Let GlobalExceptionHandler handle exceptions (404 for not found or invalid ObjectId)

**Testing** (TDD - write tests first):
- Unit test: Controller calls service with correct ID
- Unit test: Controller returns 200 with ATM JSON on success
- Integration test: Full request/response cycle with MockMvc
- Test cases: valid existing ID, valid non-existent ID, invalid ObjectId format

**Dependencies**: TASK-002 (GlobalExceptionHandler), TASK-004 (AtmService.findById)

---

### [TASK-004] - [AI] Implement AtmService.findById() with ObjectId validation

**What**:
- Add findById(String id) method to AtmService
- Validate ObjectId format using ObjectId.isValid() method
- Throw InvalidObjectIdException if format is invalid
- Convert String id to ObjectId for repository query
- Call AtmRepository.findById(ObjectId id)
- Throw AtmNotFoundException if repository returns empty Optional
- Return Atm object if found

**Testing** (TDD - write tests first):
- Unit test: Valid ObjectId found in repository returns Atm
- Unit test: Valid ObjectId not found throws AtmNotFoundException
- Unit test: Invalid ObjectId format throws InvalidObjectIdException
- Unit test: Verify ObjectId.isValid() is used for validation
- Test cases: valid existing, valid non-existent, invalid formats (too short, non-hex, null)

**Dependencies**: TASK-001 (custom exceptions)

---

### [TASK-005] - [AI] Enhance AtmService.findAtms() with in-memory AND filtering logic

**Why**: In-memory filtering mirrors the Node.js implementation pattern exactly, ensuring behavioral parity. Performance optimization is deferred until functional parity is validated.

**What**:
- Update findAtms() method to call AtmRepository.findAll()
- Retrieve all ATMs from MongoDB
- Apply in-memory filtering in Java code based on AtmSearchRequest filters
- When isOpenNow filter provided, filter ATMs where isOpenNow matches
- When isInterPlanetary filter provided, filter ATMs where isInterPlanetary matches
- When both filters provided, apply AND logic (only ATMs matching both conditions)
- When no filters provided, return all ATMs (before randomization)
- Pass filtered results to RandomizationUtils.selectRandom() for up to 4 results

**Testing** (TDD - write tests first):
- Unit test: No filters returns all ATMs (randomized, max 4)
- Unit test: Single filter isOpenNow=true returns only open ATMs
- Unit test: Single filter isInterPlanetary=true returns only interplanetary ATMs
- Unit test: Both filters=true applies AND logic (only ATMs matching both)
- Unit test: Mixed filters (one true, one false) applies AND logic correctly
- Unit test: Verify AtmRepository.findAll() is called (not filtered query)
- Test cases: null filters, single filter, both filters, no matches, multiple matches

**Dependencies**: TASK-004 (for understanding service layer patterns)

---

### [TASK-006] - [MANUAL] Review ObjectId validation strategy

**Why**: ObjectId validation is critical for API stability. Need to ensure the validation approach using MongoDB's native ObjectId.isValid() is correct and handles all edge cases appropriately.

**What**:
- Review ObjectId.isValid() implementation to understand validation logic
- Verify it handles all expected edge cases (null, empty string, wrong length, non-hex characters)
- Confirm that invalid ObjectId returns 404 (not 400) matches Node.js behavior
- Document any edge cases or limitations discovered
- Verify no additional validation is needed beyond ObjectId.isValid()

**Dependencies**: TASK-004 (AtmService.findById implementation)

---

### [TASK-007] - [MANUAL] Coordinate error response format with Node.js implementation

**Why**: Error response format must exactly match Node.js implementation to ensure client compatibility. Any deviation could break existing error handling in UI or dashboard.

**What**:
- Compare error response structure between Java and Node.js implementations
- Verify message field naming and content match
- Verify stack trace field naming and inclusion logic match
- Document any discrepancies found
- If discrepancies exist, determine if GlobalExceptionHandler needs adjustments
- Confirm error response format in Node.js: `{"message": "...", "stack": "..."}`

**Dependencies**: TASK-002 (GlobalExceptionHandler implementation)

---

### [TASK-008] - [MANUAL] Architecture review of in-memory filtering approach

**Why**: In-memory filtering fetches all ATMs from MongoDB on every request. Need to validate this approach is acceptable and document when optimization should occur.

**What**:
- Review the in-memory filtering strategy in AtmService.findAtms()
- Confirm dataset size is small enough for in-memory filtering (currently ~25 ATMs)
- Document performance characteristics and when optimization is needed
- Verify approach matches Node.js implementation exactly
- Identify threshold where MongoDB-level filtering becomes necessary
- Document optimization plan for future phases if dataset grows

**Dependencies**: TASK-005 (enhanced filtering implementation)

---

### [TASK-009] - [MANUAL] Integration testing setup for GET /api/atm/{id}

**What**:
- Verify GET /api/atm/{id} endpoint is accessible via HTTP
- Confirm path variable extraction works correctly
- Test endpoint with Postman or curl using known ObjectIds from seeded data
- Document sample requests and responses for future reference
- Verify Content-Type headers are correct (application/json)

**Dependencies**: TASK-003 (GET endpoint implementation), TASK-004 (service implementation)

---
