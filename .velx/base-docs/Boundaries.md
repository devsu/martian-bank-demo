# Scope Boundaries

## In Scope

### Code Migration
- Complete migration of atm-locator service from Node.js to Java
- Implementation of all three existing endpoints (POST /api/atm/, POST /atm/add, GET /atm/:id)
- Migration of ATM data model with all fields and nested structures
- Migration of filtering and randomization logic

### Framework Selection
- Research and recommendation of appropriate Java web framework
- Research and recommendation of appropriate data persistence framework for MongoDB
- Research and recommendation of appropriate API documentation framework
- Implementation and configuration of selected frameworks

### Error Handling
- Design and implementation of structured exception hierarchy
- Creation of standardized error response format
- Mapping of exceptions to appropriate HTTP status codes
- Environment-based stack trace handling

### Type Safety
- Creation of strongly-typed request and response DTOs
- Implementation of type-safe domain entities
- Use of validation annotations for field constraints

### API Documentation
- Generation of interactive Swagger/OpenAPI documentation
- Implementation of /docs and /docs.json endpoints
- Documentation of all request/response schemas and parameters

### Configuration
- Environment variable support for database connection (local and Atlas)
- Port configuration
- Environment-based behavior (development vs production)

## Out of Scope

### Infrastructure Changes
- Changes to MongoDB database schema or data structure
- Migration or modification of existing seed data in atm_data.json
- Changes to deployment infrastructure or containerization strategy
- Changes to database indexes or performance tuning

### Authentication and Authorization
- Implementation of JWT authentication mechanism (existing dependency will be noted but not migrated)
- Implementation of access control for private endpoints
- Role-based authorization logic

### Frontend and Client Changes
- Modifications to any consuming applications or clients
- Changes to API contracts that would require client updates
- Development of new client libraries or SDKs

### Additional Features
- New endpoints beyond the three existing ones
- New filtering capabilities beyond isOpenNow and isInterPlanetary
- Real-time availability calculation based on timings (preserving current boolean flag approach)
- Geospatial queries or distance-based filtering
- Search functionality by name, address, or other fields

### Cross-Cutting Concerns
- Distributed tracing or APM integration
- Metrics collection and monitoring dashboard setup
- Rate limiting or throttling implementation
- CORS policy modifications (will preserve existing configuration)
- Caching layer implementation

### Database Operations
- Database backup or migration procedures
- Data transformation or enrichment
- Database connection pooling optimization (will use framework defaults)
- Transaction management beyond framework defaults

### Testing Infrastructure
- End-to-end testing framework setup
- Performance testing or load testing
- Migration of existing tests from Node.js (if any exist)

### Documentation Beyond API Specs
- Operational runbooks or deployment guides
- Architecture decision records (ADRs)
- Developer onboarding documentation
