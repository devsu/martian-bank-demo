# Testing Tasks

## Overview

Establish comprehensive testing infrastructure with JUnit 5, Mockito, and MockMvc. Create reusable test fixtures and implement extensive test coverage across all layers following TDD principles with >80% coverage target.

## Prerequisites

- Maven dependencies configured with test framework ([TASK-003])
- Model entities and DTOs created ([TASK-004], [TASK-005], [TASK-007])

## Tasks

### [TASK-008] - [AI] Set up test infrastructure with JUnit 5 and Mockito

**Why**: Early test infrastructure setup enables TDD approach for all subsequent implementation tasks.

**What**:
- Verify spring-boot-starter-test dependency includes JUnit 5, Mockito, AssertJ, Hamcrest
- Configure JUnit 5 platform in Maven Surefire plugin for test execution
- Create base test configuration class with common annotations
- Set up MockMvc infrastructure for controller testing
- Configure test logging with dedicated logback-test.xml
- Set up JaCoCo plugin for code coverage reporting with 80% threshold
- Create test profile (application-test.yml) with embedded test configuration
- Organize test package structure mirroring main source packages

**Testing** (TDD - write tests first):
- Meta test: Verify mvn test executes successfully (even with empty test)
- Meta test: JaCoCo report generates in target/site/jacoco
- Meta test: Test logging configuration outputs to console
- Meta test: Verify test-specific application.yml loaded during tests

**Dependencies**: [TASK-003] Maven dependencies

---

### [TASK-009] - [AI] Create test fixtures for ATM entities and DTOs

**Why**: Reusable test fixtures ensure consistent test data and reduce test maintenance overhead.

**What**:
- Create ATMFixtures utility class with static factory methods
- Provide fixtures for common ATM scenarios: open 24/7, business hours only, closed
- Create sample coordinates: equator, poles, prime meridian, various cities
- Create sample timings: weekday hours, weekend hours, holiday hours
- Provide valid and invalid DTO examples for validation testing
- Create builders for flexible test data customization
- Include edge cases: null timings, zero numberOfATMs, extreme coordinates
- Create ObjectId fixtures with valid and invalid formats

**Testing** (TDD - write tests first):
- Unit test: All fixture methods return valid entities
- Unit test: Builder pattern allows field customization
- Unit test: Edge case fixtures represent actual edge conditions
- Unit test: Fixtures cover happy path and error scenarios

**Dependencies**: [TASK-004] ATM entity, [TASK-005] value objects, [TASK-007] DTOs

---

### [TASK-024] - [AI] Create controller tests with MockMvc

**Why**: Controller tests validate HTTP layer behavior including request validation, response formatting, and status codes.

**What**:
- Create ATMControllerTest with @WebMvcTest(ATMController.class)
- Mock ATMService using @MockBean
- Inject MockMvc for HTTP request simulation
- Test GET /api/atms/nearby with valid parameters returns HTTP 200 and JSON array
- Test validation errors return HTTP 400 with field-level details
- Test GET /atm/{id} with valid ID returns HTTP 200 with complete ATM
- Test GET /atm/{id} with invalid ID returns HTTP 404
- Test POST /atm/add with valid payload returns HTTP 201 with Location header
- Verify response JSON structure matches expected format
- Confirm ObjectId appears in $oid wrapper format
- Test Content-Type headers and Accept headers

**Testing** (TDD - write tests first):
- Controller test suite covers all endpoints
- Tests use MockMvc.perform() with assertions on status, content, headers
- Tests verify service method calls with Mockito.verify()
- Tests validate JSON structure with JsonPath matchers
- Tests cover happy paths and all error scenarios

**Dependencies**: [TASK-021], [TASK-022], [TASK-023] controller implementation, [TASK-008] test infrastructure

---

### [TASK-025] - [AI] Implement validation tests for all DTOs

**Why**: Comprehensive validation testing ensures input constraints work correctly and provide clear error messages.

**What**:
- Create DTO validation test classes using Hibernate Validator test harness
- Create Validator instance from ValidatorFactory
- Test NearbySearchRequest: latitude in range, longitude in range, radius positive
- Test ATMCreateRequest: required fields not null, nested objects valid
- Test Address validation: required fields, zip code format
- Test Coordinates validation: latitude/longitude ranges
- Test Timings validation: string format for operating hours
- Verify validation messages are clear and actionable
- Test violation count matches expected constraint violations
- Test valid DTOs pass validation without violations

**Testing** (TDD - write tests first):
- Validation test: Each constraint violation produces expected message
- Validation test: Multiple violations reported for single object
- Validation test: Nested object validation cascades with @Valid
- Validation test: Valid objects have zero violations
- Validation test: Null values handled per @NotNull constraints

**Dependencies**: [TASK-007] DTOs with validation annotations, [TASK-008] test infrastructure
