# Project Setup Tasks

## Overview

Establish the Maven project structure with Spring Boot 3.x, configure all required dependencies, and set up the build system for Java 17+ development. This component provides the foundation for all subsequent implementation work.

## Prerequisites

- JDK 17+ installed on development machines
- Maven 3.6+ for build automation
- IDE with Spring Boot and Lombok plugin support

## Tasks

### [TASK-001] - [MANUAL] Review Spring Boot architecture and approve technology stack

**Why**: Architecture approval ensures team alignment and stakeholder buy-in before significant development investment.

**What**:
- Review arc42 architecture documentation (sections 01-08)
- Validate Spring Boot 3.x framework selection against organizational standards
- Confirm Java 17+ and Maven build tooling approval
- Review MongoDB integration strategy and $oid compatibility approach
- Approve layered architecture pattern (Controller-Service-Repository)
- Sign off on SpringDoc OpenAPI for documentation generation
- Validate JWT authentication approach for protected endpoints
- Confirm backward compatibility requirements with existing clients

**Dependencies**: None (first task)

---

### [TASK-002] - [AI] Create Maven project structure with Spring Boot parent POM

**Why**: Spring Boot parent POM provides dependency management and reduces configuration overhead.

**What**:
- Create root pom.xml with spring-boot-starter-parent 3.2.x
- Configure project metadata (groupId, artifactId, version, packaging)
- Set Java version to 17 with maven.compiler.source and target properties
- Configure standard Maven directory structure (src/main/java, src/main/resources, src/test/java, src/test/resources)
- Set project name to "atm-locator" with description
- Configure UTF-8 encoding for sources

**Testing** (TDD - write tests first):
- Verify pom.xml validates successfully with mvn validate
- Test mvn clean compiles without errors
- Confirm Java 17 compilation settings with mvn help:effective-pom

**Dependencies**: [TASK-001] architecture approval

---

### [TASK-003] - [AI] Configure Maven dependencies and build plugins

**Why**: Comprehensive dependency configuration enables all required functionality without manual library management.

**What**:
- Add spring-boot-starter-web for REST API and embedded Tomcat
- Add spring-boot-starter-data-mongodb for MongoDB integration
- Add spring-boot-starter-validation for JSR-380 bean validation
- Add spring-boot-starter-actuator for health checks and metrics
- Add springdoc-openapi-starter-webmvc-ui for OpenAPI documentation
- Add lombok for boilerplate reduction
- Add spring-boot-starter-test for testing framework (JUnit 5, Mockito, AssertJ)
- Configure spring-boot-maven-plugin for executable JAR packaging
- Add maven-surefire-plugin for test execution
- Add jacoco-maven-plugin for code coverage reporting with 80% threshold

**Testing** (TDD - write tests first):
- Run mvn dependency:tree to verify no conflicts
- Execute mvn clean package to build executable JAR
- Verify JAR manifest includes main class and classpath
- Confirm test dependencies available with mvn test (even with no tests yet)

**Dependencies**: [TASK-002] project structure
