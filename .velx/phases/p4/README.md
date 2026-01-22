# Phase 04 Tasks: Production Cutover and Documentation

Final deployment phase that replaces the Node.js ATM Locator service with the Java implementation in production. Since Docker infrastructure was established in Phase-01, this phase focuses on the cutover process, final validation, and documentation.

## Components

- **cutover**: Switch from Node.js to Java service in docker-compose
- **smoke-tests**: Extended validation scripts for production cutover
- **documentation**: Deployment procedures, rollback plans, and troubleshooting guides

## Task Summary

### cutover
- 2 tasks total
- 1 [AI] automated tasks
- 1 [MANUAL] human-required tasks

### smoke-tests
- 1 task total
- 1 [AI] automated task
- 0 [MANUAL] human-required tasks

### documentation
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required tasks

## Execution Order

1. **Cutover Preparation**:
   - [TASK-001] - [AI] Update docker-compose.yml to replace Node.js with Java service
   - [TASK-002] - [MANUAL] Coordinate cutover schedule and perform service switch

2. **Validation**:
   - [TASK-003] - [AI] Create comprehensive production smoke test suite

3. **Documentation**:
   - [TASK-004] - [AI] Create step-by-step deployment documentation
   - [TASK-005] - [AI] Document rollback procedures and troubleshooting guide
   - [TASK-006] - [MANUAL] Review and validate deployment procedures with team

## Cross-Component Dependencies

- All Phase-01, Phase-02, and Phase-03 tasks must be complete
- [TASK-001] must complete before [TASK-002] (compose config needed for cutover)
- [TASK-003] supports [TASK-002] (smoke tests validate cutover success)
- [TASK-004] and [TASK-005] should be complete before [TASK-006] (docs needed for review)

## Integration Points

- **Docker Compose Update**: Modify existing atm-locator service to use Java image instead of Node.js
- **Port Mapping**: Java service takes over port 8001 (same as Node.js for API compatibility)
- **NGINX Integration**: No NGINX changes needed - same hostname and port
- **Network Integration**: Java service uses same bankapp-network connection
