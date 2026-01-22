# Testing Tasks

## Overview

Validate Phase-02 implementations with unit tests and manual comparison against Node.js service.

## Tasks

### [P2-006] - [AI] Write unit tests for AtmService.findById()

Create tests for: valid existing ID returns ATM, valid non-existent ID throws AtmNotFoundException, invalid ObjectId format throws InvalidObjectIdException, null and empty string handling.

---

### [P2-007] - [AI] Write unit tests for enhanced filtering logic

Test all filter combinations: isOpenNow=true/false, isInterPlanetary=true/false, both filters with AND logic. Verify filter=false behavior correctly filters for closed/earth-only ATMs.

---

### [P2-008] - [AI] Write unit tests for GlobalExceptionHandler

Test each exception handler: AtmNotFoundException returns 404, InvalidObjectIdException returns 404, generic exception returns 500. Verify error response message format.

---

### [P2-009] - [MANUAL] Perform manual validation against Node.js implementation

Compare GET /api/atm/{id} responses between services for: valid ID, invalid ID format, non-existent valid ObjectId. Compare all filter combinations. Verify error response format matches Node.js.
