# Project Setup Tasks

## Overview

Initialize the Spring Boot 3.5 project with Gradle 9.1 wrapper and establish the foundational package structure for layer-based architecture. This component provides the foundation for all subsequent development work.

## Prerequisites

- Java 25 JDK installed on development machine
- Git repository initialized
- Access to Spring Initializr or equivalent project generation tool

## Tasks

### [TASK-001] - [AI] Initialize Spring Boot project with Gradle wrapper

**Why**: Establishes consistent build tooling across development environments without requiring global Gradle installations, following Spring Boot best practices.

**What**:
- Generate Spring Boot 3.5 project using Spring Initializr with Gradle 9.1
- Include dependencies: Spring Web, Spring Data MongoDB, Spring Boot Actuator, Lombok
- Configure build.gradle with Java 25 source/target compatibility
- Include Gradle wrapper scripts for consistent builds across environments
- Set project metadata: group=com.martianbank, artifact=atm-locator, name=ATM Locator
- Configure application.properties with default server.port=8001

**Testing**:
- Verify Gradle wrapper executes: `./gradlew --version`
- Verify project builds successfully: `./gradlew build`
- Verify Spring Boot application starts: `./gradlew bootRun`

**Dependencies**: None (first task)

---

### [TASK-002] - [AI] Create layer-based package structure

**Why**: Establishes organized codebase structure that separates concerns clearly and follows Spring Boot conventions for maintainability and scalability.

**What**:
- Create package structure under src/main/java/com/martianbank/atmlocator/:
  - controller/ - REST API request handling
  - service/ - Business logic implementation
  - repository/ - Data persistence interfaces
  - model/ - Domain entities and DTOs
  - config/ - Configuration classes
  - util/ - Utility classes
- Create resources structure under src/main/resources/:
  - config/ - Configuration files (for atm_data.json)
  - application.properties - Application configuration
- Create test package structure mirroring main packages under src/test/java/

**Testing**:
- Verify directory structure exists with correct naming
- Verify project still compiles after structure creation

**Dependencies**: [TASK-001] - Requires initialized project
