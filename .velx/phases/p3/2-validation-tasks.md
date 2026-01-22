# Controller and Validation Tasks

## Overview

Define the POST /atm/add endpoint with request validation. Controller comes first (API surface), then validation details.

## Tasks

### [TASK-003] - [AI] Create AtmCreateRequest DTO with Bean Validation

Create AtmCreateRequest record with @NotBlank name, @NotNull/@Valid nested LocationRequest, @NotNull isOpenNow and isInterPlanetary. Create nested DTOs with validation: CoordinatesRequest (latitude -90 to 90, longitude -180 to 180), AddressRequest (required street, city, state, zip).

---

### [TASK-004] - [AI] Add POST /atm/add endpoint to AtmController

Add @PostMapping("/add") endpoint with @Valid @RequestBody AtmCreateRequest. Return ResponseEntity with 201 Created status. Document with OpenAPI annotations.

---

### [TASK-005] - [AI] Extend GlobalExceptionHandler for validation errors

Add handler for MethodArgumentNotValidException. Return 400 Bad Request with field-level error messages in format: {"message": "Validation failed", "errors": {"field": "message"}}.

---

### [TASK-006] - [AI] Add field-level validation for nested objects

Ensure @Valid cascades to nested objects. Add descriptive validation messages for coordinate range violations. Test that nested field paths appear correctly in error responses.
