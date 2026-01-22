# Phase 04 Tasks: Final Validation and Documentation

Final phase for production readiness. Since the Java service replaces Node.js from Phase-01 (drop-and-replace migration), this phase focuses on performance validation, documentation, and team sign-off.

## Components

- **performance-testing**: Locust load tests validating Java service performance
- **documentation**: Deployment procedures, rollback plans, and troubleshooting guides

## Task Summary

### performance-testing
- 1 task total
- 1 [AI] automated task
- 0 [MANUAL] human-required tasks

### documentation
- 3 tasks total
- 2 [AI] automated tasks
- 1 [MANUAL] human-required tasks

## Execution Order

1. **Performance Testing**:
   - [P4-001] - [AI] Create Locust performance test for Java ATM Locator service

2. **Documentation**:
   - [P4-002] - [AI] Create step-by-step deployment documentation
   - [P4-003] - [AI] Document rollback procedures and troubleshooting guide
   - [P4-004] - [MANUAL] Review and validate deployment procedures with team

## Cross-Component Dependencies

- All Phase-01, Phase-02, and Phase-03 tasks must be complete
- [P4-001] should complete before [P4-004] (performance validated before team review)
- [P4-002] and [P4-003] should be complete before [P4-004] (docs needed for review)

## Integration Points

- **Port Mapping**: Java service runs on port 8001 (replaced Node.js in Phase-01)
- **NGINX Integration**: No NGINX changes needed - same hostname and port
- **Network Integration**: Java service uses same bankapp-network connection
