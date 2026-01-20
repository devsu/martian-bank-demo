# Repository Tasks

## Overview

Create Spring Data MongoDB repository interfaces for data access abstraction and implement unit tests with mocked dependencies following TDD principles.

## Prerequisites

- ATM entity created ([TASK-004])
- Test infrastructure set up ([TASK-008])

## Tasks

### [TASK-010] - [AI] Create ATMRepository interface with Spring Data MongoDB

**Why**: Repository abstraction enables clean separation between data access and business logic while leveraging Spring Data MongoDB query generation.

**What**:
- Create ATMRepository interface extending MongoRepository<ATM, String>
- Inherit standard CRUD operations: findAll(), findById(), save(), delete()
- Add custom query method: countBy() for seed data duplicate checking
- Ensure interface is annotated with @Repository for component scanning
- No implementation needed - Spring Data generates at runtime

**Testing** (TDD - write tests first):
- Unit test: Mock repository.findAll() and verify returns list of ATMs
- Unit test: Mock repository.findById() for existing and non-existing IDs
- Unit test: Mock repository.save() and verify saved entity returned
- Unit test: Mock repository.countBy() returns expected count
- Note: These tests use Mockito mocks, not actual database

**Dependencies**: [TASK-004] ATM entity

---

### [TASK-011] - [AI] Implement repository unit tests with mocked dependencies

**Why**: Unit tests validate repository usage patterns without requiring database infrastructure.

**What**:
- Create ATMRepositoryTest class with @ExtendWith(MockitoExtension.class)
- Mock ATMRepository using @Mock annotation
- Test findAll returns empty list, single item, multiple items
- Test findById with valid ObjectId returns Optional.of(atm)
- Test findById with invalid ObjectId returns Optional.empty()
- Test save persists new ATM with generated ID
- Test save updates existing ATM preserving ID
- Use test fixtures from TASK-009 for consistent test data

**Testing** (TDD - write tests first):
- Test covers all repository method signatures
- Test verifies Mockito mock interactions with verify()
- Test uses ArgumentCaptor for complex validation
- Test includes edge cases: null values, empty strings

**Dependencies**: [TASK-008] test infrastructure, [TASK-009] test fixtures, [TASK-010] repository interface
