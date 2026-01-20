# Model Tasks

## Overview

Create domain entities, value objects, and Data Transfer Objects (DTOs) with comprehensive validation annotations. Includes custom serialization logic to maintain MongoDB $oid format for backward compatibility.

## Prerequisites

- Maven project structure established ([TASK-002])
- Dependencies configured ([TASK-003])

## Tasks

### [TASK-004] - [AI] Create ATM entity with MongoDB annotations

**Why**: ATM entity represents the core domain model mapping to MongoDB documents while preserving exact field structure.

**What**:
- Create ATM entity class with @Document annotation specifying "atms" collection
- Add all core fields: id (String with @Id), name, address, coordinates, atmHours, timings, numberOfATMs, isOpen, interPlanetary
- Add system fields: createdAt (@CreatedDate), updatedAt (@LastModifiedDate), version (@Version for __v field)
- Use Lombok @Data for getters/setters and @Builder for construction
- Ensure field names match MongoDB document structure exactly
- Mark id field for custom ObjectId serialization (preparation for TASK-006)

**Testing** (TDD - write tests first):
- Unit test: Verify entity instantiation with all fields
- Unit test: Validate @Builder pattern creates complete entities
- Unit test: Confirm Lombok generates getters/setters correctly
- Unit test: Test equals and hashCode based on id field

**Dependencies**: [TASK-003] dependencies configured

---

### [TASK-005] - [AI] Create nested value objects for Address, Coordinates, and Timings

**Why**: Value objects encapsulate related fields and provide type safety for complex nested structures.

**What**:
- Create Address class with fields: street, city, state, zip
- Create Coordinates class with fields: latitude (Double), longitude (Double)
- Create Timings class with fields: monFri (String), satSun (String), holidays (String)
- Use Lombok @Data and @NoArgsConstructor/@AllArgsConstructor for each
- Ensure classes are serializable for MongoDB storage
- Add JSR-380 validation annotations (@NotNull, @NotBlank where appropriate)

**Testing** (TDD - write tests first):
- Unit test: Verify each value object instantiates with valid data
- Unit test: Test null handling for optional fields
- Unit test: Validate serialization to JSON preserves field names
- Unit test: Confirm nested object equality and hashCode

**Dependencies**: [TASK-004] entity structure

---

### [TASK-006] - [AI] Implement custom ObjectId serializer for $oid format

**Why**: MongoDB clients expect ObjectId in {"$oid": "..."} wrapper format for backward compatibility.

**What**:
- Create MongoObjectIdSerializer extending JsonSerializer<String>
- Implement serialize method to output {"$oid": "value"} structure
- Create MongoObjectIdDeserializer extending JsonDeserializer<String>
- Implement deserialize method to parse both plain string and $oid wrapper formats
- Handle null and invalid ObjectId formats gracefully
- Ensure 24-character hex string validation

**Testing** (TDD - write tests first):
- Unit test: Serialize valid ObjectId produces {"$oid": "507f1f77bcf86cd799439011"}
- Unit test: Deserialize {"$oid": "..."} wrapper format correctly
- Unit test: Deserialize plain ObjectId string for flexibility
- Unit test: Handle null values without exceptions
- Unit test: Reject invalid ObjectId formats with clear error

**Dependencies**: [TASK-003] Jackson dependencies

---

### [TASK-007] - [AI] Create request and response DTOs with validation annotations

**Why**: DTOs separate API contracts from domain models and enable comprehensive input validation.

**What**:
- Create NearbySearchRequest DTO with latitude (@Min(-90) @Max(90)), longitude (@Min(-180) @Max(180)), radius (@Positive) fields
- Create ATMCreateRequest DTO with all ATM fields and nested validation (@Valid)
- Create ATMResponse DTO matching ATM entity structure with @JsonSerialize for ObjectId
- Create ErrorResponse DTO with code, message, timestamp, details fields
- Add JSR-380 validation annotations to all request DTOs (@NotNull, @NotBlank, @Size, custom validators)
- Use Lombok @Data and @Builder for all DTOs
- Ensure response DTOs preserve field naming exactly as Node.js implementation

**Testing** (TDD - write tests first):
- Unit test: Validate latitude/longitude range constraints
- Unit test: Verify radius must be positive value
- Unit test: Test nested object validation in ATMCreateRequest
- Unit test: Confirm ATMResponse serializes id with $oid wrapper
- Unit test: Validate error response structure matches specification

**Dependencies**: [TASK-005] value objects, [TASK-006] ObjectId serializer
