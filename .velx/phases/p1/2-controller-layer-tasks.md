# Controller Layer Tasks

## Overview

Define the API contract by implementing controllers, DTOs, and request/response handling. Controller initially returns mocked data.

## Tasks

### [P1-007] - [AI] Create AtmSearchRequest and AtmResponse DTOs

Create `AtmSearchRequest` record with optional isOpenNow and isInterPlanetary filters. Create `AtmResponse` record with nested DTOs (LocationResponse, AddressResponse, CoordinatesResponse) matching the Node.js API response structure.

---

### [P1-008] - [AI] Implement AtmController with POST endpoint (mocked response)

Create AtmController with @RestController and POST `/api/atm/` endpoint. Initially return hardcoded sample ATMs to validate API contract before wiring to service layer.

---

### [P1-009] - [AI] Configure CORS settings

Create WebConfig implementing WebMvcConfigurer with CORS settings: allow all origins, methods (GET, POST, PUT, DELETE, OPTIONS), headers, and credentials. Apply to all paths.
