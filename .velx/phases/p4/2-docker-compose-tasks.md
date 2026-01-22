# Docker Compose Tasks

## Overview

Update the existing Docker Compose configuration to replace the Node.js service definition with the Java service, maintaining identical port mapping, network configuration, and environment variables. This enables a direct cutover deployment strategy with minimal infrastructure changes.

## Prerequisites

- Existing docker-compose.yml file accessible
- MongoDB container operational in bankapp-network
- Access permissions to stop/start containers
- Docker image built from docker tasks (TASK-001, TASK-002)

## Tasks

### [TASK-003] - [AI] Update docker-compose.yml to replace Node.js service with Java service

**What**:
- Replace Node.js service definition with Java service configuration
- Service name preserved or updated to atm-locator-java for clarity
- Image reference pointing to locally built atm-locator-java:latest
- Port mapping: 8001:8001 (maintaining existing API port)
- Environment variables: DATABASE_HOST, DB_URL, PORT configured
- Network configuration: bankapp-network (existing shared network)
- Depends_on configuration: MongoDB container dependency
- Restart policy: unless-stopped for automatic recovery
- Container name: atm-locator-java for easy identification
- Remove or comment out Node.js service definition for clean cutover

**Testing**:
- Configuration validation: `docker-compose config` parses without errors
- Dry-run deployment: `docker-compose up --no-start` creates containers without starting
- Verify service definition includes all required environment variables
- Network inspection: Confirm atm-locator-java service attached to bankapp-network

**Dependencies**: [TASK-001], [TASK-002] (requires Docker image to be built)

---

### [TASK-004] - [MANUAL] Coordinate with operations team for cutover schedule and access

**Why**: Ensure proper coordination for production cutover, minimize service interruption, and confirm team readiness for deployment and potential rollback.

**What**:
- Schedule cutover window with operations team (1-2 hour window recommended)
- Confirm access to docker-compose.yml file and container management permissions
- Validate MongoDB container operational status and connectivity
- Communicate cutover plan to API consumers (though no changes required for them)
- Establish monitoring and alerting for new Java service container
- Confirm rollback procedures documented and approved
- Verify operations team has Docker Compose access and can execute stop/start commands
- Plan brief service interruption window during cutover (Node.js stop to Java start)

**Dependencies**: [TASK-003] (requires compose configuration ready for deployment)

---
