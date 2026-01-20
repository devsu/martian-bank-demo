# Seed Data Tasks

## Overview

Implement automatic seed data loading on application startup to replicate Node.js behavior, ensuring consistent test environments and immediate development readiness with pre-populated ATM data.

## Prerequisites

- MongoDB configuration established ([TASK-012])
- ATM entity and repository created ([TASK-004], [TASK-010])
- Application configuration with profile support ([TASK-026])

## Tasks

### [TASK-014] - [AI] Create seed data loader with ApplicationRunner

**Why**: ApplicationRunner ensures seed data loads automatically after Spring context initialization, providing immediate development readiness.

**What**:
- Create SeedDataLoader class with @Component annotation
- Implement ApplicationRunner interface with run(ApplicationArguments) method
- Inject ATMRepository and ObjectMapper via constructor
- Read atm_data.json from classpath resources using ResourceLoader
- Implement conditional loading based on LOAD_SEED_DATA environment variable or profile
- Log start and completion of seed data loading with record count
- Handle file not found exception gracefully with warning log
- Ensure loader runs only once per application startup

**Testing** (TDD - write tests first):
- Integration test: Seed loader executes during application startup
- Integration test: Seed data file read from classpath successfully
- Unit test: LOAD_SEED_DATA=true triggers seed loading
- Unit test: LOAD_SEED_DATA=false skips seed loading
- Unit test: Missing seed data file logs warning without crashing application
- Unit test: Verify ApplicationRunner.run called on startup

**Dependencies**: [TASK-012] MongoDB config, [TASK-010] repository

---

### [TASK-015] - [AI] Implement seed data loading logic with duplicate checking

**Why**: Idempotent seed loading prevents duplicate data on application restarts while ensuring database initialization.

**What**:
- Check if ATM collection is empty using repository.count()
- Skip loading if count > 0 to prevent duplicates
- Parse atm_data.json file into List<ATM> using ObjectMapper
- Handle JSON parsing errors with detailed error messages
- Validate parsed ATM entities before insertion
- Use repository.saveAll() for bulk insert efficiency
- Log number of records loaded successfully
- Handle MongoDB connection errors during loading
- Ensure seed data file contains same ATMs as Node.js version

**Testing** (TDD - write tests first):
- Integration test: Empty collection triggers seed data loading
- Integration test: Existing data (count > 0) skips loading
- Integration test: All ATMs from seed file inserted successfully
- Integration test: Bulk insert more efficient than individual saves
- Unit test: JSON parsing creates valid ATM entities
- Unit test: Invalid JSON format logs error and skips loading
- Unit test: MongoDB connection failure during load handled gracefully
- Integration test: Re-running application doesn't duplicate data

**Dependencies**: [TASK-014] seed loader structure, [TASK-004] ATM entity, [TASK-010] repository
