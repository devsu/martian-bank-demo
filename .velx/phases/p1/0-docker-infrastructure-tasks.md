# Docker Infrastructure Tasks

## Overview

Establish Docker containerization infrastructure FIRST to enable development and testing from day one.

## Tasks

### [TASK-001] - [AI] Create multi-stage Dockerfile for Java service

Create `atm-locator-java/Dockerfile` with build stage (gradle:8.14-jdk25) and runtime stage (eclipse-temurin:25-jre-alpine). Include HEALTHCHECK using `/actuator/health`.

---

### [TASK-002] - [AI] Configure .dockerignore and build optimization

Create `atm-locator-java/.dockerignore` to exclude build artifacts, IDE files, Git files, and documentation.

---

### [TASK-003] - [AI] Update docker-compose.yml to add Java ATM Locator service

Add `atm-locator-java` service to docker-compose.yaml on port 8002 (external) mapping to 8001 (internal), connected to bankapp-network with MongoDB dependency.

---

### [TASK-004] - [MANUAL] Verify Docker build and container startup

Build and start the Java service via Docker Compose. Verify container health, MongoDB connectivity, and rebuild cycle works correctly.
