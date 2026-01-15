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
