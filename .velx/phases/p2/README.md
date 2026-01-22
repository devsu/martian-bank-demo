# Phase 02 Tasks: Complete Public API Surface

This phase implements the remaining unauthenticated endpoints following the **top-to-bottom approach**: exception handling (API surface), then controllers, then services. Focus on GET /api/atm/{id} endpoint and enhanced filtering logic.

## Development Philosophy

**Top-to-Bottom**: Build from API surface down:
1. Exception Handling → Define error response format (client-facing)
2. Controller Layer → Add GET endpoint with path variable
3. Service Layer → Implement business logic for findById and enhanced filtering
4. Testing → Validate behavioral parity

## Components

- **exception-handling**: Global exception handler and custom exceptions (API surface)
- **controller**: GET /api/atm/{id} endpoint
- **service**: findById implementation and enhanced filtering
- **testing**: Unit tests and manual validation

## Task Summary

### exception-handling
- 2 tasks total
- 2 [AI] automated tasks

### controller
- 1 task total
- 1 [AI] automated task

### service
- 2 tasks total
- 2 [AI] automated tasks

### testing
- 4 tasks total
- 3 [AI] automated tasks
- 1 [MANUAL] human-required task

## Execution Order

### Phase 1: Exception Handling (API Surface First)
1. [TASK-001] - [AI] Create custom exception classes
2. [TASK-002] - [AI] Implement GlobalExceptionHandler with @ControllerAdvice

### Phase 2: Controller Layer (Endpoint Definition)
3. [TASK-003] - [AI] Add GET /api/atm/{id} endpoint to AtmController

### Phase 3: Service Layer (Business Logic)
4. [TASK-004] - [AI] Implement AtmService.findById() with ObjectId validation
5. [TASK-005] - [AI] Enhance AtmService.findAtms() with in-memory AND filtering

### Phase 4: Testing
6. [TASK-006] - [AI] Write unit tests for AtmService.findById()
7. [TASK-007] - [AI] Write unit tests for enhanced filtering logic
8. [TASK-008] - [AI] Write unit tests for GlobalExceptionHandler
9. [TASK-009] - [MANUAL] Perform manual validation against Node.js implementation

## Cross-Component Dependencies

- **TASK-001 → TASK-002**: Exception handler needs exception classes
- **TASK-002 → TASK-003**: Controller relies on exception handler for error responses
- **TASK-003 → TASK-004**: Controller endpoint calls service method
- **TASK-004, TASK-005 → TASK-006, TASK-007**: Tests require implementations

## Integration Points

### Error Response Flow (Top-to-Bottom)
1. Client receives consistent JSON error format
2. GlobalExceptionHandler catches and formats exceptions
3. Service throws typed exceptions (AtmNotFoundException, InvalidObjectIdException)
4. Repository returns Optional.empty() for not found

### Controllers to Services
- AtmController.getAtmById() → AtmService.findById()
- AtmController.searchAtms() → AtmService.findAtms() (enhanced filtering)
