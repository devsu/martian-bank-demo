# Technology Stack

## Overview
The technology stack for the migrated ATM Locator service prioritizes mature, well-supported Java technologies that directly address the requirements for type safety, enhanced error handling, and API documentation while maintaining full compatibility with the existing MongoDB database and API contract.

## Core Technologies

### Backend Framework
- **Technology**: Spring Boot 3.x
- **Version**: 3.2.x (latest stable)
- **Rationale**: Spring Boot provides a comprehensive, production-ready framework with embedded server, auto-configuration, and extensive MongoDB support. Its convention-over-configuration approach accelerates development while maintaining flexibility. The framework's maturity ensures long-term support and extensive community resources.
- **Alternatives Considered**:
  - Micronaut: Faster startup and lower memory footprint but smaller ecosystem and less MongoDB integration maturity
  - Quarkus: Optimized for cloud-native and GraalVM but adds complexity for straightforward migration needs
  - Vanilla Spring MVC: More configuration overhead without Boot's auto-configuration benefits

### Web Server
- **Technology**: Embedded Apache Tomcat
- **Version**: 10.x (bundled with Spring Boot 3.x)
- **Rationale**: Tomcat comes embedded with Spring Boot, eliminating deployment complexity. It provides enterprise-grade performance, connection pooling, and thread management out-of-the-box. No separate application server installation required.
- **Alternatives Considered**:
  - Undertow: Slightly better performance but less widespread adoption
  - Jetty: Lighter weight but Tomcat has better Spring Boot integration

### Data Persistence
- **Technology**: Spring Data MongoDB
- **Version**: Aligned with Spring Boot version
- **Rationale**: Native MongoDB support without ORM translation layers preserves exact document structure. Provides repository abstraction, query derivation, and MongoTemplate for complex operations. Seamlessly integrates with Spring Boot auto-configuration for both local and Atlas deployments.
- **Alternatives Considered**:
  - MongoDB Java Driver (standalone): Lower level, requires more boilerplate code
  - Morphia: Less Spring integration, smaller community
  - Panache (Quarkus): Would require Quarkus framework adoption

### API Documentation
- **Technology**: SpringDoc OpenAPI
- **Version**: 2.3.x
- **Rationale**: Generates OpenAPI 3.0 specification from code annotations, provides embedded Swagger UI, actively maintained replacement for deprecated Springfox. Automatically discovers Spring endpoints and generates accurate documentation from code.
- **Alternatives Considered**:
  - Springfox: Deprecated, no longer maintained
  - Manual OpenAPI YAML: Prone to drift from implementation
  - Spring REST Docs: Test-driven approach adds complexity for simple API

### Type Safety & Validation
- **Technology**: Jakarta Bean Validation (Hibernate Validator)
- **Version**: 3.0.x (Jakarta EE 10)
- **Rationale**: Standard Java validation API integrated with Spring Boot. Provides declarative validation through annotations, automatic request validation, and custom constraint support. Ensures type safety at compile time and runtime validation.
- **Alternatives Considered**:
  - Manual validation: Error-prone and verbose
  - Apache Commons Validator: Less integrated with Spring

### Java Runtime
- **Technology**: OpenJDK
- **Version**: 25
- **Rationale**: Java 25 provides the latest language features, performance improvements, and modern capabilities. Includes enhanced pattern matching, virtual threads, and improved concurrency support for better application performance.
- **Alternatives Considered**:
  - Java 17 LTS: Older LTS version with long-term support but missing latest features
  - Java 21 LTS: Previous LTS version but superseded by Java 25 for this project
  - GraalVM: Adds complexity without clear benefits for this use case

## Infrastructure and Deployment

### Hosting
- **Platform**: Container-based deployment (Docker)
- **Rationale**: Maintains consistency with existing Node.js deployment approach. Spring Boot applications package as single JAR files that run anywhere Java is available. Container deployment ensures environment consistency and simplifies operations.

### Deployment Strategy
- **Approach**: Containerized JAR with externalized configuration
- **Rationale**: Spring Boot's executable JAR packaging includes embedded server, simplifying deployment. Docker containers ensure consistency across environments. Environment variables provide configuration without rebuilding.

### Build Tool
- **Technology**: Maven
- **Version**: 3.9.x
- **Rationale**: Mature, well-supported build tool with excellent Spring Boot integration. Maven's convention-over-configuration aligns with Spring Boot philosophy. Extensive plugin ecosystem for testing, packaging, and deployment.
- **Alternatives Considered**:
  - Gradle: More flexible but steeper learning curve, Maven is more widely understood

## Cross-Cutting Technologies

### Logging
- **Technology**: SLF4J with Logback
- **Rationale**: Spring Boot default logging framework. SLF4J provides facade pattern for flexibility. Logback offers high performance and extensive configuration options. Automatic integration with Spring Boot's logging configuration.

### JSON Processing
- **Technology**: Jackson
- **Rationale**: Spring Boot default JSON processor. Handles MongoDB ObjectId serialization, date formatting, and custom type mappings. Extensive annotation support for controlling serialization behavior.

### Development Tools
- **Technology**: Spring Boot DevTools
- **Rationale**: Provides automatic application restart on code changes, LiveReload support, and development-time optimizations. Improves developer productivity during migration.

### Testing Framework
- **Technology**: JUnit 5 with Spring Boot Test
- **Rationale**: Spring Boot includes comprehensive testing support with mock MVC, embedded MongoDB for integration tests, and test containers support. Enables thorough testing during migration.

## Dependencies and Constraints

### From Enterprise Standards
No enterprise standards specified, allowing technology selection based purely on technical merit and requirements fit.

### From Components
Not applicable - this is a new Java implementation replacing the existing Node.js service.

### Technical Constraints
- **MongoDB Compatibility**: Spring Data MongoDB supports all MongoDB versions compatible with the existing Node.js service
- **Java Version**: Java 25 is required for this project to leverage the latest language features and performance improvements
- **Container Support**: Spring Boot applications naturally support containerization with minimal configuration
- **Port Configuration**: Spring Boot supports port configuration via environment variables or properties, maintaining compatibility with port 8001