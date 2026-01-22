# Service Layer Tasks

## Overview

Implement the business logic for ATM creation after the controller and validation layers are complete.

## Tasks

### [P3-007] - [MANUAL] Investigate Node.js duplicate handling logic

Review Node.js POST /atm/add implementation. Identify duplicate detection criteria (by name, coordinates, or combination). Document HTTP status code and error message format for duplicates.

---

### [P3-008] - [AI] Implement AtmService.createAtm() with duplicate detection

Add createAtm() to AtmService interface. Create DuplicateAtmException and add handler returning 409 Conflict. Implement service method: check for duplicates based on Node.js behavior, convert request to entity, save to repository, return response DTO.
