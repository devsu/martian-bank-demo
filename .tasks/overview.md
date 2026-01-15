# ATM-Locator Migration: Node.js to Java 25 with Spring Boot

## Overview

This plan describes the migration of the `atm-locator` microservice from Node.js/Express to Java 25 with Spring Boot 3.x. The migration preserves **exact functional parity** with the legacy application while modernizing the technology stack.

## Objective

Migrate the atm-locator microservice to Java 25 while keeping the following **EXACTLY THE SAME**:
- Exposed Endpoints (paths, HTTP methods)
- Request parameters and body formats
- Input validations
- Database operations and query logic
- Response data structures
- Response formats (JSON structure, field names, status codes)

## Current State (Legacy Node.js Application)

### Technology Stack
| Component | Technology |
|-----------|------------|
| Runtime | Node.js 14 |
| Framework | Express 4.18.2 |
| Database | MongoDB (Mongoose 7.2.4) |
| Port | 8001 |
| Documentation | Swagger/OpenAPI |

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/atm/` | List ATMs with optional filters (returns max 4 randomized) |
| POST | `/api/atm/add` | Create a new ATM record |
| GET | `/api/atm/:id` | Get specific ATM details by ID |

### Key Source Files (Legacy)
- `atm-locator/controllers/atmController.js` - Business logic
- `atm-locator/models/atmModel.js` - Mongoose schema
- `atm-locator/routes/atmRoutes.js` - Route definitions
- `atm-locator/config/db.js` - Database connection and seeding
- `atm-locator/middleware/errorMiddleware.js` - Error handling
- `atm-locator/config/atm_data.json` - Seed data (13 ATM records)

## Target State (Java 25 Application)

### Technology Stack
| Component | Technology |
|-----------|------------|
| Runtime | Java 25 |
| Framework | Spring Boot 3.x |
| Database | Spring Data MongoDB |
| Build Tool | Gradle |
| Port | 8001 |
| Documentation | Springdoc OpenAPI |
| Testing | JUnit 5 + Mockito |

### Project Structure (Target)
```
atm-locator-java/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── src/
│   ├── main/
│   │   ├── java/com/martianbank/atmlocator/
│   │   │   ├── AtmLocatorApplication.java
│   │   │   ├── config/
│   │   │   │   ├── MongoConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── DataSeeder.java
│   │   │   ├── model/
│   │   │   │   └── Atm.java
│   │   │   ├── repository/
│   │   │   │   └── AtmRepository.java
│   │   │   ├── service/
│   │   │   │   └── AtmService.java
│   │   │   ├── controller/
│   │   │   │   └── AtmController.java
│   │   │   ├── dto/
│   │   │   │   ├── AtmFilterRequest.java
│   │   │   │   ├── AtmCreateRequest.java
│   │   │   │   ├── AtmListResponse.java
│   │   │   │   └── AtmDetailResponse.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── AtmNotFoundException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── atm_data.json
│   └── test/
│       └── java/com/martianbank/atmlocator/
│           ├── service/
│           │   └── AtmServiceTest.java
│           ├── controller/
│           │   └── AtmControllerTest.java
│           └── repository/
│               └── AtmRepositoryTest.java
```

## Migration Scope

### In Scope
- All 3 API endpoints with exact same behavior
- MongoDB integration via Spring Data MongoDB
- Database seeding on startup
- CORS configuration (credentials: true, all origins)
- Error handling with same response format
- Docker Compose integration
- OpenAPI documentation
- Unit testing with 90% coverage target

### Out of Scope
- Solving existing tech debt
- Addressing security issues
- Adding new features or improvements
- Performance optimizations beyond functional parity

## Critical Behaviors to Preserve

### 1. Query Logic (`getATMs`)
```javascript
// Legacy behavior to replicate exactly:
let query = { interPlanetary: false };  // Default
if (req.body.isOpenNow) query.isOpen = true;
if (req.body.isInterPlanetary) query.interPlanetary = true;
```

### 2. Response Projection (`getATMs`)
Only return these fields: `_id`, `name`, `coordinates`, `address`, `isOpen`

### 3. Randomization (`getATMs`)
Results shuffled using random sort, limited to 4 items maximum

### 4. Error Response Format
```json
{
  "message": "Error message here",
  "stack": null  // null in production, stack trace otherwise
}
```

### 5. Database Seeding
On startup: drop ATM collection, reseed from `atm_data.json`

## Implementation Phases

| Phase | Description | Deliverable |
|-------|-------------|-------------|
| 1 | Project Setup & Configuration | Spring Boot project with Gradle, MongoDB config |
| 1.5 | Tests for Phase 1 | Configuration tests |
| 2 | Data Model & Repository | ATM entity, repository interface, seeder |
| 2.5 | Tests for Phase 2 | Repository unit tests |
| 3 | Service Layer | Business logic with exact query behavior |
| 3.5 | Tests for Phase 3 | Service unit tests |
| 4 | REST Controllers & Error Handling | Endpoints with exact request/response format |
| 4.5 | Tests for Phase 4 | Controller unit tests |
| 5 | Docker Compose Integration | Dockerfile, docker-compose.yaml updates |
| 5.5 | Tests for Phase 5 | Integration verification |
| 6 | OpenAPI & Final Verification | Documentation, parity verification |
| 6.5 | Final Test Suite & Coverage | 90% coverage report |

## Success Criteria

1. All 3 endpoints respond with identical JSON structure as legacy
2. Query logic produces same results for same inputs
3. Error responses match legacy format exactly
4. Docker Compose works with existing infrastructure
5. NGINX routing unchanged (`/api/atm` -> `atm-locator:8001`)
6. 90% unit test coverage achieved
7. All existing UI functionality continues to work

## References

- Legacy source: `atm-locator/` directory
- Docker config: `docker-compose.yaml:59-71`
- NGINX routing: `nginx/default.conf:19-24`
- Seed data: `atm-locator/config/atm_data.json`
