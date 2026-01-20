# Configuration Tasks

## Overview

Configure Spring Boot application settings, MongoDB connection with environment variable support, and Jackson ObjectMapper customization for proper JSON serialization including custom ObjectId handling.

## Prerequisites

- Maven project structure established ([TASK-002], [TASK-003])
- Custom ObjectId serializer created ([TASK-006])

## Tasks

### [TASK-012] - [AI] Configure MongoDB connection with environment variable support

**Why**: Externalized configuration enables deployment across different environments without code changes.

**What**:
- Create MongoConfig class with @Configuration annotation
- Configure spring.data.mongodb.uri in application.yml with default value
- Support MONGODB_URI environment variable override
- Enable auto-index-creation for MongoDB indexes
- Configure connection pool settings (min/max connections)
- Add connection timeout and socket timeout configuration
- Support both local MongoDB and Atlas connection strings
- Configure database name extraction from URI

**Testing** (TDD - write tests first):
- Integration test: Application starts with default MongoDB URI
- Integration test: MONGODB_URI environment variable overrides default
- Integration test: Connection pool settings applied correctly
- Integration test: Invalid URI format fails fast with clear error message
- Unit test: Configuration bean creates MongoTemplate successfully

**Dependencies**: [TASK-003] MongoDB dependencies

---

### [TASK-013] - [AI] Configure Jackson ObjectMapper for custom serialization

**Why**: Custom ObjectMapper configuration applies ObjectId serialization globally across all endpoints.

**What**:
- Create JacksonConfig class with @Configuration annotation
- Define @Bean for ObjectMapper with custom configuration
- Register MongoObjectIdSerializer and MongoObjectIdDeserializer modules
- Configure JSON property naming strategy (match Node.js snake_case if needed)
- Set SerializationFeature.WRITE_DATES_AS_TIMESTAMPS to false for ISO 8601 dates
- Configure DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES to false for flexibility
- Set default property inclusion to JsonInclude.Include.NON_NULL to omit null fields
- Register JavaTimeModule for proper Java 8 date/time handling

**Testing** (TDD - write tests first):
- Unit test: ObjectId serializes with $oid wrapper format
- Unit test: Dates serialize as ISO 8601 strings, not timestamps
- Unit test: Null fields omitted from JSON output
- Unit test: Unknown properties in input JSON ignored without error
- Unit test: Custom serializer and deserializer registered in ObjectMapper

**Dependencies**: [TASK-006] custom ObjectId serializer/deserializer

---

### [TASK-026] - [AI] Create application.yml with externalized configuration

**Why**: Centralized configuration management with sensible defaults and environment-specific profiles.

**What**:
- Create application.yml in src/main/resources with base configuration
- Configure server.port with default 8001 and SERVER_PORT env var support
- Configure spring.data.mongodb.uri with default and MONGODB_URI env var support
- Configure spring.application.name as "atm-locator"
- Enable Spring Boot Actuator endpoints (health, info, metrics)
- Create application-development.yml profile with debug logging and seed data enabled
- Create application-production.yml profile with INFO logging and seed data disabled
- Configure CORS settings for allowed origins (localhost in dev, specific domains in prod)
- Set logging.level for org.springframework and application packages
- Configure management.endpoints.web.exposure.include for Actuator endpoints

**Testing** (TDD - write tests first):
- Integration test: Application starts with default profile and configuration
- Integration test: Development profile activates with SPRING_PROFILES_ACTIVE=development
- Integration test: Production profile activates with SPRING_PROFILES_ACTIVE=production
- Integration test: Environment variables override application.yml values
- Integration test: Actuator health endpoint responds with UP status

**Dependencies**: [TASK-012] MongoDB config, [TASK-013] Jackson config
