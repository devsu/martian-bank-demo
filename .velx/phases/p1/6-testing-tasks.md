# Testing Tasks

## Overview

Implement essential unit tests for service layer business logic and perform manual validation of endpoint functionality. This component ensures core business logic correctness and behavioral parity with the existing Node.js implementation.

## Prerequisites

- Service layer complete (TASK-005, TASK-006)
- Controller layer complete (TASK-008)
- Application running successfully with seeded data

## Tasks

### [TASK-013] - [AI] Write unit tests for AtmService

**Why**: Validates service layer business logic including filtering and randomization coordination to ensure correctness before manual testing.

**What**:
- Create AtmServiceTest class using JUnit 5 and Mockito
- Use @ExtendWith(MockitoExtension.class) for test setup
- Mock AtmRepository dependency
- Test findAtms() method with multiple scenarios:
  - No filters: Returns results from repository, applies randomization
  - isOpenNow=true filter: Calls repository with correct filter, returns filtered results
  - isInterPlanetary=true filter: Calls repository with correct filter, returns filtered results
  - Both filters: Calls repository with both filters, returns results matching both
  - Empty results: Returns empty list when repository returns no matches
  - Randomization applied: Verify max 4 results returned when more available
- Use AssertJ or standard JUnit assertions
- Follow Arrange-Act-Assert pattern
- Mock repository responses with test data

**Testing** (Verify tests themselves):
- All test cases pass
- Test coverage includes main business logic paths
- Mocked dependencies are properly configured
- Tests are independent and repeatable

**Dependencies**: [TASK-006] - Requires AtmService implementation

---

### [TASK-014] - [MANUAL] Perform manual endpoint validation and behavioral parity testing

**Why**: Human verification ensures the Spring Boot implementation matches existing Node.js behavior for API consumers and catches integration issues not covered by unit tests.

**What**:
- Start local Spring Boot application on port 8001
- Verify application startup completes successfully with data seeding
- Verify health check endpoint: GET /actuator/health returns 200 with {"status": "UP"}
- Test POST /api/atm/ endpoint using Postman or curl:
  - Empty body: Returns randomized ATM list (max 4 items)
  - {"isOpenNow": true}: Returns only open ATMs
  - {"isInterPlanetary": true}: Returns only interplanetary ATMs
  - {"isOpenNow": true, "isInterPlanetary": true}: Returns ATMs matching both filters
- Compare responses with running Node.js service:
  - Response structure matches (same JSON fields)
  - Filter behavior matches (same filtering logic)
  - Randomization behavior matches (max 4 results)
  - HTTP status codes match
  - Error responses match (if any)
- Verify CORS headers present in responses
- Test OPTIONS preflight request
- Document any discrepancies found
- Verify MongoDB data persisted correctly after seeding

**Dependencies**: [TASK-008] - Requires controller endpoint, [TASK-012] - Requires data seeding, [TASK-013] - Unit tests should pass first
