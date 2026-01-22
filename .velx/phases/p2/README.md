# Plan 01 Tasks: Complete Public API Surface

This plan implements the remaining unauthenticated endpoints to achieve feature parity with the Node.js implementation. The focus is on completing GET /api/atm/{id}, establishing centralized exception handling, and enhancing the existing POST /api/atm/ filtering logic with in-memory AND filtering.

## Components

- **backend**: Core application implementation including controllers, services, repositories, and exception handling
- **testing**: Unit tests, integration tests, and manual validation against Node.js implementation

## Task Summary

### backend
- 9 tasks total
- 6 [AI] automated tasks
- 3 [MANUAL] human-required tasks

### testing
- 4 tasks total
- 3 [AI] automated tasks
- 1 [MANUAL] human-required task

## Execution Order

The plan follows a horizontal layer-by-layer development approach to ensure consistent patterns across all features before moving to the next layer.

### Phase 1: Exception Handling Foundation (Setup)
1. [TASK-001] - [AI] Create custom exception classes
2. [TASK-002] - [AI] Implement GlobalExceptionHandler with @ControllerAdvice

### Phase 2: Controllers Layer (Implement all endpoints)
3. [TASK-003] - [AI] Add GET /api/atm/{id} endpoint to AtmController

### Phase 3: Services Layer (Business logic)
4. [TASK-004] - [AI] Implement AtmService.findById() with ObjectId validation
5. [TASK-005] - [AI] Enhance AtmService.findAtms() with in-memory AND filtering logic

### Phase 4: Testing Layer (Verification)
6. [TASK-006] - [AI] Write unit tests for AtmService.findById()
7. [TASK-007] - [AI] Write unit tests for enhanced filtering logic
8. [TASK-008] - [AI] Write unit tests for GlobalExceptionHandler
9. [TASK-009] - [MANUAL] Perform manual validation against Node.js implementation

### Phase 5: Final Integration (Manual)
10. [TASK-010] - [MANUAL] Validate error response format parity
11. [TASK-011] - [MANUAL] Document testing results and edge cases
12. [TASK-012] - [MANUAL] Validate complete filter combination matrix
13. [TASK-013] - [MANUAL] Review and validate ObjectId handling behavior

## Cross-Component Dependencies

- **TASK-002** (GlobalExceptionHandler) must be completed before **TASK-003** (GET endpoint) to ensure exceptions are properly handled
- **TASK-001** (custom exceptions) must be completed before **TASK-004** (findById implementation) as it throws these exceptions
- **TASK-004** and **TASK-005** must be completed before their corresponding test tasks (**TASK-006**, **TASK-007**)
- All automated tasks (**TASK-001** through **TASK-008**) must be completed before manual validation tasks (**TASK-009** through **TASK-013**)

## Integration Points

### Controllers to Services
- AtmController calls AtmService.findById() for GET /api/atm/{id}
- AtmController calls AtmService.findAtms() for POST /api/atm/ with enhanced filtering

### Services to Repository
- AtmService uses AtmRepository.findById() for single document lookup
- AtmService uses AtmRepository.findAll() for filter-based queries

### Exception Flow
- Service layer throws custom exceptions (InvalidObjectIdException, AtmNotFoundException)
- GlobalExceptionHandler intercepts all controller exceptions
- Handler returns consistent JSON error responses matching Node.js format

### Testing Strategy
- Unit tests validate service logic with mocked repositories
- Unit tests validate exception handler responses
- Manual validation ensures behavioral parity with Node.js implementation
- Side-by-side comparison for all filter combinations and edge cases
