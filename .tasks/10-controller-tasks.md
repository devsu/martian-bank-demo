# Controller Tasks

## Overview

Implement REST API endpoints with Spring Web MVC controllers, request validation, response formatting, and proper HTTP status code handling. Maintains exact backward compatibility with Node.js implementation.

## Prerequisites

- Service layer implemented ([TASK-016], [TASK-017])
- DTOs created with validation ([TASK-007])
- Exception handler implemented ([TASK-019])
- Test infrastructure set up ([TASK-008], [TASK-009])

## Tasks

### [TASK-021] - [AI] Implement GET /api/atms/nearby endpoint with validation

**Why**: Geospatial search is the primary use case for finding ATMs within a specified radius of user location.

**What**:
- Create ATMController class with @RestController and @RequestMapping("/api")
- Inject ATMService via constructor using @RequiredArgsConstructor
- Implement GET /atms/nearby endpoint with @GetMapping annotation
- Accept query parameters: latitude (@RequestParam @Valid), longitude (@RequestParam @Valid), radius (@RequestParam @Valid)
- Use @Valid annotation to trigger JSR-380 validation
- Call service.findNearbyATMs(lat, lon, radius)
- Return List<ATMResponse> with HTTP 200 status
- Ensure ObjectId serialization uses $oid format
- Add @Operation annotation for OpenAPI documentation

**Testing** (TDD - write tests first):
- Controller test: MockMvc test for valid coordinates and radius returns HTTP 200
- Controller test: Test latitude > 90 returns HTTP 400 with validation error
- Controller test: Test longitude < -180 returns HTTP 400 with validation error
- Controller test: Test negative radius returns HTTP 400 with validation error
- Controller test: Verify response format matches expected JSON structure
- Controller test: Confirm ObjectId appears as {"$oid": "..."} in response
- Controller test: Test empty results (no ATMs within radius) returns empty array

**Dependencies**: [TASK-016] service implementation, [TASK-007] DTOs, [TASK-019] exception handler

---

### [TASK-022] - [AI] Implement GET /atm/{id} endpoint for ATM retrieval

**Why**: Direct ATM retrieval by ID enables detail views and specific ATM information access.

**What**:
- Add GET /atm/{id} endpoint to ATMController
- Accept id as @PathVariable String
- Validate ObjectId format (24-character hex string)
- Call service.getATMById(id)
- Return ATMResponse with HTTP 200 if found
- Return HTTP 404 with error message if not found
- Handle invalid ObjectId format with HTTP 404
- Add @Operation annotation for OpenAPI documentation

**Testing** (TDD - write tests first):
- Controller test: Valid ObjectId returns HTTP 200 with complete ATM details
- Controller test: Non-existent ObjectId returns HTTP 404 with error message
- Controller test: Invalid ObjectId format (not 24 hex chars) returns HTTP 404
- Controller test: Verify response includes all ATM fields with correct nesting
- Controller test: Confirm id field uses $oid wrapper format
- Controller test: Test timestamps formatted as ISO 8601

**Dependencies**: [TASK-016] service implementation, [TASK-007] DTOs, [TASK-019] exception handler

---

### [TASK-023] - [AI] Implement POST /atm/add endpoint with authentication

**Why**: Administrative capability to add new ATMs to the system with proper access control.

**What**:
- Add POST /atm/add endpoint to ATMController
- Accept @RequestBody @Valid ATMCreateRequest DTO
- Validate all required fields through JSR-380 annotations
- Call service.createATM(request)
- Return created ATMResponse with HTTP 201 status
- Include Location header with new ATM URI
- Add Spring Security @PreAuthorize for JWT validation (authentication check)
- Add @Operation annotation for OpenAPI documentation with security requirement

**Testing** (TDD - write tests first):
- Controller test: Valid ATM creation request returns HTTP 201
- Controller test: Response includes generated ObjectId in $oid format
- Controller test: Response includes createdAt and updatedAt timestamps
- Controller test: Missing required fields returns HTTP 400 with field errors
- Controller test: Invalid nested object (e.g., coordinates out of range) returns HTTP 400
- Controller test: Location header contains correct ATM URI
- Controller test: Verify service.createATM called with correct DTO

**Dependencies**: [TASK-016] service implementation, [TASK-007] DTOs, [TASK-019] exception handler
