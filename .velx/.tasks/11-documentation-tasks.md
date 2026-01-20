# Documentation Tasks

## Overview

Generate comprehensive API documentation using SpringDoc OpenAPI with interactive Swagger UI, ensuring developers and consumers have clear, accurate, and up-to-date API reference documentation.

## Prerequisites

- All controllers implemented ([TASK-021], [TASK-022], [TASK-023])
- Application configuration complete ([TASK-026])
- DTOs with validation annotations created ([TASK-007])

## Tasks

### [TASK-027] - [AI] Configure SpringDoc OpenAPI with Swagger UI

**Why**: Auto-generated API documentation stays synchronized with code and eliminates manual documentation maintenance.

**What**:
- Add springdoc-openapi-starter-webmvc-ui dependency to pom.xml (already in TASK-003)
- Create OpenApiConfig class with @Configuration annotation
- Define @Bean for OpenAPI with API metadata (title, version, description, contact)
- Configure server URLs for development and production environments
- Set up security schemes for JWT authentication (Bearer token)
- Configure Swagger UI path to /docs (custom path from default /swagger-ui.html)
- Configure OpenAPI JSON spec path to /docs.json
- Enable tryItOut functionality in Swagger UI
- Configure response examples and schema documentation
- Set up tag grouping for endpoint organization

**Testing** (TDD - write tests first):
- Integration test: Application starts with SpringDoc enabled
- Integration test: Swagger UI accessible at http://localhost:8001/docs
- Integration test: OpenAPI JSON spec available at http://localhost:8001/docs.json
- Integration test: All endpoints appear in Swagger UI with descriptions
- Integration test: Try It Out functionality works for GET endpoints
- Integration test: Security scheme configured for protected endpoints

**Dependencies**: [TASK-026] application configuration

---

### [TASK-028] - [AI] Add OpenAPI annotations to all controller endpoints

**Why**: Annotations provide detailed endpoint documentation including parameters, responses, examples, and authentication requirements.

**What**:
- Add @Tag annotation to ATMController class for grouping endpoints
- Add @Operation annotation to each endpoint method with summary and description
- Add @Parameter annotations to all request parameters with descriptions and examples
- Add @ApiResponse annotations for all possible HTTP responses (200, 201, 400, 404, 500)
- Include example request and response payloads in annotations
- Add @Schema annotations to DTOs for field-level documentation
- Document validation constraints in field descriptions
- Add @SecurityRequirement annotation to POST /atm/add endpoint
- Ensure ObjectId $oid format documented in examples
- Document pagination parameters for list endpoints

**Testing** (TDD - write tests first):
- Integration test: Swagger UI displays all endpoint summaries and descriptions
- Integration test: Parameter descriptions appear with type information
- Integration test: Response examples show correct JSON structure with $oid format
- Integration test: Error response formats documented for each endpoint
- Integration test: Authentication requirement indicated on protected endpoints
- Integration test: DTO schemas include validation constraint documentation

**Dependencies**: [TASK-027] OpenAPI configuration, [TASK-021], [TASK-022], [TASK-023] controllers
