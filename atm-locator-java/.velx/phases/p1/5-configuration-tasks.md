# Configuration Tasks

## Overview

Configure MongoDB connection and data seeding as the final implementation step to complete the full stack.

## Tasks

### [P1-016] - [AI] Configure MongoDB connection with environment variables

Configure application.properties with MongoDB URI from DB_URL environment variable (default: localhost), server port from PORT variable, and actuator health endpoint exposure.

---

### [P1-017] - [AI] Implement DataSeederConfig with ApplicationRunner

Create DataSeederConfig that loads atm_data.json from classpath, clears existing ATM data, parses JSON to entities, and saves all ATMs to MongoDB on application startup.
