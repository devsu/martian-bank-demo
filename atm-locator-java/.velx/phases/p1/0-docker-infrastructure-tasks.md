# Docker Infrastructure Tasks

## Overview

Establish Docker containerization infrastructure FIRST to enable development and testing from day one.

## Tasks

### [P1-001] - [AI] Create multi-stage Dockerfile for Java service

Create `atm-locator-java/Dockerfile` with build stage (gradle:8.14-jdk25) and runtime stage (eclipse-temurin:25-jre-alpine). Include HEALTHCHECK using `/actuator/health`.

---

### [P1-002] - [AI] Configure .dockerignore and build optimization

Create `atm-locator-java/.dockerignore` to exclude build artifacts, IDE files, Git files, and documentation.

---

### [P1-003] - [AI] Update docker-compose.yml to replace Node.js ATM Locator with Java service

Replace existing `atm-locator` service with Java implementation. Update build context to `./atm-locator-java`, keep port 8001 mapping. Comment out original Node.js config for reference. Maintain bankapp-network connection and MongoDB dependency.

---

### [P1-004] - [MANUAL] Verify Docker build and container startup

Build and start the Java service via Docker Compose. Verify container health, MongoDB connectivity, and rebuild cycle works correctly.
