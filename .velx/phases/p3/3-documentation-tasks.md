# Documentation Tasks

## Overview

Documentation components implement OpenAPI specification using SpringDoc to provide interactive API documentation matching the existing Node.js Swagger documentation. This enables consistent developer experience across implementations.

## Prerequisites

- SpringDoc OpenAPI dependency added ([TASK-002])
- All endpoints implemented (Phase-01, Phase-02, and POST /atm/add)
- Spring Boot application structure established

## Tasks

### [TASK-011] - [AI] Configure SpringDoc OpenAPI

**Why**: SpringDoc configuration enables automatic API documentation generation with proper endpoints and metadata.

**What**:
- Create OpenAPI configuration class with @Configuration
- Configure Swagger UI path to /docs
- Configure OpenAPI JSON specification path to /docs.json
- Set API metadata: title, version, description matching Node.js documentation
- Configure server URL for local development (http://localhost:8001)
- Enable automatic schema generation from DTOs and entities
- Configure response examples where appropriate

**Testing**:
- Verify /docs UI is accessible and renders correctly
- Verify /docs.json returns valid OpenAPI 3.0 specification
- Verify API metadata matches Node.js Swagger
- Manual browser testing of Swagger UI

**Dependencies**: [TASK-002] (requires SpringDoc dependency)

---

### [TASK-012] - [AI] Document all endpoints with OpenAPI annotations

**Why**: OpenAPI annotations provide detailed endpoint documentation for request/response schemas and examples.

**What**:
- Add @Operation annotations to all controller methods with descriptions
- Add @ApiResponse annotations for success and error responses
- Document POST /api/atm/ endpoint: request body, response array, filters
- Document GET /api/atm/{id} endpoint: path parameter, response object, 404 error
- Document POST /atm/add endpoint: request body, 201 response, validation errors
- Add @Schema annotations to DTOs for field descriptions
- Include example request/response payloads
- Match documentation style from Node.js Swagger

**Testing**:
- Verify /docs UI displays all three endpoints
- Verify request/response schemas are accurate
- Verify examples are helpful and valid
- Manual testing: try example requests from Swagger UI

**Dependencies**: [TASK-011] (requires OpenAPI configuration), [TASK-008] (requires POST /atm/add endpoint)
