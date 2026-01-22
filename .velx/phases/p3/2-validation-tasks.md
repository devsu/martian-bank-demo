# Validation Tasks

## Overview

Validation layer ensures data integrity for ATM creation using Bean Validation (JSR-303) annotations and custom validators for complex business rules. This provides declarative validation that matches Node.js service patterns.

## Prerequisites

- AtmCreateRequest DTO created ([TASK-003])
- Bean Validation dependency available in Spring Boot
- Understanding of Node.js validation error format

## Tasks

### [TASK-004] - [AI] Create custom validators for complex business rules

**Why**: Complex validation logic beyond simple annotations requires custom validators for maintainability.

**What**:
- Identify business rules requiring custom validation from Node.js service
- Create custom constraint annotations for complex rules
- Implement ConstraintValidator classes for each custom annotation
- Support validation for coordinate ranges, address formats, or domain-specific rules
- Make validators reusable across multiple DTOs if needed

**Testing** (TDD - write tests first):
- Unit test: custom validators accept valid values
- Unit test: custom validators reject invalid values
- Unit test: validation messages are descriptive
- Test edge cases for boundary conditions

**Dependencies**: [TASK-003] (requires AtmCreateRequest DTO)

---

### [TASK-005] - [AI] Implement field-level validation for structured fields

**Why**: Structured fields like coordinates and addresses require nested validation.

**What**:
- Add validation annotations to nested objects within AtmCreateRequest
- Validate coordinate latitude/longitude ranges
- Validate address field formats and required subfields
- Validate atmHours structure and time formats
- Use @Valid annotation for nested object validation
- Ensure validation cascades through object graph

**Testing** (TDD - write tests first):
- Unit test: invalid coordinates rejected
- Unit test: invalid address format rejected
- Unit test: invalid atmHours rejected
- Unit test: valid structured fields pass validation
- Test partial vs complete nested objects

**Dependencies**: [TASK-003] (requires AtmCreateRequest DTO)

---

### [TASK-016] - [MANUAL] Validate behavioral parity with Node.js service

**Why**: Manual validation ensures the Java implementation exactly matches Node.js behavior for migration consistency.

**What**:
- Start local Spring Boot application on port 8001
- Start Node.js service for side-by-side comparison
- Test POST /atm/add with valid complete payload - compare 201 response structure
- Test POST /atm/add with missing required fields - compare 400 response and field errors
- Test POST /atm/add with invalid field formats - compare validation error details
- Test POST /atm/add with duplicate data - compare response status and message
- Test edge cases: empty strings, nulls, extreme coordinates, partial payloads
- Verify created ATM persists to MongoDB
- Retrieve created ATM via GET /api/atm/{id} - confirm data integrity
- Validate OpenAPI /docs UI displays endpoint correctly
- Validate /docs.json specification structure matches Node.js Swagger
- Document any discrepancies for resolution

**Dependencies**: All implementation tasks ([TASK-002] through [TASK-015]) completed
