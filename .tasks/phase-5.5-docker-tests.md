# Phase 5.5: Docker Integration Tests

## Overview

Verify the Docker setup works correctly by testing the containerized application. This phase focuses on integration verification rather than traditional unit tests.

## Prerequisites

- Phase 5 completed
- Docker and Docker Compose installed
- Dockerfile builds successfully

## Deliverables

1. `verify-docker.sh` - Shell script to verify Docker setup
2. Manual verification checklist
3. API parity verification tests

## Implementation Steps

### Step 1: Create Verification Script

**File**: `atm-locator-java/scripts/verify-docker.sh`

```bash
#!/bin/bash
# Copyright (c) 2023 Cisco Systems, Inc. and its affiliates All rights reserved.
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file.

# Docker verification script for atm-locator-java
# Run from repository root: ./atm-locator-java/scripts/verify-docker.sh

set -e

echo "=== ATM Locator Java - Docker Verification ==="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Counters
PASSED=0
FAILED=0

# Helper functions
pass() {
    echo -e "${GREEN}✓ PASS${NC}: $1"
    ((PASSED++))
}

fail() {
    echo -e "${RED}✗ FAIL${NC}: $1"
    ((FAILED++))
}

warn() {
    echo -e "${YELLOW}⚠ WARN${NC}: $1"
}

# Test 1: Docker image builds
echo "--- Test 1: Docker Image Build ---"
if docker build -t atm-locator-java-test ./atm-locator-java > /dev/null 2>&1; then
    pass "Docker image builds successfully"
else
    fail "Docker image build failed"
fi
echo ""

# Test 2: Container starts
echo "--- Test 2: Container Startup ---"
docker-compose up -d mongo atm-locator > /dev/null 2>&1
sleep 15  # Wait for application startup

if docker-compose ps atm-locator | grep -q "Up"; then
    pass "Container is running"
else
    fail "Container failed to start"
fi
echo ""

# Test 3: Health check
echo "--- Test 3: Health Check ---"
HEALTH=$(docker inspect --format='{{.State.Health.Status}}' atm-locator 2>/dev/null || echo "none")
if [ "$HEALTH" == "healthy" ]; then
    pass "Health check passing"
elif [ "$HEALTH" == "starting" ]; then
    warn "Health check still starting"
else
    warn "Health check status: $HEALTH"
fi
echo ""

# Test 4: POST /api/atm/ endpoint
echo "--- Test 4: POST /api/atm/ Endpoint ---"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8001/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{}')

if [ "$RESPONSE" == "200" ]; then
    pass "POST /api/atm/ returns 200"
else
    fail "POST /api/atm/ returned $RESPONSE (expected 200)"
fi
echo ""

# Test 5: Response is JSON array with correct fields
echo "--- Test 5: Response Structure ---"
RESPONSE=$(curl -s -X POST http://localhost:8001/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{}')

if echo "$RESPONSE" | jq -e '.[0]._id' > /dev/null 2>&1; then
    pass "Response contains _id field"
else
    fail "Response missing _id field"
fi

if echo "$RESPONSE" | jq -e '.[0].name' > /dev/null 2>&1; then
    pass "Response contains name field"
else
    fail "Response missing name field"
fi

if echo "$RESPONSE" | jq -e '.[0].coordinates' > /dev/null 2>&1; then
    pass "Response contains coordinates field"
else
    fail "Response missing coordinates field"
fi

if echo "$RESPONSE" | jq -e '.[0].address' > /dev/null 2>&1; then
    pass "Response contains address field"
else
    fail "Response missing address field"
fi

if echo "$RESPONSE" | jq -e '.[0].isOpen' > /dev/null 2>&1; then
    pass "Response contains isOpen field"
else
    fail "Response missing isOpen field"
fi
echo ""

# Test 6: GET /api/atm/:id endpoint
echo "--- Test 6: GET /api/atm/:id Endpoint ---"
ATM_ID=$(curl -s -X POST http://localhost:8001/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{}' | jq -r '.[0]._id')

RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8001/api/atm/$ATM_ID")

if [ "$RESPONSE" == "200" ]; then
    pass "GET /api/atm/:id returns 200"
else
    fail "GET /api/atm/:id returned $RESPONSE (expected 200)"
fi
echo ""

# Test 7: Detail response structure
echo "--- Test 7: Detail Response Structure ---"
DETAIL=$(curl -s "http://localhost:8001/api/atm/$ATM_ID")

if echo "$DETAIL" | jq -e '.coordinates' > /dev/null 2>&1; then
    pass "Detail has coordinates"
else
    fail "Detail missing coordinates"
fi

if echo "$DETAIL" | jq -e '.timings' > /dev/null 2>&1; then
    pass "Detail has timings"
else
    fail "Detail missing timings"
fi

if echo "$DETAIL" | jq -e '.atmHours' > /dev/null 2>&1; then
    pass "Detail has atmHours"
else
    fail "Detail missing atmHours"
fi

if ! echo "$DETAIL" | jq -e '._id' > /dev/null 2>&1; then
    pass "Detail correctly excludes _id"
else
    fail "Detail should not have _id"
fi

if ! echo "$DETAIL" | jq -e '.name' > /dev/null 2>&1; then
    pass "Detail correctly excludes name"
else
    fail "Detail should not have name"
fi
echo ""

# Test 8: 404 handling
echo "--- Test 8: 404 Error Handling ---"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8001/api/atm/nonexistent-id")

if [ "$RESPONSE" == "404" ]; then
    pass "Invalid ID returns 404"
else
    fail "Invalid ID returned $RESPONSE (expected 404)"
fi

ERROR_BODY=$(curl -s "http://localhost:8001/api/atm/nonexistent-id")
if echo "$ERROR_BODY" | jq -e '.message' > /dev/null 2>&1; then
    pass "Error response has message field"
else
    fail "Error response missing message field"
fi
echo ""

# Test 9: Filter functionality
echo "--- Test 9: Filter Functionality ---"
# Test isOpenNow filter
OPEN_ATMS=$(curl -s -X POST http://localhost:8001/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{"isOpenNow": true}' | jq 'map(select(.isOpen == true)) | length')

ALL_OPEN=$(curl -s -X POST http://localhost:8001/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{"isOpenNow": true}' | jq 'map(select(.isOpen == false)) | length')

if [ "$ALL_OPEN" == "0" ]; then
    pass "isOpenNow filter returns only open ATMs"
else
    fail "isOpenNow filter returned closed ATMs"
fi
echo ""

# Test 10: Results limited to 4
echo "--- Test 10: Results Limited to 4 ---"
COUNT=$(curl -s -X POST http://localhost:8001/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{}' | jq 'length')

if [ "$COUNT" -le 4 ]; then
    pass "Results limited to max 4 (got $COUNT)"
else
    fail "Results exceeded limit (got $COUNT, expected <= 4)"
fi
echo ""

# Test 11: NGINX routing
echo "--- Test 11: NGINX Routing ---"
NGINX_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/atm/ \
    -H "Content-Type: application/json" \
    -d '{}' 2>/dev/null || echo "000")

if [ "$NGINX_RESPONSE" == "200" ]; then
    pass "NGINX routes to atm-locator correctly"
else
    warn "NGINX routing returned $NGINX_RESPONSE (may not be running)"
fi
echo ""

# Cleanup
echo "--- Cleanup ---"
docker-compose down > /dev/null 2>&1
echo "Containers stopped"
echo ""

# Summary
echo "=== Summary ==="
echo -e "Passed: ${GREEN}$PASSED${NC}"
echo -e "Failed: ${RED}$FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi
```

