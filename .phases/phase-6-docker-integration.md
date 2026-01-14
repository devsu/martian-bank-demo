# Phase 6: Docker and Docker Compose Integration

## Overview
Create Dockerfile for the Java service and update docker-compose.yaml to include the new Java-based loan service.

## Changes Required:

### 1. Multi-stage Dockerfile
**File**: `loan-java/Dockerfile`

```dockerfile
# Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file.

# Build stage
FROM --platform=linux/amd64 eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Copy source code
COPY src ./src

# Build the application
RUN chmod +x gradlew && ./gradlew build -Dquarkus.package.jar.type=uber-jar -x test

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:25-jre

WORKDIR /app

# Copy the built jar
COPY --from=build /app/build/*-runner.jar /app/loan-service.jar

# Expose port (matches Python service port)
EXPOSE 50053

# Set environment variables
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/loan-service.jar"]
```

### 2. Gradle Wrapper Files
Create Gradle wrapper (run in loan-java directory):
```bash
cd loan-java && gradle wrapper --gradle-version 8.12
```

This will create:
- `loan-java/gradlew`
- `loan-java/gradlew.bat`
- `loan-java/gradle/wrapper/gradle-wrapper.jar`
- `loan-java/gradle/wrapper/gradle-wrapper.properties`

### 3. Update Docker Compose
**File**: `docker-compose.yaml`

Add new service definition after the existing `loan` service (around line 115):

```yaml
  loan-java:
    build:
      context: ./loan-java
      dockerfile: Dockerfile
    container_name: loan-java
    hostname: loan
    image: martian-bank-loan-java
    restart: always
    environment:
      DB_URL: mongodb://root:example@mongo:27017
      SERVICE_PROTOCOL: http
    networks:
      - bankapp-network
    profiles:
      - java
```

**Note**: The `hostname: loan` ensures the Java service responds to the same DNS name, and the `profiles: java` allows selective activation.

### 4. Alternative Docker Compose for Java Migration
**File**: `docker-compose.java.yaml` (override file)

```yaml
# Use with: docker-compose -f docker-compose.yaml -f docker-compose.java.yaml up
services:
  loan:
    # Disable Python loan service
    profiles:
      - disabled

  loan-java:
    build:
      context: ./loan-java
      dockerfile: Dockerfile
    container_name: loan
    hostname: loan
    image: martian-bank-loan-java
    restart: always
    environment:
      DB_URL: mongodb://root:example@mongo:27017
      SERVICE_PROTOCOL: http
    networks:
      - bankapp-network
```

## Success Criteria:

### Automated Verification:
- [x] Docker build succeeds: `docker build -t martian-bank-loan-java ./loan-java`
- [x] Container starts: `docker run -e DB_URL=mongodb://root:example@localhost:27017 -p 50053:50053 martian-bank-loan-java`
- [x] Full stack starts with Java loan: `docker-compose -f docker-compose.yaml -f docker-compose.java.yaml up --build`

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 7 for final manual verification.
