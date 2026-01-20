# Deployment Tasks

## Overview

Containerize the Spring Boot application with Docker, create Docker Compose for local development, and establish CI/CD pipeline with automated testing and deployment to staging environment.

## Prerequisites

- Complete application implementation ([TASK-001] through [TASK-026])
- Application configuration finalized ([TASK-026])
- Comprehensive test coverage achieved ([TASK-018], [TASK-024], [TASK-025])

## Tasks

### [TASK-029] - [AI] Create Dockerfile with multi-stage build

**Why**: Multi-stage Docker build optimizes image size while including build and runtime requirements.

**What**:
- Create Dockerfile with two stages: build and runtime
- Build stage: Use maven:3.9-eclipse-temurin-17 base image for compilation
- Build stage: Copy pom.xml and download dependencies (layer caching)
- Build stage: Copy source code and run mvn clean package
- Runtime stage: Use eclipse-temurin:17-jre-alpine for minimal size
- Runtime stage: Copy JAR from build stage to /app/app.jar
- Copy atm_data.json seed file to /app/atm_data.json
- Expose port 8001 for application traffic
- Add HEALTHCHECK with wget to /actuator/health endpoint
- Set ENTRYPOINT to java -jar with JVM options from JAVA_OPTS env var
- Include ARG for version labeling from build pipeline

**Testing** (TDD - write tests first):
- Integration test: Docker build completes successfully
- Integration test: Built image size reasonable (<200MB for JRE-based)
- Integration test: Container starts and serves requests on port 8001
- Integration test: Health check passes after startup
- Integration test: Seed data file accessible within container
- Integration test: Environment variables override application.yml

**Dependencies**: [TASK-026] application configuration

---

### [TASK-030] - [AI] Create Docker Compose for local development

**Why**: Docker Compose provides one-command local environment setup with all dependencies.

**What**:
- Create docker-compose.yml with two services: app and mongo
- Configure app service: build from Dockerfile, ports 8001:8001, depends_on mongo
- Configure app environment variables: MONGODB_URI=mongodb://mongo:27017/atm-locator, LOAD_SEED_DATA=true
- Configure mongo service: use mongo:5.0 image, ports 27017:27017 for host access
- Add mongo volume for data persistence: mongo-data:/data/db
- Configure health checks for both services
- Add restart policies for reliability
- Configure networks for service discovery
- Document usage in README: docker-compose up -d, docker-compose logs, docker-compose down

**Testing** (TDD - write tests first):
- Integration test: docker-compose up starts both services successfully
- Integration test: Application connects to MongoDB on startup
- Integration test: Seed data loads automatically in fresh environment
- Integration test: API endpoints respond correctly through localhost:8001
- Integration test: MongoDB data persists across container restarts
- Integration test: docker-compose down stops all services cleanly

**Dependencies**: [TASK-029] Dockerfile

---

### [TASK-031] - [MANUAL] Set up CI/CD pipeline with test execution and coverage reporting

**Why**: Automated pipeline ensures code quality, test execution, and deployment consistency across environments.

**What**:
- Choose CI/CD platform (GitHub Actions, GitLab CI, or Jenkins)
- Create pipeline configuration file (.github/workflows, .gitlab-ci.yml, or Jenkinsfile)
- Configure build stage: checkout code, setup JDK 17, Maven cache
- Configure test stage: mvn clean test with JaCoCo coverage report
- Enforce coverage threshold: fail build if <80% coverage
- Configure build stage: mvn package to create executable JAR
- Configure Docker build stage: build and tag image with commit SHA
- Configure Docker push stage: push to container registry (Docker Hub, ECR, GCR)
- Configure deploy stage: deploy to staging environment (Kubernetes or ECS)
- Add manual approval gate before production deployment
- Configure notifications for build failures and deployment status
- Document pipeline stages and required secrets in README

**Dependencies**: [TASK-029] Dockerfile, [TASK-030] Docker Compose, all test tasks completed

---

### [TASK-032] - [MANUAL] Deploy to staging environment and validate backward compatibility

**Why**: Staging deployment validates production readiness and ensures backward compatibility with existing clients.

**What**:
- Deploy Docker container to staging Kubernetes cluster or ECS service
- Configure staging MongoDB Atlas cluster connection
- Set environment variables for staging: SPRING_PROFILES_ACTIVE=production, LOAD_SEED_DATA=false
- Configure load balancer with health check to /actuator/health
- Validate all three endpoints respond correctly with proper HTTP status codes
- Test MongoDB $oid format preserved in responses (compare with Node.js output)
- Run backward compatibility test suite against staging API
- Verify OpenAPI documentation accessible at /docs endpoint
- Test geospatial search with various coordinates and radii
- Validate error responses for invalid inputs match expected format
- Monitor application logs for errors or warnings
- Check JVM metrics and performance characteristics
- Obtain QA team sign-off on backward compatibility
- Document staging environment access and testing procedures

**Dependencies**: [TASK-031] CI/CD pipeline, all implementation and testing tasks completed
