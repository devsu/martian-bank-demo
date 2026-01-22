# Testing Tasks

## Overview

Validate the complete vertical slice after all layers are integrated.

## Tasks

### [P1-018] - [AI] Write unit tests for AtmService

Create AtmServiceTest using JUnit 5 and Mockito. Test findAtms() scenarios: no filters, isOpenNow filter, isInterPlanetary filter, both filters (AND logic), empty results. Use AssertJ assertions.

---

### [P1-019] - [MANUAL] Perform manual endpoint validation via Docker

Start Java service on port 8001. Test POST /api/atm/ with empty body, isOpenNow filter, isInterPlanetary filter, and both filters. Verify response structure matches expected format, HTTP status codes are correct, and CORS headers are present.
