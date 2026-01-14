# Testing Strategy

## Framework and Tools
| Tool | Purpose |
|------|---------|
| JUnit 5 | Test framework (via `quarkus-junit5`) |
| Mockito | Mocking MongoDB clients and external dependencies |
| REST Assured | HTTP endpoint testing |
| JaCoCo | Code coverage measurement and reporting |

## Coverage Requirements
- **Minimum Line Coverage**: 90%
- **Enforcement**: JaCoCo Gradle plugin with `violationRules` to fail build if coverage drops below threshold
- **Reporting**: HTML and XML reports generated in `build/reports/jacoco/`

## Testing Approach
1. **Unit Tests Only**: All tests use mocks for external dependencies (MongoDB, etc.)
2. **No Integration Tests**: Tests do not connect to real databases or external services
3. **Phase-Based Testing**: Each coding phase is followed by a dedicated testing phase
4. **Incremental Coverage**: Each testing phase must achieve 90% coverage for the code introduced in its corresponding coding phase

## Test Directory Structure
```
loan-java/src/test/java/com/martianbank/loan/
├── model/           # DTO/Document model tests
├── repository/      # Repository tests with mocked MongoClient
├── service/         # Business logic tests with mocked repositories
├── resource/        # REST endpoint tests
└── grpc/            # gRPC service tests
```

## Running Tests
```bash
# Run all tests with coverage
cd loan-java && ./gradlew test jacocoTestReport

# Check coverage threshold (fails if below 90%)
cd loan-java && ./gradlew jacocoTestCoverageVerification

# View coverage report
open loan-java/build/reports/jacoco/test/html/index.html
```
