# Docker Tasks

## Overview

Create a production-ready multi-stage Dockerfile that builds the Java service and packages it into a minimal runtime container. The Dockerfile must produce a container compatible with the existing Docker Compose environment and include the atm_data.json file for automatic data seeding on startup.

## Prerequisites

- Phase-03 complete with all endpoints functional and tested
- Gradle build configuration in place from Phase-01
- atm_data.json file present at src/main/resources/config/

## Tasks

### [TASK-001] - [AI] Create multi-stage Dockerfile with build and runtime stages

**What**:
- Multi-stage Dockerfile with separate build and runtime stages
- Build stage using gradle:9.1-jdk25 base image with full Gradle build environment
- Runtime stage using eclipse-temurin:25-jre-alpine for minimal footprint
- Gradle build execution with `-x test` flag (tests already validated in Phase-03)
- JAR artifact copying from build stage to runtime stage
- atm_data.json file copying from build stage to /app/config/atm_data.json in runtime image
- Port 8001 exposed for API compatibility
- Entrypoint configured to execute JAR with `java -jar app.jar`
- Working directory set to /app in runtime stage

**Testing** (TDD - write tests first):
- Manual build test: `docker build -t atm-locator-java:test .` completes successfully
- Image size verification: Final image under 300MB (Alpine JRE base provides small footprint)
- Layer inspection: Verify atm_data.json present at /app/config/atm_data.json
- Container startup test: `docker run -e DB_URL=mongodb://host:27017/martianbank -p 8001:8001 atm-locator-java:test` starts without errors

**Dependencies**: None (foundational task)

---

### [TASK-002] - [AI] Configure Docker build optimization and image bundling

**Why**: Ensure reproducible builds with proper layer caching and dependency management for faster iteration during development and deployment.

**What**:
- .dockerignore file to exclude build artifacts, IDE files, and test resources
- Gradle dependency caching strategy in build stage (copy gradle wrapper and dependencies before source code)
- Build-time validation that atm_data.json exists and is valid JSON
- Health check configuration in Dockerfile using `/actuator/health` endpoint
- Image labeling with version, build date, and commit SHA for traceability

**Testing**:
- Build performance test: Second build with no source changes completes in under 30 seconds (layer caching working)
- Verify .dockerignore excludes: .git/, build/, .gradle/, *.log, .idea/
- Health check test: `docker inspect atm-locator-java:test` shows HEALTHCHECK instruction
- Label verification: Image metadata includes version, build date, commit labels

**Dependencies**: [TASK-001] (builds on base Dockerfile)

---
