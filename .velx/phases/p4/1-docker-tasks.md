# Cutover Tasks

## Overview

Update docker-compose.yml to replace the Node.js ATM Locator service with the Java implementation for production deployment.

## Tasks

### [TASK-001] - [AI] Update docker-compose.yml to replace Node.js with Java service

Modify `atm-locator` service to use `./atm-locator-java` build context on port 8001. Keep original Node.js config commented for rollback reference. Remove/comment the development `atm-locator-java` service (port 8002). Ensure hostname remains compatible with NGINX.

---

### [TASK-002] - [MANUAL] Coordinate cutover schedule and perform service switch

Schedule cutover window during low-traffic period. Stop Node.js service, rebuild with Java, start Java service, verify health endpoint, run smoke tests, verify NGINX routing, confirm with stakeholders.
