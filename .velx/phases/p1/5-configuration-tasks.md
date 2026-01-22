# Configuration Tasks

## Overview

Configure MongoDB connection and data seeding mechanism to establish database connectivity and ensure consistent test data availability. This component handles environment variable mapping and startup data loading.

## Prerequisites

- Data layer complete (TASK-003, TASK-004)
- MongoDB instance accessible (TASK-011)
- atm_data.json file available (TASK-011)

## Tasks

### [TASK-010] - [AI] Configure MongoDB connection with environment variables

**Why**: Establishes database connectivity using environment variables matching existing Node.js deployment pattern for infrastructure compatibility.

**What**:
- Create MongoConfig class annotated with @Configuration
- Read environment variables using @Value:
  - DB_URL (full connection string, primary)
  - DATABASE_HOST (legacy compatibility, fallback)
  - Default to localhost:27017/martianbank if neither provided
- Configure MongoDB connection URI construction:
  - If DB_URL exists, use directly
  - If only DATABASE_HOST exists, construct mongodb://host:27017/martianbank
  - Otherwise use localhost default
- Update application.properties to map environment variables:
  - spring.data.mongodb.uri from DB_URL or constructed URI
  - server.port from PORT (default 8001)
- Ensure Spring Data MongoDB auto-configuration uses provided URI

**Testing**:
- Verify MongoDB connection succeeds with default localhost
- Test DB_URL environment variable overrides default
- Test DATABASE_HOST environment variable constructs correct URI
- Verify application fails fast if MongoDB unavailable

**Dependencies**: [TASK-002] - Requires config package structure

---

### [TASK-011] - [MANUAL] Set up local MongoDB instance and obtain atm_data.json

**Why**: MongoDB instance and seed data are external dependencies required before application can run or be tested.

**What**:
- Install and start MongoDB 5.8.x locally on port 27017
- Create martianbank database
- Obtain atm_data.json file from existing Node.js repository
- Place atm_data.json in src/main/resources/config/ directory
- Verify file format: MongoDB extended JSON with $oid and $date syntax
- Verify file is accessible from classpath at runtime
- Document connection details for team (hostname, port, database name)

**Dependencies**: [TASK-002] - Requires resources/config directory structure

---

### [TASK-012] - [AI] Implement DataSeederConfig with ApplicationRunner

**Why**: Ensures consistent known state by loading seed data on application startup with drop-and-seed behavior matching existing Node.js implementation.

**What**:
- Create DataSeederConfig class annotated with @Configuration
- Implement ApplicationRunner interface for startup execution
- Inject AtmRepository and ResourceLoader dependencies
- Implement run() method:
  - Load atm_data.json from classpath: config/atm_data.json
  - Fail-fast if file missing (throw exception with clear message)
  - Drop existing ATM collection using repository.deleteAll()
  - Parse JSON using MongoDB Document.parse() for extended JSON support ($oid, $date)
  - Bulk insert parsed documents using repository.saveAll()
  - Log seeding completion with document count
- Handle parsing errors with descriptive exception messages
- Use try-catch for file operations with proper error context

**Testing**:
- Unit test: Missing file throws exception with clear message
- Integration test: Seeding drops existing data
- Integration test: Seeding inserts all documents from file
- Integration test: Extended JSON fields ($oid, $date) parsed correctly
- Verify seeding runs on application startup

**Dependencies**: [TASK-004] - Requires AtmRepository, [TASK-010] - Requires MongoDB connection, [TASK-011] - Requires atm_data.json file available
