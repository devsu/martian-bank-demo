# Phase-04 Plan 1 Tasks: Docker Containerization and Deployment

Deploy the fully functional Java service to Docker Compose environment as a direct replacement for the Node.js service with basic smoke testing to verify successful deployment.

## Components

- **docker**: Multi-stage Dockerfile for building and packaging the Java service
- **docker-compose**: Docker Compose configuration updates for service replacement
- **documentation**: Deployment procedures, rollback plans, and troubleshooting guides
- **smoke-tests**: Basic validation scripts for post-deployment verification

## Task Summary

### docker
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

### docker-compose
- 2 tasks total
- 1 [AI] automated tasks
- 1 [MANUAL] human-required tasks

### documentation
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required tasks

### smoke-tests
- 2 tasks total
- 2 [AI] automated tasks
- 0 [MANUAL] human-required tasks

## Execution Order

1. **Build Infrastructure Setup**:
   - [TASK-001] - [AI] Create multi-stage Dockerfile with build and runtime stages
   - [TASK-002] - [AI] Configure Docker build optimization and image bundling

2. **Docker Compose Configuration**:
   - [TASK-003] - [AI] Update docker-compose.yml to replace Node.js service with Java service
   - [TASK-004] - [MANUAL] Coordinate with operations team for cutover schedule and access

3. **Testing Infrastructure**:
   - [TASK-005] - [AI] Create smoke test scripts for service validation
   - [TASK-006] - [AI] Implement automated health check and API endpoint tests

4. **Documentation and Procedures**:
   - [TASK-007] - [AI] Create step-by-step deployment documentation
   - [TASK-008] - [AI] Document rollback procedures and troubleshooting guide
   - [TASK-009] - [MANUAL] Review and validate deployment procedures with operations team

## Cross-Component Dependencies

- [TASK-001] and [TASK-002] must complete before [TASK-003] (Dockerfile needed for docker-compose service definition)
- [TASK-003] must complete before [TASK-005] and [TASK-006] (compose environment needed for smoke tests)
- [TASK-007] and [TASK-008] should reference [TASK-003] (documentation describes compose configuration)
- [TASK-004] and [TASK-009] require team coordination and approval before deployment

## Integration Points

- **Docker to Docker Compose**: Dockerfile produces image referenced in docker-compose.yml service definition
- **Docker Compose to Smoke Tests**: Smoke test scripts execute against running containers defined in compose configuration
- **Documentation to All Components**: Deployment docs describe usage of Dockerfile, compose setup, and smoke test execution
- **Network Integration**: Java service container connects to existing MongoDB container via bankapp-network
- **Port Mapping**: Java service exposes port 8001 (same as Node.js service for API compatibility)
- **Data Seeding**: atm_data.json bundled in Docker image, automatically seeded on container startup
