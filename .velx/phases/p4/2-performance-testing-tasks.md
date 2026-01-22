# Performance Testing Tasks

## Overview

Create Locust performance tests for the Java ATM Locator service, mirroring the existing Node.js test coverage.

## Tasks

### [P4-003] - [AI] Create Locust performance test for Java ATM Locator service

Create `atm_locust_java.py` mirroring `atm_locust.py` structure. Add `VITE_ATM_JAVA_URL` to `api_urls.py` (default: localhost:8001). Add test execution to `locust.sh`. Test scenarios: POST / (get all ATMs), GET /:id (get details for each ATM).
