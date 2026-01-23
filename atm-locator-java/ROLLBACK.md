# ATM Locator Java Service - Rollback and Troubleshooting Guide

This document provides procedures for rolling back from the Java ATM Locator service to the original Node.js service, along with comprehensive troubleshooting guidelines.

## Table of Contents

- [Rollback Decision Criteria](#rollback-decision-criteria)
- [Quick Rollback Procedure](#quick-rollback-procedure)
- [Rollback Verification Steps](#rollback-verification-steps)
- [Troubleshooting Guide](#troubleshooting-guide)
- [Log Analysis Examples](#log-analysis-examples)
- [Escalation Procedures](#escalation-procedures)

---

## Rollback Decision Criteria

Use the following criteria to determine when a rollback to the Node.js service is necessary.

### Immediate Rollback Triggers

Initiate rollback immediately if any of these conditions occur:

| Condition | Threshold | Action |
|-----------|-----------|--------|
| Service completely unresponsive | > 2 minutes | Immediate rollback |
| Health check failures | > 5 consecutive failures | Immediate rollback |
| HTTP 5xx error rate | > 50% of requests | Immediate rollback |
| Data corruption detected | Any occurrence | Immediate rollback + investigation |
| Critical security vulnerability | Active exploitation | Immediate rollback + incident response |

### Warning-Level Indicators

Monitor these metrics and consider rollback if they persist:

| Metric | Warning Threshold | Critical Threshold | Observation Period |
|--------|-------------------|--------------------|--------------------|
| Error rate (4xx/5xx) | > 5% | > 20% | 5 minutes |
| Average response time | > 500ms | > 2000ms | 5 minutes |
| P95 response time | > 1000ms | > 5000ms | 5 minutes |
| Memory usage | > 80% | > 95% | 10 minutes |
| CPU usage | > 70% | > 90% | 10 minutes |
| MongoDB connection failures | > 3 per minute | > 10 per minute | 5 minutes |

### Health Check Failure Patterns

The Java service health endpoint (`/actuator/health`) should return status `UP`. Consider rollback if:

1. **Health status is DOWN** for more than 2 consecutive checks (30 seconds apart)
2. **MongoDB component is DOWN** and cannot reconnect within 1 minute
3. **Health endpoint is unreachable** for more than 1 minute

### Business Impact Assessment

Before rolling back, assess:

- [ ] Number of affected users
- [ ] Impact on dependent services (UI, NGINX routing)
- [ ] Data consistency state
- [ ] Time since last known good state

---

## Quick Rollback Procedure

This section provides step-by-step instructions to restore the Node.js ATM Locator service.

### Prerequisites

- Access to the Docker host or orchestration system
- Ability to modify NGINX configuration
- Access to the `martian-bank-demo` repository

### Rollback Steps

#### Step 1: Verify the Legacy Service is Running

The Node.js service should already be running as `atm-locator-legacy` on port 9091.

```bash
# Check if legacy service is running
docker ps | grep atm-locator-legacy

# Expected output:
# CONTAINER ID   IMAGE                              STATUS         PORTS
# xyz789...      martian-bank-atm-locator-legacy    Up X minutes   0.0.0.0:9091->9091/tcp
```

If not running, start it:

```bash
docker-compose up -d atm-locator-legacy
```

#### Step 2: Verify Legacy Service Health

```bash
# Test the legacy service endpoint
curl -s -X POST http://localhost:9091/api/atm \
  -H "Content-Type: application/json" \
  -d '{}' | head -c 500

# Check response is valid JSON array
curl -s -X POST http://localhost:9091/api/atm \
  -H "Content-Type: application/json" \
  -d '{}' | python3 -m json.tool > /dev/null && echo "Valid JSON response"
```

#### Step 3: Update NGINX Configuration

Modify the NGINX configuration to route traffic to the legacy service.

**Option A: Edit NGINX Configuration File**

Edit `/home/hector-brito/martian-bank-demo/nginx/default.conf`:

Change from:
```nginx
# Java ATM Locator (primary)
location /api/atm {
    proxy_pass http://atm-locator:8081/api/atm/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

To:
```nginx
# Node.js ATM Locator (rollback)
location /api/atm {
    proxy_pass http://atm-locator-legacy:9091/api/atm/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

**Option B: Use sed for Quick Edit**

```bash
# Backup current config
cp /home/hector-brito/martian-bank-demo/nginx/default.conf \
   /home/hector-brito/martian-bank-demo/nginx/default.conf.backup

# Replace routing (Java -> Node.js)
sed -i 's|proxy_pass http://atm-locator:8081/api/atm/|proxy_pass http://atm-locator-legacy:9091/api/atm/|g' \
    /home/hector-brito/martian-bank-demo/nginx/default.conf
```

#### Step 4: Reload NGINX

```bash
# Reload NGINX configuration without downtime
docker exec nginx nginx -s reload

# Verify NGINX is healthy
docker exec nginx nginx -t
```

#### Step 5: Stop the Java Service (Optional)

Optionally stop the Java service to free resources:

```bash
# Stop the Java service
docker-compose stop atm-locator

# Or stop and remove the container
docker-compose rm -sf atm-locator
```

#### Step 6: Verify Rollback Success

See [Rollback Verification Steps](#rollback-verification-steps) below.

### Emergency One-Liner Rollback

For critical situations, use this combined command:

```bash
# Emergency rollback command (run from martian-bank-demo directory)
cp /home/hector-brito/martian-bank-demo/nginx/default.conf /home/hector-brito/martian-bank-demo/nginx/default.conf.backup && \
sed -i 's|proxy_pass http://atm-locator:8081/api/atm/|proxy_pass http://atm-locator-legacy:9091/api/atm/|g' \
    /home/hector-brito/martian-bank-demo/nginx/default.conf && \
docker exec nginx nginx -s reload && \
echo "Rollback completed - verifying..." && \
sleep 2 && \
curl -s -X POST http://localhost:8080/api/atm -H "Content-Type: application/json" -d '{}' | head -c 100
```

---

## Rollback Verification Steps

After completing the rollback, verify success with these checks.

### 1. NGINX Routing Verification

```bash
# Test through NGINX (primary endpoint)
curl -s -X POST http://localhost:8080/api/atm \
  -H "Content-Type: application/json" \
  -d '{}' | python3 -m json.tool > /dev/null && echo "NGINX routing: OK" || echo "NGINX routing: FAILED"
```

### 2. Direct Service Verification

```bash
# Test Node.js service directly
curl -s -X POST http://localhost:9091/api/atm \
  -H "Content-Type: application/json" \
  -d '{}' | python3 -m json.tool > /dev/null && echo "Node.js service: OK" || echo "Node.js service: FAILED"
```

### 3. Response Time Check

```bash
# Check response time (should be < 500ms)
time curl -s -X POST http://localhost:8080/api/atm \
  -H "Content-Type: application/json" \
  -d '{}' > /dev/null
```

### 4. UI Functionality Test

1. Open browser to `http://localhost:8080`
2. Navigate to the ATM Locator feature
3. Verify ATM locations are displayed on the map
4. Test search functionality if available

### 5. Container Health Verification

```bash
# Verify all critical containers are running
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(nginx|atm-locator|mongo)"
```

Expected output:
```
NAMES                STATUS         PORTS
nginx                Up X minutes   0.0.0.0:8080->8080/tcp
atm-locator-legacy   Up X minutes   0.0.0.0:9091->9091/tcp
mongo                Up X minutes   27017/tcp
```

### 6. Log Verification

```bash
# Check NGINX logs for errors
docker logs --tail 20 nginx | grep -i error

# Check Node.js service logs
docker logs --tail 20 atm-locator-legacy | grep -i error
```

### 7. Comprehensive Health Check Script

```bash
#!/bin/bash
# Save as: verify_rollback.sh

echo "=== Rollback Verification ==="
echo ""

# Check containers
echo "1. Container Status:"
docker ps --format "{{.Names}}: {{.Status}}" | grep -E "(nginx|atm-locator|mongo)"
echo ""

# Check NGINX routing
echo "2. NGINX Routing Test:"
NGINX_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/atm -H "Content-Type: application/json" -d '{}')
if [ "$NGINX_RESPONSE" == "200" ]; then
    echo "   Status: OK (HTTP $NGINX_RESPONSE)"
else
    echo "   Status: FAILED (HTTP $NGINX_RESPONSE)"
fi
echo ""

# Check direct service
echo "3. Node.js Service Direct Test:"
NODEJS_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:9091/api/atm -H "Content-Type: application/json" -d '{}')
if [ "$NODEJS_RESPONSE" == "200" ]; then
    echo "   Status: OK (HTTP $NODEJS_RESPONSE)"
else
    echo "   Status: FAILED (HTTP $NODEJS_RESPONSE)"
fi
echo ""

# Check response time
echo "4. Response Time:"
RESPONSE_TIME=$(curl -s -o /dev/null -w "%{time_total}" -X POST http://localhost:8080/api/atm -H "Content-Type: application/json" -d '{}')
echo "   Total time: ${RESPONSE_TIME}s"
echo ""

echo "=== Verification Complete ==="
```

---

## Troubleshooting Guide

### Container Failures

#### Symptom: Container Won't Start

**Diagnosis:**
```bash
# Check container status
docker ps -a | grep atm-locator-java

# View container logs
docker logs atm-locator-java

# Check for port conflicts
lsof -i :8081
netstat -tuln | grep 8081
```

**Common Causes and Solutions:**

| Cause | Solution |
|-------|----------|
| Port already in use | Stop conflicting process or change PORT env var |
| Missing dependencies | Rebuild image: `docker-compose build atm-locator` |
| Out of memory | Increase Docker memory allocation or tune JVM settings |
| Image not found | Rebuild: `docker-compose build atm-locator` |

**Resolution Steps:**

```bash
# Stop any conflicting containers
docker stop $(docker ps -q --filter "publish=8081")

# Remove and recreate the container
docker-compose rm -sf atm-locator
docker-compose up -d atm-locator

# Check logs after restart
docker logs -f atm-locator-java
```

#### Symptom: Container Keeps Restarting

**Diagnosis:**
```bash
# Check restart count
docker inspect atm-locator-java --format='{{.RestartCount}}'

# View last 100 log lines
docker logs --tail 100 atm-locator-java

# Check exit code
docker inspect atm-locator-java --format='{{.State.ExitCode}}'
```

**Common Exit Codes:**

| Exit Code | Meaning | Likely Cause |
|-----------|---------|--------------|
| 0 | Normal exit | Application shutdown gracefully |
| 1 | General error | Application startup failure |
| 137 | SIGKILL | Out of memory (OOM killed) |
| 143 | SIGTERM | Container stopped by Docker |

**Resolution for OOM (Exit 137):**
```bash
# Check container memory limits
docker stats atm-locator-java --no-stream

# Increase memory in docker-compose.yaml:
# deploy:
#   resources:
#     limits:
#       memory: 1G
```

---

### Health Check Failures

#### Symptom: Health Endpoint Returns DOWN

**Diagnosis:**
```bash
# Check health status
curl -s http://localhost:8081/actuator/health | python3 -m json.tool

# Check specific components
curl -s http://localhost:8081/actuator/health/mongo
curl -s http://localhost:8081/actuator/health/ping
```

**Expected Healthy Response:**
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

**Common Issues:**

| Component | Status | Cause | Solution |
|-----------|--------|-------|----------|
| mongo | DOWN | MongoDB unreachable | Check MongoDB container and network |
| mongo | DOWN | Authentication failed | Verify DB_URL credentials |
| ping | DOWN | Application not fully started | Wait for startup to complete |

**Resolution Steps:**

```bash
# Verify MongoDB is running
docker ps | grep mongo

# Test MongoDB connection from Java container
docker exec atm-locator-java bash -c "curl -s mongo:27017 || echo 'Connection failed'"

# Restart Java service if MongoDB is healthy
docker-compose restart atm-locator
```

#### Symptom: Health Endpoint Unreachable

**Diagnosis:**
```bash
# Check if container is running
docker ps | grep atm-locator-java

# Check if port is listening inside container
docker exec atm-locator-java netstat -tuln | grep 8081

# Test from inside Docker network
docker exec nginx curl -s http://atm-locator:8081/actuator/health
```

**Resolution:**
```bash
# Check application logs for startup errors
docker logs atm-locator-java | grep -i "error\|exception\|failed"

# Force container restart
docker-compose restart atm-locator

# If still failing, rebuild
docker-compose build --no-cache atm-locator
docker-compose up -d atm-locator
```

---

### API Errors

#### Symptom: HTTP 404 Not Found

**Diagnosis:**
```bash
# Test various endpoints
curl -v http://localhost:8081/api/atm
curl -v http://localhost:8081/api/atm/
curl -v -X POST http://localhost:8081/api/atm -H "Content-Type: application/json" -d '{}'
```

**Common Causes:**

| URL Pattern | Issue | Solution |
|-------------|-------|----------|
| `/api/atm` (GET) | Should be POST | Use POST method with body |
| `/api/atm/invalid-id` | Invalid ObjectId format | Use valid MongoDB ObjectId |
| `/api/atm/add` | Missing request body | Include JSON body with required fields |

**Resolution:**
```bash
# Correct request format for listing ATMs
curl -X POST http://localhost:8081/api/atm \
  -H "Content-Type: application/json" \
  -d '{}'

# Correct request format for getting ATM by ID
curl http://localhost:8081/api/atm/507f1f77bcf86cd799439011
```

#### Symptom: HTTP 500 Internal Server Error

**Diagnosis:**
```bash
# Check application logs for stack traces
docker logs --tail 50 atm-locator-java | grep -A 20 "Exception\|Error"

# Check recent requests
docker logs atm-locator-java 2>&1 | grep "api/atm" | tail -20
```

**Common Causes and Solutions:**

| Error Pattern | Cause | Solution |
|---------------|-------|----------|
| `MongoTimeoutException` | Database connection timeout | Check MongoDB health, network connectivity |
| `NullPointerException` | Missing required data | Check request payload format |
| `JsonParseException` | Malformed JSON | Validate request body JSON |
| `BsonInvalidOperationException` | Invalid ObjectId | Use valid 24-character hex string |

**Resolution:**
```bash
# Verify request body format
curl -X POST http://localhost:8081/api/atm \
  -H "Content-Type: application/json" \
  -d '{"searchText": "bank"}' -v

# Check database connectivity
docker exec atm-locator-java ./gradlew test --tests "AtmServiceTest"
```

#### Symptom: HTTP 400 Bad Request

**Diagnosis:**
```bash
# Check request/response details
curl -v -X POST http://localhost:8081/api/atm/add \
  -H "Content-Type: application/json" \
  -d '{}' 2>&1
```

**Common Causes:**
- Missing required fields in request body
- Invalid data format (e.g., coordinates)
- Validation constraints violated

**Resolution:**
Check API documentation at `http://localhost:8081/docs` for required request format.

---

### Performance Issues

#### Symptom: Slow Response Times

**Diagnosis:**
```bash
# Measure response time
time curl -s -X POST http://localhost:8081/api/atm \
  -H "Content-Type: application/json" -d '{}' > /dev/null

# Check resource usage
docker stats atm-locator-java --no-stream

# Check JVM metrics
curl -s http://localhost:8081/actuator/metrics/jvm.memory.used
curl -s http://localhost:8081/actuator/metrics/jvm.gc.pause
```

**Performance Metrics to Check:**

| Metric | Endpoint | Healthy Range |
|--------|----------|---------------|
| JVM Heap | `/actuator/metrics/jvm.memory.used` | < 75% of max |
| GC Pause | `/actuator/metrics/jvm.gc.pause` | < 200ms average |
| HTTP Requests | `/actuator/metrics/http.server.requests` | < 500ms average |
| Thread Count | `/actuator/metrics/jvm.threads.live` | < 200 |

**Resolution Steps:**

```bash
# 1. Check for memory issues
curl -s http://localhost:8081/actuator/metrics/jvm.memory.max | python3 -m json.tool
curl -s http://localhost:8081/actuator/metrics/jvm.memory.used | python3 -m json.tool

# 2. Check MongoDB performance
docker logs mongo --tail 50 | grep -i slow

# 3. Restart service to clear any accumulated issues
docker-compose restart atm-locator

# 4. If persistent, consider JVM tuning
# Edit docker-compose.yaml to add JAVA_OPTS:
# environment:
#   JAVA_OPTS: "-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
```

#### Symptom: High Memory Usage

**Diagnosis:**
```bash
# Check container memory
docker stats atm-locator-java --no-stream --format "{{.MemUsage}}"

# Check JVM heap
curl -s http://localhost:8081/actuator/metrics/jvm.memory.used?tag=area:heap
```

**Resolution:**
```bash
# Trigger garbage collection (if endpoint enabled)
curl -X POST http://localhost:8081/actuator/gc

# Restart container to clear memory
docker-compose restart atm-locator

# Add memory limits in docker-compose.yaml
# deploy:
#   resources:
#     limits:
#       memory: 512M
```

#### Symptom: Connection Pool Exhaustion

**Diagnosis:**
```bash
# Check MongoDB connection pool
curl -s http://localhost:8081/actuator/metrics/mongodb.driver.pool.size
curl -s http://localhost:8081/actuator/metrics/mongodb.driver.pool.checkedout
```

**Resolution:**
Configure connection pool in `application.properties`:
```properties
spring.data.mongodb.min-connections=5
spring.data.mongodb.max-connections=50
```

---

## Log Analysis Examples

### Understanding Log Patterns

#### Successful Startup Log

```
2024-01-15 10:00:00.123 INFO  --- Starting AtmLocatorApplication v1.0.0
2024-01-15 10:00:02.456 INFO  --- Connecting to MongoDB at mongo:27017
2024-01-15 10:00:02.789 INFO  --- MongoDB connection established
2024-01-15 10:00:03.012 INFO  --- Tomcat started on port(s): 8081
2024-01-15 10:00:03.234 INFO  --- Started AtmLocatorApplication in 3.5 seconds
```

#### Failed Startup - MongoDB Connection

```
2024-01-15 10:00:00.123 INFO  --- Starting AtmLocatorApplication v1.0.0
2024-01-15 10:00:02.456 ERROR --- Failed to connect to MongoDB
com.mongodb.MongoTimeoutException: Timed out after 30000 ms while waiting
    at com.mongodb.internal.connection.DefaultServerMonitor...
```

**Action:** Check MongoDB container status and network connectivity.

#### Failed Startup - Port Already in Use

```
2024-01-15 10:00:00.123 INFO  --- Starting AtmLocatorApplication v1.0.0
2024-01-15 10:00:01.456 ERROR --- Web server failed to start
org.springframework.boot.web.server.PortInUseException: Port 8081 is already in use
```

**Action:** Stop conflicting process or change PORT environment variable.

#### Runtime Error - Invalid ObjectId

```
2024-01-15 10:05:00.123 WARN  --- Request to /api/atm/invalid-id
2024-01-15 10:05:00.124 ERROR --- Invalid ObjectId format: invalid-id
com.martianbank.atmlocator.exception.InvalidObjectIdException: Invalid ObjectId format
```

**Action:** Client is sending malformed ATM ID. Validate input on client side.

#### Runtime Error - Database Operation Failed

```
2024-01-15 10:10:00.123 ERROR --- Failed to execute MongoDB query
org.springframework.data.mongodb.UncategorizedMongoDbException: Command failed
Caused by: com.mongodb.MongoCommandException: Command failed with error 13
```

**Action:** Check database authentication and permissions.

### Log Filtering Commands

```bash
# View only errors
docker logs atm-locator-java 2>&1 | grep -i error

# View startup sequence
docker logs atm-locator-java 2>&1 | head -50

# View recent activity
docker logs --tail 100 --since 5m atm-locator-java

# Follow logs in real-time
docker logs -f atm-locator-java

# Filter by specific exception
docker logs atm-locator-java 2>&1 | grep -A 10 "MongoTimeoutException"

# Count error occurrences
docker logs atm-locator-java 2>&1 | grep -c "ERROR"
```

### Key Log Patterns to Monitor

| Pattern | Meaning | Severity |
|---------|---------|----------|
| `Started AtmLocatorApplication` | Successful startup | INFO |
| `MongoDB connection established` | Database connected | INFO |
| `MongoTimeoutException` | Database unreachable | CRITICAL |
| `PortInUseException` | Port conflict | CRITICAL |
| `OutOfMemoryError` | JVM memory exhausted | CRITICAL |
| `InvalidObjectIdException` | Bad client request | WARN |
| `Connection refused` | Network issue | ERROR |

---

## Escalation Procedures

### Escalation Levels

| Level | Trigger | Response Time | Actions |
|-------|---------|---------------|---------|
| L1 | Service degradation | 15 minutes | Monitor, attempt self-healing |
| L2 | Service unavailable | 5 minutes | Rollback, notify team |
| L3 | Data corruption | Immediate | Rollback, incident response |

### When to Escalate

**Escalate to L2 (On-Call Engineer) if:**

- Service is down for more than 5 minutes
- Rollback procedure fails
- Multiple dependent services affected
- Error rate > 50% for more than 2 minutes

**Escalate to L3 (Incident Response) if:**

- Data corruption suspected
- Security breach detected
- Multiple services failing simultaneously
- Rollback unsuccessful after 2 attempts

### Escalation Contacts

| Role | Responsibility | Contact Method |
|------|----------------|----------------|
| On-Call Engineer | First responder | PagerDuty / Slack #incidents |
| Team Lead | Decision authority | Slack DM / Phone |
| Database Admin | MongoDB issues | Slack #database |
| Infrastructure | Docker/Network issues | Slack #infrastructure |

### Escalation Template

When escalating, include:

```
INCIDENT REPORT
===============
Time Detected: [ISO timestamp]
Service: ATM Locator Java (atm-locator-java)
Severity: [L1/L2/L3]

SYMPTOMS:
- [List observed symptoms]
- [Include error messages]

IMPACT:
- [Affected users/services]
- [Business impact]

ACTIONS TAKEN:
1. [Action 1 and result]
2. [Action 2 and result]

CURRENT STATUS:
- [Service state]
- [Rollback status]

LOGS/EVIDENCE:
- [Relevant log snippets]
- [Metrics screenshots]
```

### Post-Incident Actions

After resolving an incident:

1. **Document the incident** in the team wiki
2. **Update runbooks** with new learnings
3. **Schedule post-mortem** within 48 hours
4. **Create tickets** for preventive measures
5. **Update monitoring** to catch similar issues earlier

---

## Appendix

### Quick Reference Commands

```bash
# Service Status
docker ps | grep atm-locator

# Health Check (Java)
curl -s http://localhost:8081/actuator/health | python3 -m json.tool

# Health Check (Node.js Legacy)
curl -s http://localhost:9091/api/atm -X POST -H "Content-Type: application/json" -d '{}'

# View Logs
docker logs -f atm-locator-java
docker logs -f atm-locator-legacy

# Restart Services
docker-compose restart atm-locator
docker-compose restart atm-locator-legacy
docker-compose restart nginx

# Emergency Rollback
sed -i 's|proxy_pass http://atm-locator:8081/api/atm/|proxy_pass http://atm-locator-legacy:9091/api/atm/|g' \
    /home/hector-brito/martian-bank-demo/nginx/default.conf && \
docker exec nginx nginx -s reload
```

### Service Comparison Matrix

| Feature | Java Service | Node.js Service |
|---------|--------------|-----------------|
| Container Name | atm-locator-java | atm-locator-legacy |
| Internal Port | 8081 | 9091 |
| External Port | 8081 | 9091 |
| Health Endpoint | /actuator/health | N/A |
| Metrics | /actuator/metrics | N/A |
| API Docs | /docs | /api-docs |
| Hostname | atm-locator | atm-locator-legacy |

### Related Documentation

- [DEPLOYMENT.md](./DEPLOYMENT.md) - Deployment procedures
- [README.md](./README.md) - Service overview
- [Project CLAUDE.md](/home/hector-brito/martian-bank-demo/CLAUDE.md) - Architecture overview
