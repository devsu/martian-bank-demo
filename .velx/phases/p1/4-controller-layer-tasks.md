# Controller Layer Tasks

## Overview

Create REST API endpoints and HTTP handling for ATM operations. This layer exposes service functionality via HTTP endpoints with proper request/response handling and CORS configuration.

## Prerequisites

- Service layer complete (TASK-006, TASK-007)
- AtmService and AtmSearchRequest available

## Tasks

### [TASK-008] - [AI] Implement AtmController with POST endpoint

**Why**: Exposes ATM search functionality via REST API endpoint matching existing Node.js implementation for API consumer compatibility.

**What**:
- Create AtmController class annotated with @RestController
- Add @RequestMapping("/api/atm") for base path
- Inject AtmService via constructor injection
- Implement POST endpoint with trailing slash: @PostMapping("/")
  - Accept @RequestBody AtmSearchRequest
  - Call atmService.findAtms(request)
  - Return List<Atm> with HTTP 200 status
  - Use @ResponseBody for automatic JSON serialization
- Add appropriate logging for incoming requests
- Configure content type as application/json

**Testing**:
- Verify controller endpoint is registered at POST /api/atm/
- Integration test: POST with empty body returns results
- Integration test: POST with filters returns filtered results
- Integration test: Response is valid JSON with correct structure

**Dependencies**: [TASK-006] - Requires AtmService, [TASK-007] - Uses AtmSearchRequest

---

### [TASK-009] - [AI] Configure CORS settings

**Why**: Enables cross-origin requests from frontend applications matching existing Node.js CORS configuration for compatibility with current API consumers.

**What**:
- Create WebConfig class annotated with @Configuration
- Implement WebMvcConfigurer interface
- Override addCorsMappings() method
- Configure CORS settings matching Node.js implementation:
  - allowedOriginPatterns("*") - allows any origin
  - allowedMethods: GET, POST, PUT, DELETE, OPTIONS
  - allowedHeaders("*") - all headers permitted
  - allowCredentials(true) - credentials supported
  - Apply to all paths ("/**")

**Testing**:
- Verify CORS headers present in HTTP responses
- Test OPTIONS preflight request returns correct headers
- Verify cross-origin POST request succeeds

**Dependencies**: [TASK-002] - Requires config package structure
