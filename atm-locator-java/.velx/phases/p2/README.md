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

### Step 1: Exception Handling (API Surface First)
1. [P2-001] - [AI] Create custom exception classes
2. [P2-002] - [AI] Implement GlobalExceptionHandler with @ControllerAdvice

### Step 2: Controller Layer (Endpoint Definition)
3. [P2-003] - [AI] Add GET /api/atm/{id} endpoint to AtmController

### Step 3: Service Layer (Business Logic)
4. [P2-004] - [AI] Implement AtmService.findById() with ObjectId validation
5. [P2-005] - [AI] Enhance AtmService.findAtms() with in-memory AND filtering

### Step 4: Testing
6. [P2-006] - [AI] Write unit tests for AtmService.findById()
7. [P2-007] - [AI] Write unit tests for enhanced filtering logic
8. [P2-008] - [AI] Write unit tests for GlobalExceptionHandler
9. [P2-009] - [MANUAL] Perform manual validation against Node.js implementation

## Cross-Component Dependencies

- **P2-001 → P2-002**: Exception handler needs exception classes
- **P2-002 → P2-003**: Controller relies on exception handler for error responses
- **P2-003 → P2-004**: Controller endpoint calls service method
- **P2-004, P2-005 → P2-006, P2-007**: Tests require implementations

## Integration Points

### Error Response Flow (Top-to-Bottom)
1. Client receives consistent JSON error format
2. GlobalExceptionHandler catches and formats exceptions
3. Service throws typed exceptions (AtmNotFoundException, InvalidObjectIdException)
4. Repository returns Optional.empty() for not found

### Controllers to Services
- AtmController.getAtmById() → AtmService.findById()
- AtmController.searchAtms() → AtmService.findAtms() (enhanced filtering)
