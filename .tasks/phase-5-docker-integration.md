# Phase 5: Docker Compose Integration

## Overview

Create the Dockerfile for the Java 25 application and integrate it into the existing `docker-compose.yaml`, ensuring seamless replacement of the Node.js service.

## Prerequisites

- Phase 4 and 4.5 completed
- Application runs locally with `./gradlew bootRun`
- All tests passing

## Deliverables

1. `Dockerfile` - Multi-stage build for Java 25
2. `.dockerignore` - Docker ignore file
3. Updated `docker-compose.yaml` - Replace Node.js service with Java
4. `.env` file template for the Java service

## Legacy Reference

**Current docker-compose.yaml entry** (`docker-compose.yaml:59-71`):
```yaml
atm-locator:
  build:
    context: ./atm-locator
  container_name: atm-locator
  hostname: martian-bank-atm-locator
  image: martian-bank-atm-locator
  ports:
    - "8001:8001"
  env_file:
    - ./atm-locator/.env
  restart: always
  networks:
    - bankapp-network
```

**Current Dockerfile** (`atm-locator/Dockerfile`):
```dockerfile
FROM --platform=linux/amd64 node:14
WORKDIR /usr/src/app
COPY ./package*.json /usr/src/app
RUN npm install
COPY . ./
EXPOSE 8001
CMD ["npm", "start"]
```

## Implementation Steps

### Step 1: Create Dockerfile

**File**: `atm-locator-java/Dockerfile`

```dockerfile
# Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file.

# Multi-stage build for Java 25 Spring Boot application

# Stage 1: Build stage
FROM --platform=linux/amd64 eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port 8001 (matching legacy)
EXPOSE 8001

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8001/api/atm/ -X POST -H "Content-Type: application/json" -d '{}' || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 2: Create .dockerignore

**File**: `atm-locator-java/.dockerignore`

```
# Build outputs
build/
.gradle/
bin/
out/

# IDE files
.idea/
*.iml
.vscode/
*.swp
*.swo

# Git
.git/
.gitignore

# Test files (not needed in runtime)
src/test/

# Documentation
*.md
docs/

# Local environment files (will be provided via docker-compose)
.env
.env.*

# OS files
.DS_Store
Thumbs.db
```

### Step 3: Create Environment File Template

**File**: `atm-locator-java/.env`

```bash
# MongoDB connection string
# For local MongoDB via Docker Compose, use DATABASE_HOST instead
# DB_URL=mongodb://root:example@mongo:27017

# Local MongoDB host (takes precedence over DB_URL)
DATABASE_HOST=root:example@mongo

# Server port
PORT=8001

# Environment mode (production hides stack traces in errors)
NODE_ENV=production
```

### Step 4: Update docker-compose.yaml

Replace the existing `atm-locator` service block with the Java version:

**File**: `docker-compose.yaml` (update lines 59-71)

```yaml
  atm-locator:
    build:
      context: ./atm-locator-java
    container_name: atm-locator
    hostname: martian-bank-atm-locator
    image: martian-bank-atm-locator
    ports:
      - "8001:8001"
    environment:
      DATABASE_HOST: root:example@mongo
      PORT: 8001
      NODE_ENV: production
    depends_on:
      - mongo
    restart: always
    networks:
      - bankapp-network
```

**Key changes from legacy**:
- `build.context`: Changed from `./atm-locator` to `./atm-locator-java`
- `environment`: Inline environment variables instead of `env_file`
- `depends_on`: Added `mongo` dependency for proper startup order
- All other fields remain the same for compatibility

### Step 5: Verify NGINX Configuration

The NGINX configuration should remain unchanged. Verify that `nginx/default.conf:19-24` still routes correctly:

```nginx
location /api/atm {
    proxy_pass http://atm-locator:8001/api/atm/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

No changes needed since:
- Container name remains `atm-locator`
- Port remains `8001`
- Base path remains `/api/atm`

### Step 6: Create Alternative docker-compose Override (Optional)

For gradual migration, create an override file that allows switching between Node.js and Java:

**File**: `docker-compose.java.yml`

```yaml
# Override file for Java version of atm-locator
# Usage: docker-compose -f docker-compose.yaml -f docker-compose.java.yml up

services:
  atm-locator:
    build:
      context: ./atm-locator-java
    image: martian-bank-atm-locator-java
    environment:
      DATABASE_HOST: root:example@mongo
      PORT: 8001
      NODE_ENV: production
    depends_on:
      - mongo
```

## Directory Structure After Phase 5

```
martian-bank-demo/
├── docker-compose.yaml (updated)
├── docker-compose.java.yml (new, optional)
├── atm-locator/                    # Legacy Node.js (preserved)
│   ├── Dockerfile
│   ├── package.json
│   └── ...
├── atm-locator-java/               # New Java version
│   ├── Dockerfile (new)
│   ├── .dockerignore (new)
│   ├── .env (new)
│   ├── build.gradle
│   ├── settings.gradle
│   └── src/
│       └── ...
└── nginx/
    └── default.conf (unchanged)
```

## Build and Run Commands

### Build Java Image Only
```bash
docker build -t martian-bank-atm-locator ./atm-locator-java
```

### Run with Docker Compose
```bash
# Full stack with Java atm-locator
docker-compose up --build

# Just the atm-locator service
docker-compose up --build atm-locator

# Rebuild without cache
docker-compose build --no-cache atm-locator
```

### Run with Override File (Gradual Migration)
```bash
# Use Java version via override
docker-compose -f docker-compose.yaml -f docker-compose.java.yml up --build
```

### View Logs
```bash
docker-compose logs -f atm-locator
```

### Verify Service
```bash
# Check service is running
docker-compose ps atm-locator

# Test endpoint
curl -X POST http://localhost:8001/api/atm/ \
  -H "Content-Type: application/json" \
  -d '{"isOpenNow": true}'

# Via NGINX
curl -X POST http://localhost:8080/api/atm/ \
  -H "Content-Type: application/json" \
  -d '{}'
```

## Success Criteria

### Automated Verification

- [ ] Docker image builds successfully: `docker build -t atm-locator-java ./atm-locator-java`
- [ ] Container starts without errors: `docker-compose up -d atm-locator`
- [ ] Health check passes: `docker inspect --format='{{.State.Health.Status}}' atm-locator`
- [ ] Endpoint responds: `curl -X POST http://localhost:8001/api/atm/ -H "Content-Type: application/json" -d '{}'`

## Rollback Procedure

If issues arise, rollback to Node.js version:

```bash
# Stop containers
docker-compose down

# Revert docker-compose.yaml changes (restore build.context to ./atm-locator)
git checkout docker-compose.yaml

# Rebuild and start
docker-compose up --build
```

## Notes

- The multi-stage Docker build reduces final image size by excluding build tools
- Health check uses the POST /api/atm/ endpoint which is the primary use case
- `depends_on: mongo` ensures MongoDB is started before the application
- The non-root user in the container improves security
- Environment variables are passed inline to match Docker Compose best practices
- The legacy Node.js `atm-locator/` directory is preserved for rollback capability
