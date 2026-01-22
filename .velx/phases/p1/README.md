# Phase 01 Tasks

Phase-01 establishes the foundational Spring Boot architecture with a **top-to-bottom development approach**. We start with Docker infrastructure, then build the API surface (controllers, DTOs, response formats), then services, then data layer, and finally database configuration. This ensures the client-facing contract is defined first and lower-level implementation follows.

## Development Philosophy

**Top-to-Bottom**: Build from the API surface down to the database:
1. Docker Infrastructure → Enable testing from day one
2. Project Setup → Spring Boot foundation
3. Controller Layer → Define API contract, endpoints, request/response formats
4. Service Layer → Business logic (initially with mocked data)
5. Data Layer → Entities and repositories
6. Configuration → Database connection and data seeding
7. Testing → Validate the complete vertical slice

## Components

- **docker-infrastructure**: Create Dockerfile and docker-compose integration (PREREQUISITE)
- **project-setup**: Initialize Spring Boot 3.5 project with Gradle wrapper
- **controller-layer**: REST API endpoints, DTOs, request/response handling, CORS
- **service-layer**: Business logic services and utilities
- **data-layer**: MongoDB document models and repository interfaces
- **configuration**: MongoDB connection and data seeding
- **testing**: Unit tests and smoke tests for validation

## Task Summary

### docker-infrastructure
- 4 tasks total
- 3 [AI] automated tasks
- 1 [MANUAL] human-required tasks

### project-setup
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### controller-layer
- 3 tasks total
- 3 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### service-layer
- 3 tasks total
- 3 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### data-layer
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### configuration
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### testing
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required tasks

## Execution Order

1. **Docker Infrastructure** (PREREQUISITE FOR ALL DEVELOPMENT):
   - [TASK-001] - [AI] Create multi-stage Dockerfile for Java service
   - [TASK-002] - [AI] Configure .dockerignore and build optimization
   - [TASK-003] - [AI] Update docker-compose.yml to add Java ATM Locator service
   - [TASK-004] - [MANUAL] Verify Docker build and container startup

2. **Project Foundation**:
   - [TASK-005] - [AI] Initialize Spring Boot project with Gradle wrapper
   - [TASK-006] - [AI] Create layer-based package structure

3. **Controller Layer** (API Surface - BUILD FIRST):
   - [TASK-007] - [AI] Create AtmSearchRequest and AtmResponse DTOs
   - [TASK-008] - [AI] Implement AtmController with POST endpoint (mocked response initially)
   - [TASK-009] - [AI] Configure CORS settings

4. **Service Layer** (Business Logic):
   - [TASK-010] - [AI] Implement RandomizationUtils
   - [TASK-011] - [AI] Create AtmService interface and implementation
   - [TASK-012] - [AI] Wire AtmController to AtmService (replace mocked response)

5. **Data Layer** (Persistence):
   - [TASK-013] - [AI] Create Atm entity with nested models
   - [TASK-014] - [AI] Implement AtmRepository interface
   - [TASK-015] - [AI] Wire AtmService to AtmRepository (replace mocked data)

6. **Configuration** (Database Connection):
   - [TASK-016] - [AI] Configure MongoDB connection with environment variables
   - [TASK-017] - [AI] Implement DataSeederConfig with ApplicationRunner

7. **Testing and Validation**:
   - [TASK-018] - [AI] Write unit tests for AtmService
   - [TASK-019] - [AI] Create smoke test scripts for container validation
   - [TASK-020] - [MANUAL] Perform manual endpoint validation via Docker

## Cross-Component Dependencies

- **Docker Infrastructure → All**: All development depends on Docker being configured first
- **TASK-007 → TASK-008**: Controller needs DTOs for request/response
- **TASK-008 → TASK-012**: Controller initially returns mocked data, then wired to service
- **TASK-011 → TASK-012**: Service implementation needed before wiring to controller
- **TASK-013 → TASK-014**: Repository needs entity definition
- **TASK-014 → TASK-015**: Service needs repository to replace mocked data
- **TASK-016 → TASK-017**: Data seeding requires MongoDB connection
- **TASK-017 → TASK-020**: Manual validation requires seeded data

## Integration Points

- **Docker Integration**: Dockerfile builds Java service, docker-compose orchestrates with MongoDB
- **API Contract**: DTOs define request/response format before implementation details
- **Service Abstraction**: Controller depends on service interface, not implementation
- **Repository Abstraction**: Service depends on repository interface for data access
- **Configuration**: Environment variables configure MongoDB connection at runtime

## Development Workflow

```bash
# Build and start services
docker-compose up -d --build mongo atm-locator-java

# Test endpoint (returns mocked data initially)
curl -X POST http://localhost:8002/api/atm/ -H "Content-Type: application/json" -d '{}'

# View logs
docker-compose logs -f atm-locator-java

# Rebuild after changes
docker-compose up -d --build atm-locator-java

# Compare with Node.js service
curl -X POST http://localhost:8001/api/atm/ -H "Content-Type: application/json" -d '{}'
```

## Notes

- **Top-to-bottom approach**: API contract defined before database schema
- **Mocked responses first**: Controller works with hardcoded data before service integration
- **Incremental integration**: Each layer is tested independently before wiring
- Docker-first development: Build and test in containers from day one
- Java service runs on port 8002 alongside Node.js on 8001 during development
