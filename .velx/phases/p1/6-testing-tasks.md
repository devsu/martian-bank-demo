# Testing Tasks

## Overview

Validate the complete vertical slice after all layers are integrated.

## Tasks

### [TASK-018] - [AI] Write unit tests for AtmService

Create AtmServiceTest using JUnit 5 and Mockito. Test findAtms() scenarios: no filters, isOpenNow filter, isInterPlanetary filter, both filters (AND logic), empty results. Use AssertJ assertions.

---

### [TASK-019] - [AI] Create smoke test scripts for container validation

Create `smoke-test.sh` shell script that tests: health check endpoint, POST /api/atm/ returns data, filtering works correctly, max 4 results enforced. Accept base URL parameter.

---

### [TASK-020] - [MANUAL] Perform manual endpoint validation via Docker

Start both Node.js (8001) and Java (8002) services. Compare responses for empty body, isOpenNow filter, isInterPlanetary filter, and both filters. Verify response structure, HTTP status codes, and CORS headers match.
