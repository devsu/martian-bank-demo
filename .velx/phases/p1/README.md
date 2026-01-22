# Plan 01 Tasks

Phase-01 establishes the foundational Spring Boot architecture and delivers the first complete vertical slice endpoint (`POST /api/atm/`) to validate the migration approach. This plan implements a sequential execution strategy with component-based task organization, focusing on speed and pragmatic development to prove migration feasibility within 2-3 days.

## Components

- **project-setup**: Initialize Spring Boot 3.5 project with Gradle wrapper and package structure
- **data-layer**: Implement MongoDB document models and repository interfaces
- **service-layer**: Build business logic services and utility classes
- **controller-layer**: Create REST API endpoints and HTTP handling
- **configuration**: Configure MongoDB connection and data seeding mechanism
- **testing**: Implement essential unit tests for service layer

## Task Summary

### project-setup
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### data-layer
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### service-layer
- 3 tasks total
- 3 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### controller-layer
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### configuration
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required tasks

### testing
- 2 tasks total
- 1 [AI] automated tasks
- 1 [MANUAL] human-required tasks

## Execution Order

1. **Project Foundation** (Prerequisites for all other work):
   - [TASK-001] - [AI] Initialize Spring Boot project with Gradle wrapper
   - [TASK-002] - [AI] Create layer-based package structure
   - [TASK-011] - [MANUAL] Set up local MongoDB instance and obtain atm_data.json

2. **Data Layer** (Foundation for service and repository work):
   - [TASK-003] - [AI] Create Atm entity with nested models
   - [TASK-004] - [AI] Implement AtmRepository interface

3. **Service Layer** (Business logic before API exposure):
   - [TASK-005] - [AI] Implement RandomizationUtils
   - [TASK-006] - [AI] Implement AtmService with filtering logic
   - [TASK-007] - [AI] Create AtmSearchRequest DTO

4. **Controller Layer** (API exposure after business logic ready):
   - [TASK-008] - [AI] Implement AtmController with POST endpoint
   - [TASK-009] - [AI] Configure CORS settings

5. **Configuration** (Data seeding after repository available):
   - [TASK-010] - [AI] Configure MongoDB connection with environment variables
   - [TASK-012] - [AI] Implement DataSeederConfig with ApplicationRunner

6. **Testing and Validation** (After implementation complete):
   - [TASK-013] - [AI] Write unit tests for AtmService
   - [TASK-014] - [MANUAL] Perform manual endpoint validation and behavioral parity testing

## Cross-Component Dependencies

- **TASK-003 → TASK-004**: Repository interface requires Atm entity definition
- **TASK-004 → TASK-006**: AtmService depends on AtmRepository
- **TASK-005 → TASK-006**: AtmService uses RandomizationUtils
- **TASK-006 → TASK-008**: AtmController depends on AtmService
- **TASK-004 → TASK-012**: DataSeederConfig requires AtmRepository for seeding
- **TASK-010 → TASK-012**: Data seeding requires MongoDB connection configured
- **TASK-006 → TASK-013**: Unit tests require AtmService implementation
- **TASK-001 → All**: All tasks depend on project initialization
- **TASK-011 → TASK-012**: Seeding requires MongoDB accessible and seed data file available

## Integration Points

- **MongoDB Integration**: Configuration layer establishes connection used by repository layer for persistence operations and data seeding
- **REST API Integration**: Controller layer exposes service layer functionality via HTTP endpoints with proper request/response serialization
- **Testing Integration**: Unit tests validate service layer business logic with mocked repository dependencies
- **Environment Configuration**: Configuration layer reads environment variables (DATABASE_HOST, DB_URL, PORT) matching existing Node.js deployment pattern

## Notes

- Sequential execution strategy: Complete each layer before moving to next
- Minimal testing approach: Essential unit tests only, rely on manual validation
- Data seeding uses fail-fast approach: Application won't start if atm_data.json missing
- CORS configuration must match existing Node.js settings for API consumer compatibility
- Focus on speed over perfection: Working implementation first, refinement later
