# Testing Tasks

## Overview

Validate the POST /atm/add endpoint implementation with unit tests and behavioral parity testing.

## Tasks

### [P3-009] - [AI] Write unit tests for AtmService.createAtm()

Test successful creation with valid request. Test duplicate name throws DuplicateAtmException. Verify repository.save() is called for valid requests and never called for duplicates.

---

### [P3-010] - [AI] Write unit tests for validation logic

Use Validator to test: missing name has validation error, invalid latitude (outside -90 to 90) has error, valid request has no errors. Verify nested field paths in violations.

---

### [P3-011] - [MANUAL] Validate behavioral parity with Node.js service

Compare successful creation responses between services. Compare validation error responses. Compare duplicate handling. Verify OpenAPI documentation is accessible and accurate.