### Step 2: Make Script Executable

```bash
mkdir -p atm-locator-java/scripts
chmod +x atm-locator-java/scripts/verify-docker.sh
```

### Step 3: Create API Parity Test

**File**: `atm-locator-java/scripts/api-parity-test.sh`

```bash
#!/bin/bash
# API Parity Test - Compares Java and Node.js responses
# Requires both services to be running on different ports

set -e

echo "=== API Parity Test ==="
echo "Comparing Java (8001) vs Node.js (8002) responses"
echo ""

# Helper to compare JSON
compare_json() {
    local java_response="$1"
    local node_response="$2"
    local field="$3"

    java_val=$(echo "$java_response" | jq -r "$field" 2>/dev/null)
    node_val=$(echo "$node_response" | jq -r "$field" 2>/dev/null)

    if [ "$java_val" == "$node_val" ]; then
        echo "✓ $field matches"
        return 0
    else
        echo "✗ $field differs: Java='$java_val', Node='$node_val'"
        return 1
    fi
}

# Test POST /api/atm/ response structure
echo "--- Comparing POST /api/atm/ ---"
JAVA_LIST=$(curl -s -X POST http://localhost:8001/api/atm/ -H "Content-Type: application/json" -d '{}')
NODE_LIST=$(curl -s -X POST http://localhost:8002/api/atm/ -H "Content-Type: application/json" -d '{}')

# Compare array length (both should return <= 4)
JAVA_LEN=$(echo "$JAVA_LIST" | jq 'length')
NODE_LEN=$(echo "$NODE_LIST" | jq 'length')
echo "Java count: $JAVA_LEN, Node count: $NODE_LEN"

# Compare field presence in first item
echo "Field presence check:"
compare_json "$JAVA_LIST" "$NODE_LIST" '.[0] | has("_id")'
compare_json "$JAVA_LIST" "$NODE_LIST" '.[0] | has("name")'
compare_json "$JAVA_LIST" "$NODE_LIST" '.[0] | has("coordinates")'
compare_json "$JAVA_LIST" "$NODE_LIST" '.[0] | has("address")'
compare_json "$JAVA_LIST" "$NODE_LIST" '.[0] | has("isOpen")'

# Verify excluded fields
echo "Excluded fields check:"
JAVA_HAS_TIMINGS=$(echo "$JAVA_LIST" | jq '.[0] | has("timings")')
NODE_HAS_TIMINGS=$(echo "$NODE_LIST" | jq '.[0] | has("timings")')
if [ "$JAVA_HAS_TIMINGS" == "false" ] && [ "$NODE_HAS_TIMINGS" == "false" ]; then
    echo "✓ Both exclude 'timings' field"
else
    echo "✗ Field exclusion mismatch for 'timings'"
fi

echo ""
echo "--- Comparing GET /api/atm/:id ---"
# Get an ID from Java response
ATM_ID=$(echo "$JAVA_LIST" | jq -r '.[0]._id')
echo "Testing with ATM ID: $ATM_ID"

JAVA_DETAIL=$(curl -s "http://localhost:8001/api/atm/$ATM_ID")
NODE_DETAIL=$(curl -s "http://localhost:8002/api/atm/$ATM_ID")

echo "Field presence check:"
compare_json "$JAVA_DETAIL" "$NODE_DETAIL" 'has("coordinates")'
compare_json "$JAVA_DETAIL" "$NODE_DETAIL" 'has("timings")'
compare_json "$JAVA_DETAIL" "$NODE_DETAIL" 'has("atmHours")'
compare_json "$JAVA_DETAIL" "$NODE_DETAIL" 'has("numberOfATMs")'
compare_json "$JAVA_DETAIL" "$NODE_DETAIL" 'has("isOpen")'

echo "Excluded fields check:"
JAVA_HAS_ID=$(echo "$JAVA_DETAIL" | jq 'has("_id")')
NODE_HAS_ID=$(echo "$NODE_DETAIL" | jq 'has("_id")')
if [ "$JAVA_HAS_ID" == "false" ] && [ "$NODE_HAS_ID" == "false" ]; then
    echo "✓ Both exclude '_id' field"
else
    echo "✗ Field exclusion mismatch for '_id'"
fi

echo ""
echo "--- Comparing 404 Response ---"
JAVA_404=$(curl -s "http://localhost:8001/api/atm/invalid-id")
NODE_404=$(curl -s "http://localhost:8002/api/atm/invalid-id")

compare_json "$JAVA_404" "$NODE_404" 'has("message")'
compare_json "$JAVA_404" "$NODE_404" 'has("stack")'

echo ""
echo "=== Parity Test Complete ==="
```

