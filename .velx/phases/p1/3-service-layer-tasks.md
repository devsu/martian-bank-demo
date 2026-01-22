# Service Layer Tasks

## Overview

Implement business logic services and utility classes for ATM search operations. This layer orchestrates repository queries, applies business rules, and coordinates randomization logic.

## Prerequisites

- Data layer complete (TASK-003, TASK-004)
- AtmRepository interface available

## Tasks

### [TASK-005] - [AI] Implement RandomizationUtils

**Why**: Encapsulates randomization logic in a reusable, testable utility class following single responsibility principle and maintaining clean service layer code.

**What**:
- Create RandomizationUtils class in util package
- Implement static method `selectRandom(List<T> items, int maxCount)`
- Use Collections.shuffle() for randomization
- Return sublist of up to maxCount items (or all items if fewer available)
- Handle empty list and null input gracefully
- Make utility class final with private constructor (pure utility pattern)

**Testing** (TDD - write tests first):
- Unit test: Empty list returns empty result
- Unit test: List smaller than maxCount returns all items
- Unit test: List larger than maxCount returns exactly maxCount items
- Unit test: Verify randomization occurs (statistical test or seed-based verification)

**Dependencies**: [TASK-002] - Requires util package structure

---

### [TASK-006] - [AI] Implement AtmService with filtering logic

**Why**: Centralizes ATM search business logic including filter processing, repository coordination, and result randomization to maintain clean separation of concerns.

**What**:
- Create AtmService class annotated with @Service
- Inject AtmRepository dependency via constructor injection
- Implement findAtms(AtmSearchRequest request) method:
  - Extract filter criteria from request (isOpenNow, isInterPlanetary)
  - Call appropriate repository method based on filter combinations
  - Apply randomization using RandomizationUtils (max 4 results)
  - Return filtered and randomized ATM list
- Add appropriate logging using SLF4J (@Slf4j annotation)
- Handle null filters (no filtering applied)

**Testing** (TDD - write tests first):
- Unit test: No filters returns randomized unfiltered results
- Unit test: isOpenNow filter returns only open ATMs
- Unit test: isInterPlanetary filter returns only interplanetary ATMs
- Unit test: Both filters return ATMs matching both criteria
- Unit test: Verify randomization applied (max 4 results)
- Mock AtmRepository for all tests

**Dependencies**: [TASK-004] - Requires AtmRepository, [TASK-005] - Uses RandomizationUtils

---

### [TASK-007] - [AI] Create AtmSearchRequest DTO

**Why**: Provides type-safe representation of search filter criteria from HTTP request body for clean controller-service communication.

**What**:
- Create AtmSearchRequest class in model package
- Define fields for filter criteria:
  - isOpenNow (Boolean, optional)
  - isInterPlanetary (Boolean, optional)
- Use Java record or POJO with Lombok annotations
- Support null values for optional filters
- Add JSON deserialization annotations if needed

**Testing**:
- Verify DTO class compiles
- Test JSON deserialization from sample request bodies
- Verify null handling for optional fields

**Dependencies**: [TASK-002] - Requires model package structure
