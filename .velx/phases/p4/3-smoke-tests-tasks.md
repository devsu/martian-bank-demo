# Smoke Tests Tasks

## Overview

Create automated smoke test scripts that validate basic service functionality post-deployment. These tests provide a quick go/no-go decision by verifying service startup, health endpoint, basic API functionality, and data seeding. Tests execute against the running containerized service, not unit or integration tests.

## Prerequisites

- Docker Compose environment running with Java service container
- curl or equivalent HTTP client available in test environment
- MongoDB container operational and accessible

## Tasks

### [TASK-005] - [AI] Create smoke test scripts for service validation

**What**:
- Shell script (smoke-test.sh) for automated post-deployment validation
- Service health check: GET /actuator/health returns 200 with {"status":"UP"}
- Container status verification: Ensure atm-locator-java container running and healthy
- MongoDB connectivity check: Verify service logs show successful database connection
- Basic error handling: Script exits with non-zero status if any check fails
- Clear output messages indicating test pass/fail status
- Execution time under 1 minute for fast feedback loop
- Script accepts base URL as parameter (default: http://localhost:8001)

**Testing** (TDD - write tests first):
- Execute smoke-test.sh against running Java service: All checks pass
- Execute smoke-test.sh with service down: Script fails with clear error message
- Execute smoke-test.sh with invalid URL: Script fails gracefully
- Verify script output includes timestamps and clear pass/fail indicators

**Dependencies**: [TASK-003] (requires Docker Compose environment with Java service)

---

### [TASK-006] - [AI] Implement automated health check and API endpoint tests

**Why**: Validate core API functionality works end-to-end in the containerized environment, ensuring functional parity with the Node.js service.

**What**:
- API endpoint test: POST /api/atm/ with filter criteria {"isOpenNow": true} returns valid ATM array
- ID lookup test: GET /api/atm/{valid-id} returns single ATM object with 200 status
- Error handling test: GET /api/atm/{invalid-id} returns 404 with consistent error format
- Data seeding verification: Query MongoDB to confirm expected document count from atm_data.json
- Response format validation: Verify JSON structure matches expected schema
- Randomization test: Multiple POST requests return different results (up to 4 ATMs)
- Performance baseline: Record response times for basic monitoring (not blocking, informational only)
- Test data extraction: Script extracts valid ATM ID from POST response for GET endpoint test

**Testing**:
- Execute against containerized Java service: All API tests pass
- Compare responses with Node.js service (if available): Structure and behavior match
- Verify error responses match Node.js error format: {"message": "...", "stack": "..."}
- MongoDB document count matches atm_data.json entry count

**Dependencies**: [TASK-005] (builds on basic smoke test infrastructure)

---
