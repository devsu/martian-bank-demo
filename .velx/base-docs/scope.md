# Scope: Martian Bank ATM Locator Service Migration to Java

## Overview
This initiative involves migrating the atm-locator microservice from Node.js to Java while preserving all existing functionality. The service provides REST APIs for querying ATM locations with filtering capabilities (operational status, interplanetary capability), retrieving specific ATM details by ID, and adding new ATM records. The migration will leverage appropriate Java frameworks to improve code quality through static typing, enhanced error handling, and comprehensive API documentation.

The service manages ATM data stored in MongoDB, supporting both terrestrial and interplanetary banking use cases. Core functionality includes filtering by real-time availability (isOpenNow), interplanetary capability (isInterPlanetary), and returning randomized ATM lists for discovery purposes.

## High-Level Requirements
See `requirements.md` for detailed functional and non-functional requirements covering endpoint migration, framework selection, error handling improvements, type safety implementation, and API documentation generation.

## Boundaries
See `boundaries.md` for explicit in-scope and out-of-scope items, including what will and will not be migrated or modified during this initiative.

## Constraints
See `constraints.md` for technical, business, and operational constraints that must be respected throughout the migration, including database compatibility and API contract preservation.
