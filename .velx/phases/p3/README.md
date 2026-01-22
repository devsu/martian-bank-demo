# Plan 01 Tasks: ATM Creation Endpoint and Documentation

This plan implements the POST /atm/add endpoint with comprehensive validation and OpenAPI documentation, completing the CRUD API surface for the ATM Locator service. Authentication is deferred to a later phase, keeping this phase focused on core endpoint functionality.

## Components

- **backend-core**: Core Spring Boot application components (controller handler, service logic, repository operations, DTOs, error handling)
- **validation**: Data validation logic with Bean Validation annotations and custom validators
- **documentation**: SpringDoc OpenAPI configuration for API documentation
- **testing**: Unit test infrastructure and test cases

## Task Summary

### backend-core
- 7 tasks total
- 6 [AI] automated tasks
- 1 [MANUAL] human-required task

### validation
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required task

### documentation
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### testing
- 3 tasks total
- 3 [AI] automated tasks
- 0 [MANUAL] human-required tasks

## Execution Order

1. **Setup Phase**:
   - [TASK-001] - [MANUAL] Investigate Node.js duplicate handling logic
   - [TASK-002] - [AI] Add SpringDoc OpenAPI dependency to build.gradle

2. **DTO and Validation Layer**:
   - [TASK-003] - [AI] Create AtmCreateRequest DTO with Bean Validation annotations
   - [TASK-004] - [AI] Create custom validators for complex business rules
   - [TASK-005] - [AI] Implement field-level validation for structured fields

3. **Service and Repository Layer**:
   - [TASK-006] - [AI] Implement AtmService.createAtm() method
   - [TASK-007] - [AI] Implement duplicate detection logic

4. **Controller and Error Handling**:
   - [TASK-008] - [AI] Add POST /atm/add endpoint handler to AtmController
   - [TASK-009] - [AI] Extend GlobalExceptionHandler for validation errors
   - [TASK-010] - [AI] Implement logging for creation endpoint

5. **Documentation**:
   - [TASK-011] - [AI] Configure SpringDoc OpenAPI
   - [TASK-012] - [AI] Document all endpoints with OpenAPI annotations

6. **Testing Phase**:
   - [TASK-013] - [AI] Write unit tests for AtmService.createAtm()
   - [TASK-014] - [AI] Write unit tests for validation logic
   - [TASK-015] - [AI] Write unit tests for POST /atm/add endpoint

7. **Manual Validation**:
   - [TASK-016] - [MANUAL] Validate behavioral parity with Node.js service

## Cross-Component Dependencies

- [TASK-003] must complete before [TASK-004] and [TASK-005] (DTO needed for validators)
- [TASK-006] depends on [TASK-003] completion (service uses DTO)
- [TASK-007] depends on [TASK-001] findings (duplicate logic matches Node.js)
- [TASK-008] depends on [TASK-003] and [TASK-006] (controller uses DTO and service)
- [TASK-009] depends on [TASK-003] (exception handler formats validation errors)
- [TASK-013], [TASK-014], [TASK-015] depend on corresponding implementation tasks
- [TASK-016] depends on all implementation tasks completion

## Integration Points

The POST /atm/add endpoint integrates with:
- Existing AtmRepository for persistence (save operation)
- Existing GlobalExceptionHandler for consistent error responses
- Existing CORS configuration for cross-origin request support
- Existing MongoDB connection from Phase-01
- Existing GET endpoints for verification of created ATMs

Testing strategy validates created ATMs through the existing GET /api/atm/{id} endpoint to ensure end-to-end persistence.
