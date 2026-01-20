# Integration Points

## Overview
The ATM Locator service has minimal external integration requirements, primarily focusing on database connectivity and client API access. The migration to Java maintains these existing integration patterns while leveraging Spring Boot's robust integration capabilities for improved reliability and monitoring.

## External Systems

### MongoDB Database
- **Purpose**: Primary data persistence for ATM location records, operational details, and metadata
- **Integration Type**: Direct database connection using MongoDB Wire Protocol
- **Data Flow**: Bidirectional - read ATM records for queries, write new ATM records on creation
- **Protocol/Format**: MongoDB Wire Protocol over TCP, BSON document format
- **Authentication**: Connection string with username/password (for Atlas) or unauthenticated (for local development)
- **Frequency**: Real-time, synchronous per request
- **Dependencies**: All API endpoints depend on database availability
- **Connection Configuration**:
  - Local: `mongodb://localhost:27017/atm-locator`
  - Atlas: `mongodb+srv://<username>:<password>@<cluster>.mongodb.net/atm-locator`

### Client Applications (Consumers)
- **Purpose**: External applications and services that consume the ATM Locator API
- **Integration Type**: RESTful HTTP API
- **Data Flow**: Inbound requests for ATM data, outbound JSON responses
- **Protocol/Format**: HTTP/HTTPS with JSON payloads
- **Authentication**: Currently public endpoints (except POST /atm/add which requires authentication - to be addressed separately)
- **Frequency**: On-demand based on client needs
- **Dependencies**: Clients depend on service availability and API contract stability

## Internal Service Integrations
The ATM Locator operates as a standalone microservice with no direct service-to-service integrations. The layered architecture provides internal component integration:

### Controller ↔ Service Layer
- **Communication**: Synchronous method calls via Spring dependency injection
- **Data Exchanged**: DTOs for requests/responses, domain entities for business logic
- **Pattern**: Request-response with exception propagation for error handling

### Service ↔ Repository Layer
- **Communication**: Synchronous method calls through Spring Data interfaces
- **Data Exchanged**: Domain entities and MongoDB documents
- **Pattern**: Repository pattern with query derivation and MongoTemplate for complex queries

### Application ↔ Configuration
- **Communication**: Environment variable and properties file loading at startup
- **Data Exchanged**: Configuration values for database connection, server port, CORS settings
- **Pattern**: Spring Boot auto-configuration with externalized configuration

## Data Integration Patterns

### Pattern 1: Document-Based Persistence
- **Use Case**: All ATM data storage and retrieval operations
- **Approach**: Direct mapping between Java POJOs and MongoDB documents using Spring Data MongoDB
- **Trade-offs**:
  - Pros: Preserves existing document structure, no impedance mismatch, flexible schema
  - Cons: Less suitable for relational data, requires careful index management

### Pattern 2: Seed Data Loading
- **Use Case**: Initial database population from atm_data.json on application startup
- **Approach**: ApplicationRunner implementation that reads JSON file and bulk inserts using MongoTemplate
- **Trade-offs**:
  - Pros: Ensures consistent test data, replicates existing behavior
  - Cons: Destructive operation (drops existing data), not suitable for production

### Pattern 3: Result Randomization
- **Use Case**: Returning random subset of ATMs for discovery
- **Approach**: In-memory shuffling after database query (maintaining existing logic)
- **Trade-offs**:
  - Pros: Simple implementation, predictable behavior
  - Cons: Not scalable for large datasets, consider MongoDB aggregation pipeline for future optimization

## Integration Standards Compliance
No enterprise integration standards specified. The service follows REST best practices and MongoDB conventions to ensure broad compatibility.

## Error Handling and Resilience

### Database Connection Resilience
- **Retry Strategy**: Spring Boot's MongoDB auto-configuration includes connection retry logic with exponential backoff
- **Connection Pooling**: MongoClient connection pool prevents connection exhaustion
- **Timeout Configuration**: Configurable connection and socket timeouts to prevent hanging requests
- **Health Checks**: Spring Boot Actuator provides MongoDB health indicator for monitoring

### API Error Responses
- **Standardized Format**: Consistent JSON error responses with status code, message, and timestamp
- **Status Code Mapping**:
  - 200: Successful query
  - 201: Successful creation
  - 404: Resource not found or invalid ObjectId
  - 400: Validation errors
  - 500: Server errors
- **Graceful Degradation**: Service returns appropriate errors when database is unavailable rather than crashing

### Circuit Breakers
- **Applicability**: Not implemented initially as there are no external service dependencies beyond database
- **Future Consideration**: Can add Resilience4j if external service integrations are added

### Monitoring
- **Spring Boot Actuator**: Provides health, metrics, and info endpoints for operational monitoring
- **Database Metrics**: Connection pool statistics, query performance metrics
- **Request Logging**: Structured logging of all API requests and responses for troubleshooting

## Security Considerations

### API Security
- **Authentication**: JWT token validation for protected endpoints (POST /atm/add)
  - Note: JWT implementation details to be determined based on existing authentication service
  - Token validation will be handled via Spring Security filters
- **Authorization**: Role-based access control for administrative endpoints
- **Input Validation**: Bean Validation annotations prevent injection attacks
- **CORS Configuration**: Configurable allowed origins, methods, and headers

### Data Security
- **Connection Encryption**: TLS/SSL for MongoDB Atlas connections
- **Credentials Management**: Externalized via environment variables, never hardcoded
- **Sensitive Data**: No PII or sensitive data in ATM location records

### Network Security
- **Transport Security**: HTTPS recommended for production deployment
- **Firewall Rules**: Restrict MongoDB access to application servers only
- **Port Security**: Service exposed on configurable port (default 8001) with appropriate firewall rules