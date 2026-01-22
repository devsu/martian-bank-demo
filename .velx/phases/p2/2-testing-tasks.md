# Testing Tasks

## Overview

Testing validates the implementation of Phase-02 endpoints and ensures behavioral parity with the Node.js implementation. This includes unit tests for service logic and exception handling, as well as comprehensive manual validation against the running Node.js service.

## Prerequisites

- Phase-02 backend implementation complete (TASK-001 through TASK-005)
- Node.js implementation running and accessible for side-by-side comparison
- MongoDB instance with seeded atm_data.json
- Known valid ObjectIds from seeded data for testing

## Tasks

### [TASK-010] - [AI] Write unit tests for AtmService.findById()

**What**:
- Create AtmServiceTest class with Mockito for AtmRepository mocking
- Test valid ObjectId found in repository returns Atm object
- Test valid ObjectId not found throws AtmNotFoundException
- Test invalid ObjectId format throws InvalidObjectIdException
- Test null ID throws InvalidObjectIdException
- Test edge cases: empty string, too short, too long, non-hexadecimal characters
- Verify ObjectId.isValid() validation occurs before repository call
- Mock AtmRepository.findById() to return Optional.of(atm) or Optional.empty()

**Testing**:
- Unit tests should use Mockito @Mock and @InjectMocks
- Use AssertJ assertions for readability
- Test both happy path and exception scenarios
- Verify exception messages include the invalid ID

**Dependencies**: Backend TASK-004 (AtmService.findById implementation)

---

### [TASK-011] - [AI] Write unit tests for enhanced filtering logic

**What**:
- Add test methods to AtmServiceTest for findAtms() filtering scenarios
- Test no filters provided returns all ATMs (randomized, max 4)
- Test isOpenNow=true filter returns only open ATMs
- Test isInterPlanetary=true filter returns only interplanetary ATMs
- Test both filters=true applies AND logic (only ATMs matching both conditions)
- Test mixed filters (isOpenNow=true, isInterPlanetary=false) applies AND correctly
- Test no matches returns empty list
- Mock AtmRepository.findAll() to return test ATM list
- Verify in-memory filtering logic (not repository-level filtering)

**Testing**:
- Create helper methods to build test ATM objects with various properties
- Use parameterized tests for filter combinations if beneficial
- Verify RandomizationUtils.selectRandom() is called with filtered results
- Assert correct number of ATMs returned (max 4)

**Dependencies**: Backend TASK-005 (enhanced filtering implementation)

---

### [TASK-012] - [AI] Write unit tests for GlobalExceptionHandler

**What**:
- Create GlobalExceptionHandlerTest class
- Test handleAtmNotFound() returns 404 status with correct message
- Test handleInvalidObjectId() returns 404 status with correct message
- Test handleBadRequest() returns 400 status for HttpMessageNotReadableException
- Test handleGenericException() returns 500 status for unexpected exceptions
- Test stack trace included when active profile is "dev"
- Test stack trace excluded when active profile is "production"
- Verify error response format matches: `{"message": "...", "stack": "..."}`
- Mock active profile using @TestPropertySource or ReflectionTestUtils

**Testing**:
- Test ResponseEntity status codes and body content
- Verify error message extraction from exceptions
- Test profile-based stack trace inclusion logic
- Use Map assertions to verify JSON structure

**Dependencies**: Backend TASK-002 (GlobalExceptionHandler implementation)

---

### [TASK-013] - [MANUAL] Perform manual validation against Node.js implementation

**Why**: Automated tests verify logic correctness, but manual validation ensures complete behavioral parity with the Node.js implementation. This catches subtle differences in error messages, response formatting, or edge case handling that unit tests might miss.

**What**:
- Start both Java and Node.js implementations locally
- Run side-by-side comparison for all scenarios:
  - GET /api/atm/{valid-existing-id} - compare response structure and data
  - GET /api/atm/{valid-non-existent-id} - verify 404 response
  - GET /api/atm/{invalid-objectid-format} - verify 404 response (not 400)
  - POST /api/atm/ with no filters - verify randomized results (up to 4)
  - POST /api/atm/ with isOpenNow=true only - verify filtering
  - POST /api/atm/ with isInterPlanetary=true only - verify filtering
  - POST /api/atm/ with both filters=true - verify AND logic
  - POST /api/atm/ with isOpenNow=true, isInterPlanetary=false - verify AND logic
  - POST /api/atm/ with both filters=false - verify behavior
- Compare HTTP status codes between implementations
- Compare JSON response structures field-by-field
- Compare error response format exactly
- Document any discrepancies found
- Use Postman, curl, or similar tool for requests
- Record sample requests/responses for documentation

**Dependencies**: Backend TASK-001 through TASK-005, Testing TASK-010 through TASK-012 (all implementation and unit tests complete)

---
