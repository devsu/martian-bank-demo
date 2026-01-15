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
