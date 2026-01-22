# Data Layer Tasks

## Overview

Implement MongoDB document models and repository interfaces to replace mocked service data with real persistence.

## Tasks

### [TASK-013] - [AI] Create Atm entity with nested models

Create Atm entity with @Document(collection = "atms") containing: id, name, location (nested), isOpenNow, isInterPlanetary, createdAt, updatedAt. Create nested models: Location, Address, Coordinates. Use Lombok annotations.

---

### [TASK-014] - [AI] Implement AtmRepository interface

Create AtmRepository extending MongoRepository<Atm, String>. Keep simple with inherited findAll() - filtering done in service layer to match Node.js behavior.

---

### [TASK-015] - [AI] Wire AtmService to AtmRepository

Inject AtmRepository into AtmServiceImpl. Create entity-to-DTO mapper method. Update findAtms() to fetch from repository, convert to DTOs, apply filters, and randomize results.
