# Documentation Tasks (API Contract First)

## Overview

Establish OpenAPI documentation before implementing the creation endpoint to define the API contract first.

## Tasks

### [TASK-001] - [AI] Add SpringDoc OpenAPI dependency and configuration

Add springdoc-openapi-starter-webmvc-ui dependency to build.gradle. Create OpenApiConfig with API metadata (title, version, description). Configure Swagger UI path as /docs and API spec path as /docs.json.

---

### [TASK-002] - [AI] Document existing endpoints with OpenAPI annotations

Add @Tag, @Operation, and @ApiResponses annotations to AtmController endpoints. Add @Schema annotations to DTOs for field descriptions and examples.
