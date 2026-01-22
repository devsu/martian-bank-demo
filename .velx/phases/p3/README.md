# Phase 03 Tasks: ATM Creation Endpoint and Documentation

This phase implements the POST /atm/add endpoint following the **top-to-bottom approach**: OpenAPI documentation first (API contract), then controller with validation, then service logic. Authentication is deferred to a later phase.

## Development Philosophy

**Top-to-Bottom**: Build from API contract down:
1. OpenAPI/Swagger Setup → Define API documentation (contract first)
2. Controller Layer → POST /atm/add endpoint with request validation
3. Validation Layer → DTOs with Bean Validation annotations
4. Service Layer → createAtm() business logic
5. Testing → Unit tests and manual validation

## Components

- **documentation**: SpringDoc OpenAPI configuration (API contract first)
- **controller**: POST /atm/add endpoint handler
- **validation**: AtmCreateRequest DTO with Bean Validation
- **service**: createAtm() implementation with duplicate detection
- **testing**: Unit tests and behavioral parity validation

## Task Summary

### documentation
- 2 tasks total
- 2 [AI] automated tasks

### controller
- 2 tasks total
- 2 [AI] automated tasks

### validation
- 2 tasks total
- 2 [AI] automated tasks

### service
- 2 tasks total
- 1 [AI] automated task
- 1 [MANUAL] human-required task

### testing
- 4 tasks total
- 3 [AI] automated tasks
- 1 [MANUAL] human-required task

## Execution Order

### Phase 1: API Documentation (Contract First)
1. [TASK-001] - [AI] Add SpringDoc OpenAPI dependency and configuration
2. [TASK-002] - [AI] Document existing endpoints with OpenAPI annotations

### Phase 2: Controller Layer (Endpoint Definition)
3. [TASK-003] - [AI] Create AtmCreateRequest DTO with Bean Validation
4. [TASK-004] - [AI] Add POST /atm/add endpoint to AtmController

### Phase 3: Validation Enhancement
5. [TASK-005] - [AI] Extend GlobalExceptionHandler for validation errors
6. [TASK-006] - [AI] Add field-level validation for nested objects

### Phase 4: Service Layer
7. [TASK-007] - [MANUAL] Investigate Node.js duplicate handling logic
8. [TASK-008] - [AI] Implement AtmService.createAtm() with duplicate detection

### Phase 5: Testing
9. [TASK-009] - [AI] Write unit tests for AtmService.createAtm()
10. [TASK-010] - [AI] Write unit tests for validation logic
11. [TASK-011] - [AI] Write integration tests for POST /atm/add
12. [TASK-012] - [MANUAL] Validate behavioral parity with Node.js service

## Cross-Component Dependencies

- **TASK-001 → TASK-002**: OpenAPI config needed before annotations
- **TASK-003 → TASK-004**: DTO needed for controller endpoint
- **TASK-004 → TASK-005**: Controller exposes validation errors
- **TASK-007 → TASK-008**: Node.js behavior informs duplicate logic
- **TASK-008 → TASK-009**: Service implementation needed for tests

## Integration Points

- **OpenAPI Documentation**: /docs UI and /docs.json specification
- **Validation Flow**: Request → Bean Validation → Controller → Service
- **Error Response**: Validation errors formatted by GlobalExceptionHandler
- **Persistence**: AtmService.createAtm() → AtmRepository.save()
