# Backend Tasks

## Overview

Implement GET /api/atm/{id} endpoint and enhanced filtering following top-to-bottom approach: exception handling first, then controller, then service.

## Tasks

### [P2-001] - [AI] Create custom exception classes

Create `AtmNotFoundException` and `InvalidObjectIdException` in exception package, both extending RuntimeException with descriptive messages including the problematic ID.

---

### [P2-002] - [AI] Implement GlobalExceptionHandler with @ControllerAdvice

Create GlobalExceptionHandler with @RestControllerAdvice. Handle AtmNotFoundException (404), InvalidObjectIdException (404), HttpMessageNotReadableException (400), and generic Exception (500). Return ErrorResponse with message and optional stack trace.

---

### [P2-003] - [AI] Add GET /api/atm/{id} endpoint to AtmController

Add @GetMapping("/{id}") endpoint accepting path variable. Delegate to AtmService.findById(). Let GlobalExceptionHandler handle errors.

---

### [P2-004] - [AI] Implement AtmService.findById() with ObjectId validation

Add findById() to AtmService interface and implementation. Validate ObjectId format using MongoDB's ObjectId.isValid(). Throw InvalidObjectIdException for malformed IDs, AtmNotFoundException for valid but non-existent IDs.

---

### [P2-005] - [AI] Enhance AtmService.findAtms() with in-memory AND filtering

Update applyFilters() to use Stream API with chained filters. Apply AND logic when both isOpenNow and isInterPlanetary filters are provided. Null filter means no filter on that field.
