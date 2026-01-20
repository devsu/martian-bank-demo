# Requirements

## Functional Requirements

### API Endpoint Migration
1. Implement POST /api/atm/ endpoint that accepts optional boolean filters (isOpenNow, isInterPlanetary) and returns up to 4 randomized ATM records with name, coordinates, address, and operational status
2. Implement POST /atm/add endpoint that creates new ATM records with full payload including name, address fields (street, city, state, zip), coordinates (latitude, longitude), timing details (monFri, satSun, holidays), numberOfATMs, isOpen status, and interPlanetary flag
3. Implement GET /atm/:id endpoint that retrieves specific ATM details including coordinates, timings, atmHours, numberOfATMs, and isOpen status
4. Preserve exact HTTP response codes: 200 for successful queries, 201 for resource creation, 404 for not found scenarios
5. Maintain server-side filtering logic for isOpenNow and isInterPlanetary parameters
6. Implement result randomization and 4-result limit for ATM list endpoint

### Data Model Implementation
1. Create Java entity classes matching existing MongoDB ATM schema with nested structures for address, coordinates, and timings
2. Support all existing ATM fields: name, address (street, city, state, zip), coordinates (latitude, longitude), atmHours, timings (monFri, satSun, holidays), numberOfATMs, isOpen, interPlanetary
3. Implement automatic timestamp tracking for createdAt and updatedAt fields
4. Preserve MongoDB document structure including __v field for version tracking

### Framework Selection and Implementation
1. Select and integrate appropriate Java web framework (e.g., Spring Boot, Micronaut, Quarkus) for REST endpoint handling
2. Select and integrate appropriate data persistence framework (e.g., Spring Data MongoDB, Morphia) for MongoDB operations
3. Select and integrate appropriate API documentation framework (e.g., SpringDoc OpenAPI, Swagger) for interactive documentation generation

### Error Handling Enhancements
1. Implement structured exception hierarchy for different error types (validation errors, not found errors, database errors)
2. Create standardized error response format with consistent structure across all endpoints
3. Implement proper exception mapping to HTTP status codes (400 for validation, 404 for not found, 500 for server errors)
4. Handle MongoDB ObjectId casting errors with appropriate 404 responses
5. Suppress stack traces in production environment while including them in development

### API Documentation
1. Generate interactive API documentation equivalent to existing Swagger UI served at /docs endpoint
2. Provide machine-readable OpenAPI specification at /docs.json endpoint
3. Document all endpoints with request/response schemas, parameter definitions, and HTTP status codes
4. Include example request and response payloads for each endpoint
5. Document optional parameters and filtering behavior

## Non-Functional Requirements

### Type Safety
- Leverage Java static typing for all request DTOs, response DTOs, and domain entities
- Enforce compile-time type checking for all data structures
- Use appropriate primitive types and validation annotations for field constraints

### Code Quality
- Implement separation of concerns with distinct layers: controllers, services, repositories, and entities
- Follow standard Java naming conventions and project structure
- Maintain modular architecture similar to existing Node.js structure

### Maintainability
- Use dependency injection for component wiring
- Externalize configuration through environment variables or application properties
- Implement clear logging with appropriate log levels for debugging and monitoring

### Compatibility
- Support existing MongoDB connection patterns (local and Atlas deployments)
- Maintain compatibility with Node.js 14+ for any coexisting services
- Ensure Java 11+ runtime compatibility

### Performance
- Maintain or improve response times compared to existing Node.js implementation
- Support concurrent request handling equivalent to current service capacity
- Optimize database query patterns (note: in-memory randomization should be evaluated for large datasets)
