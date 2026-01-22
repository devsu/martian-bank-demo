# Testing Tasks

## Overview

Testing components provide comprehensive unit test coverage for the POST /atm/add endpoint, validation logic, and service layer creation functionality. Tests follow TDD approach with Given-When-Then structure established in Phase-01 and Phase-02.

## Prerequisites

- JUnit 5 and Mockito dependencies from Phase-01
- Existing test patterns from Phase-01 and Phase-02
- Implementation tasks completed for corresponding test coverage

## Tasks

### [TASK-013] - [AI] Write unit tests for AtmService.createAtm()

**Why**: Service layer tests validate business logic for ATM creation, duplicate detection, and validation coordination.

**What**:
- Test successful ATM creation returns entity with generated ID
- Test validation failures throw appropriate exceptions
- Test duplicate detection prevents creation
- Test business rule enforcement
- Mock AtmRepository.save() for predictable test outcomes
- Mock duplicate detection queries
- Use Given-When-Then test structure
- Cover happy path and error scenarios

**Testing** (TDD - write these tests first, before implementation):
- Test: createAtm with valid request returns Atm with ID
- Test: createAtm with duplicate name/coordinates throws exception
- Test: createAtm with null required fields throws validation exception
- Test: createAtm calls repository.save() exactly once on success
- Test: createAtm does not call repository.save() when duplicate detected

**Dependencies**: [TASK-006] (AtmService.createAtm() method)

---

### [TASK-014] - [AI] Write unit tests for validation logic

**Why**: Validation tests ensure data integrity requirements are correctly enforced at the DTO level.

**What**:
- Test required field validation triggers errors for missing fields
- Test format validation rejects invalid coordinates, addresses, hours
- Test custom validator logic for business rules
- Test nested object validation cascades correctly
- Test validation error messages are descriptive
- Use Hibernate Validator testing utilities
- Test boundary conditions and edge cases

**Testing** (TDD - write these tests first):
- Test: missing required field (name, location) triggers @NotNull error
- Test: invalid coordinate latitude/longitude rejected
- Test: invalid address format rejected
- Test: invalid atmHours format rejected
- Test: valid complete payload passes all validations
- Test: multiple validation errors collected in single validation pass
- Test: custom validators accept valid values and reject invalid values

**Dependencies**: [TASK-003] (AtmCreateRequest DTO), [TASK-004] (custom validators), [TASK-005] (field-level validation)

---

### [TASK-015] - [AI] Write unit tests for POST /atm/add endpoint

**Why**: Controller tests validate HTTP request/response handling and error propagation for the creation endpoint.

**What**:
- Test valid request returns 201 Created with ATM JSON body
- Test invalid request body returns 400 with validation errors
- Test service exceptions propagate to GlobalExceptionHandler
- Test duplicate creation returns appropriate error response
- Mock AtmService for isolated controller testing
- Use MockMvc for HTTP-level testing
- Verify response status codes and content types
- Verify response body structure matches expectations

**Testing** (TDD - write these tests first):
- Test: POST /atm/add with valid payload returns 201 and ATM JSON
- Test: POST /atm/add with missing required fields returns 400
- Test: POST /atm/add with invalid field formats returns 400
- Test: POST /atm/add with duplicate ATM returns error (status from TASK-001 findings)
- Test: POST /atm/add with malformed JSON returns 400
- Test: POST /atm/add success response includes all ATM fields including generated ID

**Dependencies**: [TASK-008] (POST /atm/add endpoint), [TASK-009] (GlobalExceptionHandler extensions)
