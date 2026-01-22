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
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required task

## Execution Order

### Step 1: API Documentation (Contract First)
1. [P3-001] - [AI] Add SpringDoc OpenAPI dependency and configuration
2. [P3-002] - [AI] Document existing endpoints with OpenAPI annotations

### Step 2: Controller Layer (Endpoint Definition)
3. [P3-003] - [AI] Create AtmCreateRequest DTO with Bean Validation
4. [P3-004] - [AI] Add POST /atm/add endpoint to AtmController

### Step 3: Validation Enhancement
5. [P3-005] - [AI] Extend GlobalExceptionHandler for validation errors
6. [P3-006] - [AI] Add field-level validation for nested objects

### Step 4: Service Layer
7. [P3-007] - [MANUAL] Investigate Node.js duplicate handling logic
8. [P3-008] - [AI] Implement AtmService.createAtm() with duplicate detection

### Step 5: Testing
9. [P3-009] - [AI] Write unit tests for AtmService.createAtm()
10. [P3-010] - [AI] Write unit tests for validation logic
11. [P3-011] - [MANUAL] Validate behavioral parity with Node.js service

## Cross-Component Dependencies

- **P3-001 → P3-002**: OpenAPI config needed before annotations
- **P3-003 → P3-004**: DTO needed for controller endpoint
- **P3-004 → P3-005**: Controller exposes validation errors
- **P3-007 → P3-008**: Node.js behavior informs duplicate logic
- **P3-008 → P3-009**: Service implementation needed for tests

## Integration Points

- **OpenAPI Documentation**: /docs UI and /docs.json specification
- **Validation Flow**: Request → Bean Validation → Controller → Service
- **Error Response**: Validation errors formatted by GlobalExceptionHandler
- **Persistence**: AtmService.createAtm() → AtmRepository.save()
