# Data Layer Tasks

## Overview

Implement MongoDB document models and repository interfaces for ATM persistence. This layer provides the foundation for data access and persistence operations throughout the application.

## Prerequisites

- Project setup complete (TASK-001, TASK-002)
- Spring Data MongoDB dependency available

## Tasks

### [TASK-003] - [AI] Create Atm entity with nested models

**Why**: Defines the domain model matching existing MongoDB schema to ensure data compatibility and proper object-document mapping.

**What**:
- Create Atm entity class annotated with @Document(collection = "atms")
- Map fields to MongoDB document structure:
  - id (String with @Id annotation for MongoDB ObjectId)
  - name (String)
  - location (nested object with coordinates and address)
  - isOpenNow (Boolean)
  - isInterPlanetary (Boolean)
  - createdAt, updatedAt (Date with proper JSON formatting)
  - __v (version field for MongoDB compatibility)
- Create nested model classes:
  - Address (street, city, state, zip)
  - Coordinates (latitude, longitude)
  - AtmHours (operational hours structure)
- Configure JSON serialization with @JsonProperty for field mapping
- Use @JsonFormat for date fields (ISO-8601 format)
- Add Lombok annotations (@Data, @NoArgsConstructor, @AllArgsConstructor) for boilerplate reduction

**Testing**:
- Verify entity class compiles without errors
- Verify all MongoDB document fields are mapped
- Test JSON serialization/deserialization with sample data

**Dependencies**: [TASK-002] - Requires model package structure

---

### [TASK-004] - [AI] Implement AtmRepository interface

**Why**: Abstracts MongoDB persistence operations following Spring Data Repository pattern for clean separation between business logic and data access.

**What**:
- Create AtmRepository interface extending MongoRepository<Atm, String>
- Add custom query methods for filter operations:
  - Method to support isOpenNow filter
  - Method to support isInterPlanetary filter
  - Method to support combination of both filters
- Use Spring Data MongoDB query derivation or @Query annotation for filtering
- Support null filter values (no filter applied when null)

**Testing**:
- Verify repository interface compiles
- Verify Spring Data MongoDB can instantiate repository bean
- Unit test custom query methods with mock MongoDB (defer to integration testing)

**Dependencies**: [TASK-003] - Requires Atm entity definition