## Manual Verification Checklist

### Pre-deployment Checks

- [ ] Docker image builds without errors
- [ ] Container starts and stays running
- [ ] Logs show successful MongoDB connection
- [ ] Logs show database seeding with 13 records
- [ ] No error messages in startup logs

### API Endpoint Checks

| Endpoint | Check | Expected |
|----------|-------|----------|
| POST /api/atm/ | Status code | 200 |
| POST /api/atm/ | Response type | JSON Array |
| POST /api/atm/ | Max items | 4 |
| POST /api/atm/ | Has _id field | Yes |
| POST /api/atm/ | Has name field | Yes |
| POST /api/atm/ | Has timings field | No |
| POST /api/atm/ with isOpenNow | All items open | Yes |
| GET /api/atm/{id} | Status code | 200 |
| GET /api/atm/{id} | Has coordinates | Yes |
| GET /api/atm/{id} | Has timings | Yes |
| GET /api/atm/{id} | Has _id | No |
| GET /api/atm/{invalid} | Status code | 404 |
| GET /api/atm/{invalid} | Has message | Yes |

### Integration Checks

- [ ] NGINX routes /api/atm to atm-locator correctly
- [ ] UI can display ATM list
- [ ] UI can display ATM details on click
- [ ] Filter checkboxes work (Open Now, Interplanetary)

### Performance Checks

- [ ] Container startup time < 30 seconds
- [ ] API response time < 500ms
- [ ] Memory usage reasonable (< 512MB)

## Success Criteria

### Automated Verification

- [ ] `./atm-locator-java/scripts/verify-docker.sh` passes all tests
- [ ] Docker health check shows "healthy"
- [ ] All API responses match expected structure

### Manual Verification

- [ ] Full stack runs with `docker-compose up --build`
- [ ] UI ATM Locator feature works end-to-end
- [ ] No errors in application logs

## Running the Tests

```bash
# From repository root
cd martian-bank-demo

# Run verification script
./atm-locator-java/scripts/verify-docker.sh

# Run API parity test (requires both versions running)
# Start Node.js on 8002, Java on 8001
./atm-locator-java/scripts/api-parity-test.sh
```

## Troubleshooting

### Container won't start
```bash
# Check logs
docker-compose logs atm-locator

# Common issues:
# - MongoDB not ready: increase sleep time or add healthcheck
# - Port conflict: check if 8001 is in use
```

### Database connection fails
```bash
# Verify MongoDB is running
docker-compose ps mongo

# Check environment variables
docker-compose exec atm-locator env | grep -E "(DATABASE|DB_URL)"
```

### Tests timeout
```bash
# Increase startup wait time in verify-docker.sh
sleep 30  # instead of sleep 15
```

## Notes

- The verification script requires `jq` for JSON parsing
- API parity test is optional but useful for validating migration
- Health check may take up to 30 seconds to become healthy after startup
- Memory settings can be adjusted via JAVA_OPTS environment variable if needed
