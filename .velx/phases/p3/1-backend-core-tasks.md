# Backend Core Tasks

## Overview

Backend core components implement the POST /atm/add endpoint functionality, including the controller handler, service business logic, and error handling extensions. This builds on the foundation established in Phase-01 and Phase-02.

## Prerequisites

- Phase-02 complete (all public endpoints functional, GlobalExceptionHandler established)
- MongoDB connection configured
- Existing Atm entity model with AtmRepository
- CORS configuration from Phase-01

## Tasks

### [TASK-001] - [MANUAL] Investigate Node.js duplicate handling logic

**Why**: The Java implementation must replicate exact duplicate detection behavior to ensure consistency during migration.

**What**:
- Access the running Node.js ATM Locator service
- Review the POST /atm/add endpoint implementation
- Identify duplicate detection strategy (by name, coordinates, combination of fields)
- Document the exact response format when duplicate is detected
- Test edge cases (case sensitivity, partial matches, coordinate tolerance)
- Capture HTTP status code and error message structure for duplicates

**Dependencies**: Access to Node.js service codebase or running instance

---

### [TASK-002] - [AI] Add SpringDoc OpenAPI dependency to build.gradle

**Why**: SpringDoc OpenAPI provides automatic API documentation generation to match the existing Swagger documentation.

**What**:
- Add springdoc-openapi-starter-webmvc-ui dependency to build.gradle
- Use version compatible with Spring Boot 3.5
- Ensure dependency includes both UI (/docs) and JSON spec (/docs.json) support

**Testing**:
- Verify build succeeds after dependency addition
- Run `./gradlew build` successfully

**Dependencies**: None

---

### [TASK-003] - [AI] Create AtmCreateRequest DTO with Bean Validation annotations

**Why**: DTO captures the creation payload with declarative validation for data integrity.

**What**:
- Create AtmCreateRequest class with all required ATM creation fields
- Match Node.js service's expected payload structure
- Add Bean Validation annotations for required fields (@NotNull, @NotBlank)
- Add format annotations for structured fields (@Pattern for formats)
- Include nested objects for location, coordinates, address, hours
- Support all fields present in the Atm entity model

**Testing** (TDD - write tests first):
- Unit tests validate required field constraints trigger errors
- Unit tests validate optional fields accept null values
- Unit tests validate format constraints reject invalid data

**Dependencies**: None

---

### [TASK-006] - [AI] Implement AtmService.createAtm() method

**Why**: Service layer coordinates business logic for ATM creation including validation and persistence.

**What**:
- Add createAtm(AtmCreateRequest request) method to AtmService
- Convert AtmCreateRequest DTO to Atm entity
- Coordinate duplicate checking before persistence
- Call AtmRepository.save() to persist new ATM
- Return created Atm entity with generated ID
- Handle business rule enforcement

**Testing** (TDD - write tests first):
- Unit test: successful ATM creation returns entity with ID
- Unit test: validation failures throw appropriate exceptions
- Unit test: duplicate detection prevents creation
- Mock AtmRepository.save() for predictable test results

**Dependencies**: [TASK-003] (requires AtmCreateRequest DTO)

---

### [TASK-007] - [AI] Implement duplicate detection logic

**Why**: Prevents duplicate ATM entries in the database following Node.js service patterns.

**What**:
- Implement duplicate checking logic matching findings from [TASK-001]
- Query AtmRepository for potential duplicates based on Node.js detection strategy
- Throw appropriate exception when duplicate detected
- Include duplicate ATM details in exception for error response

**Testing** (TDD - write tests first):
- Unit test: duplicate name triggers detection
- Unit test: duplicate coordinates trigger detection
- Unit test: non-duplicate creation succeeds
- Mock repository queries to simulate duplicate scenarios

**Dependencies**: [TASK-001] (requires Node.js duplicate handling investigation), [TASK-006] (integrates with createAtm method)

---

### [TASK-008] - [AI] Add POST /atm/add endpoint handler to AtmController

**Why**: Controller exposes the creation endpoint to clients, completing the CRUD API surface.

**What**:
- Add POST /atm/add endpoint method to AtmController
- Accept AtmCreateRequest in request body
- Delegate to AtmService.createAtm()
- Return 201 Created status with created ATM JSON in response body
- Use @PostMapping with path "/atm/add"
- Use @RequestBody for payload binding with @Valid for validation

**Testing** (TDD - write tests first):
- Unit test: valid request returns 201 with ATM JSON
- Unit test: invalid request triggers validation error
- Unit test: service exceptions propagate to GlobalExceptionHandler
- Mock AtmService for isolated controller testing

**Dependencies**: [TASK-003] (requires DTO), [TASK-006] (requires service method)

---

### [TASK-009] - [AI] Extend GlobalExceptionHandler for validation errors

**Why**: Consistent error response format across all endpoints including validation failures.

**What**:
- Add @ExceptionHandler for MethodArgumentNotValidException
- Extract field-level validation errors from exception
- Format validation errors matching Node.js field-level error structure
- Include field name, rejected value, and validation message
- Return 400 Bad Request status
- Maintain existing error response format (message and optional stack)

**Testing** (TDD - write tests first):
- Unit test: validation errors formatted with field details
- Unit test: multiple validation errors included in response
- Unit test: error response structure matches Node.js format
- Integration test: POST /atm/add with invalid payload returns 400 with field errors

**Dependencies**: [TASK-003] (requires DTO with validation)

---

### [TASK-010] - [AI] Implement logging for creation endpoint

**Why**: Observability and troubleshooting require consistent logging matching Node.js patterns.

**What**:
- Add log statement when POST /atm/add request received
- Log validation errors with field details
- Log duplicate detection with ATM details
- Log successful creation with created ATM ID
- Match Node.js logging levels and message formats
- Use SLF4J logger in AtmController and AtmService

**Testing**:
- Manual verification: check logs during endpoint testing
- Verify log levels match Node.js implementation
- Ensure sensitive data not logged

**Dependencies**: [TASK-008] (requires endpoint implementation)
