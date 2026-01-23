# ATM Locator Java Service - Deployment Guide

This document provides comprehensive deployment instructions for the ATM Locator Java service, a Spring Boot microservice that is part of the Martian Bank demo application.

## Table of Contents

- [Pre-deployment Checklist](#pre-deployment-checklist)
- [Build Instructions](#build-instructions)
- [Environment Variables Reference](#environment-variables-reference)
- [Network Configuration](#network-configuration)
- [Post-deployment Validation](#post-deployment-validation)
- [Monitoring Guidelines](#monitoring-guidelines)
- [Development Workflow](#development-workflow)

---

## Pre-deployment Checklist

Before deploying the ATM Locator Java service, verify the following items:

### Infrastructure Requirements

- [ ] Docker and Docker Compose are installed and running
- [ ] Sufficient disk space available (minimum 2GB recommended)
- [ ] Network ports 8081 and 8080 are available
- [ ] MongoDB instance is accessible

### Configuration Requirements

- [ ] MongoDB connection string is configured correctly
- [ ] Environment variables are set (see [Environment Variables Reference](#environment-variables-reference))
- [ ] Docker network `bankapp-network` exists or will be created

### Dependencies

- [ ] MongoDB service is running and healthy
- [ ] NGINX service is configured with proper routing rules
- [ ] All dependent services are in the Docker network

### Code Verification

- [ ] All unit tests pass
- [ ] Application builds successfully without errors
- [ ] No security vulnerabilities in dependencies

---

## Build Instructions

All build commands must be executed through the Docker container since build tools (Gradle, JDK) are not installed on the host machine.

### Building the Application

#### Full Build (Clean + Compile + Package)

```bash
docker exec atm-locator-java ./gradlew clean build
```

This command:
- Cleans previous build artifacts
- Compiles the Java source code
- Runs all unit tests
- Packages the application into a JAR file

#### Build Without Tests

```bash
docker exec atm-locator-java ./gradlew clean build -x test
```

Use this for faster builds when tests have already been verified.

#### Running Tests Only

```bash
docker exec atm-locator-java ./gradlew test
```

#### Checking Dependencies

```bash
docker exec atm-locator-java ./gradlew dependencies
```

### Docker Compose Build

To build the service as part of the full application stack:

```bash
# From the martian-bank-demo root directory
docker-compose build atm-locator

# Or rebuild all services
docker-compose build
```

### Starting the Service

```bash
# Start all services
docker-compose up -d

# Start only the ATM locator and its dependencies
docker-compose up -d atm-locator mongo
```

---

## Environment Variables Reference

The ATM Locator Java service uses the following environment variables:

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_URL` | Yes | `mongodb://root:example@localhost:27017/test?authSource=admin` | MongoDB connection URI |
| `PORT` | No | `8081` | HTTP server port |
| `JAVA_OPTS` | No | (see below) | JVM configuration options |

### DB_URL

The MongoDB connection string. Must include:
- Authentication credentials (username:password)
- Host and port
- Database name
- Authentication source

**Example:**
```
mongodb://root:example@mongo:27017/test?authSource=admin
```

### PORT

The HTTP port on which the service listens. Default is `8081`.

### JAVA_OPTS

JVM options for container optimization. Default configuration:
```
-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom
```

### Docker Compose Configuration

The `docker-compose.yaml` configures these variables as follows:

```yaml
atm-locator:
  environment:
    DB_URL: mongodb://root:example@mongo:27017/test?authSource=admin
    PORT: 8081
```

---

## Network Configuration

### Port Mappings

| Service | Internal Port | External Port | Description |
|---------|---------------|---------------|-------------|
| ATM Locator Java | 8081 | 8081 | Direct service access |
| NGINX | 8080 | 8080 | Reverse proxy access |

### Service Endpoints

#### Direct Access (Development)

| Endpoint | Method | URL |
|----------|--------|-----|
| List ATMs | POST | `http://localhost:8081/api/atm` |
| Get ATM by ID | GET | `http://localhost:8081/api/atm/{id}` |
| Create ATM | POST | `http://localhost:8081/api/atm/add` |
| Health Check | GET | `http://localhost:8081/actuator/health` |
| Metrics | GET | `http://localhost:8081/actuator/metrics` |
| API Documentation | GET | `http://localhost:8081/docs` |
| OpenAPI Spec | GET | `http://localhost:8081/docs.json` |

#### Through NGINX (Production)

| Endpoint | Method | URL |
|----------|--------|-----|
| List ATMs | POST | `http://localhost:8080/api/atm` |
| Get ATM by ID | GET | `http://localhost:8080/api/atm/{id}` |
| Create ATM | POST | `http://localhost:8080/api/atm/add` |

### NGINX Routing

The NGINX reverse proxy routes ATM requests as follows:

```
/api/atm/* --> http://atm-locator:8081/api/atm/
```

Configuration in `/nginx/default.conf`:
```nginx
location /api/atm {
    proxy_pass http://atm-locator:8081/api/atm/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

### Docker Network

All services communicate through the `bankapp-network` bridge network:

```yaml
networks:
  bankapp-network:
    driver: bridge
```

### Service Dependencies

The ATM Locator service depends on:
- **mongo**: MongoDB database for ATM data storage

```yaml
depends_on:
  - mongo
```

---

## Post-deployment Validation

After deploying the service, perform the following validation steps:

### 1. Verify Container Status

```bash
docker ps | grep atm-locator-java
```

Expected output shows the container running with status "Up":
```
CONTAINER ID   IMAGE                           STATUS         PORTS
abc123...      martian-bank-atm-locator-java   Up X minutes   0.0.0.0:8081->8081/tcp
```

### 2. Check Container Logs

```bash
docker logs atm-locator-java
```

Look for:
- "Started AtmLocatorApplication" message
- No ERROR or FATAL log entries
- Successful MongoDB connection

### 3. Health Check

```bash
curl -s http://localhost:8081/actuator/health | jq
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "mongo": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### 4. API Endpoint Validation

Test the ATM list endpoint:
```bash
curl -X POST http://localhost:8081/api/atm \
  -H "Content-Type: application/json" \
  -d '{}'
```

Expected: JSON array of ATM objects (or empty array if no data seeded)

### 5. NGINX Routing Validation

Test access through the reverse proxy:
```bash
curl -X POST http://localhost:8080/api/atm \
  -H "Content-Type: application/json" \
  -d '{}'
```

### 6. Swagger UI Access

Open in browser: `http://localhost:8081/docs`

Verify the Swagger UI loads and displays all available endpoints.

---

## Monitoring Guidelines

### Health Endpoints

The service exposes Spring Boot Actuator endpoints for monitoring:

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Overall service health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Performance metrics |

### Health Check Configuration

The actuator is configured to show detailed health information:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.health.mongo.enabled=true
```

### Key Metrics to Monitor

Access metrics at `http://localhost:8081/actuator/metrics`:

- `http.server.requests` - HTTP request statistics
- `jvm.memory.used` - JVM memory usage
- `jvm.gc.pause` - Garbage collection pauses
- `mongodb.driver.pool.size` - MongoDB connection pool
- `process.cpu.usage` - CPU utilization

### Example Metrics Query

```bash
# HTTP request metrics
curl http://localhost:8081/actuator/metrics/http.server.requests

# JVM memory usage
curl http://localhost:8081/actuator/metrics/jvm.memory.used
```

### Log Monitoring

View real-time logs:
```bash
docker logs -f atm-locator-java
```

Log levels are configured in `application.properties`:
```properties
logging.level.root=INFO
logging.level.com.martianbank.atmlocator=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
```

### Docker Health Check

The container can be configured with a health check:
```bash
docker inspect --format='{{.State.Health.Status}}' atm-locator-java
```

### Alerting Recommendations

Consider setting up alerts for:
- Health status changes (UP -> DOWN)
- Response time exceeding thresholds
- Error rate increases
- Memory usage approaching limits
- MongoDB connection failures

---

## Development Workflow

### Local Development Setup

1. **Start Required Services**

   Start MongoDB and NGINX:
   ```bash
   docker-compose up -d mongo nginx
   ```

2. **Build and Start ATM Locator**

   ```bash
   docker-compose up -d atm-locator
   ```

3. **View Logs**

   ```bash
   docker logs -f atm-locator-java
   ```

### Making Code Changes

The source code is mounted as a volume for hot development:
```yaml
volumes:
  - ./atm-locator-java/src:/app/src
```

After making changes:

1. **Rebuild the Application**
   ```bash
   docker exec atm-locator-java ./gradlew clean build -x test
   ```

2. **Restart the Container**
   ```bash
   docker-compose restart atm-locator
   ```

### Running Tests

#### All Tests
```bash
docker exec atm-locator-java ./gradlew test
```

#### Specific Test Class
```bash
docker exec atm-locator-java ./gradlew test --tests "AtmControllerTest"
```

#### With Test Reports
After running tests, reports are available at:
- HTML: `build/reports/tests/test/index.html`
- XML: `build/test-results/test/`

### Code Quality Checks

#### Check Dependencies
```bash
docker exec atm-locator-java ./gradlew dependencies
```

#### Dependency Updates
```bash
docker exec atm-locator-java ./gradlew dependencyUpdates
```

### Common Development Tasks

| Task | Command |
|------|---------|
| Clean build | `docker exec atm-locator-java ./gradlew clean build` |
| Run tests | `docker exec atm-locator-java ./gradlew test` |
| Build without tests | `docker exec atm-locator-java ./gradlew build -x test` |
| Check dependencies | `docker exec atm-locator-java ./gradlew dependencies` |
| View task list | `docker exec atm-locator-java ./gradlew tasks` |

### Debugging

#### Enable Debug Logging

Temporarily enable debug logging by setting environment variable:
```bash
docker exec atm-locator-java env | grep -i log
```

Or modify `application.properties` and restart.

#### Access Container Shell

```bash
docker exec -it atm-locator-java /bin/bash
```

#### Check Application Status

```bash
docker exec atm-locator-java ps aux | grep java
```

### Stopping Development Environment

```bash
# Stop ATM Locator only
docker-compose stop atm-locator

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

---

## Troubleshooting

### Common Issues

#### Service Won't Start

1. Check logs: `docker logs atm-locator-java`
2. Verify MongoDB is running: `docker ps | grep mongo`
3. Check port availability: `lsof -i :8081`

#### MongoDB Connection Failed

1. Verify MongoDB is running
2. Check connection string format
3. Ensure services are on same Docker network

#### Build Failures

1. Clean and rebuild: `docker exec atm-locator-java ./gradlew clean build`
2. Check Gradle cache: `docker exec atm-locator-java ls -la .gradle`
3. Verify source files are mounted correctly

#### NGINX Routing Issues

1. Check NGINX logs: `docker logs nginx`
2. Verify hostname resolution: Service must be named `atm-locator`
3. Test direct access first: `curl http://localhost:8081/actuator/health`

---

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Martian Bank Project README](/home/hector-brito/martian-bank-demo/README.md)
