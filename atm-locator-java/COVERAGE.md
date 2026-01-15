# ATM Locator Java - Test Coverage Report

## Summary

| Metric | Target | Actual | Status |
|--------|--------|--------|--------|
| Line Coverage | 90% | 94.4% | PASS |
| Instruction Coverage | 90% | 95.3% | PASS |
| Branch Coverage | N/A | 72.7% | N/A |
| Method Coverage | N/A | 94.3% | N/A |

## Coverage by Package

| Package | Classes | Covered/Total Instructions | Coverage |
|---------|---------|---------------------------|----------|
| controller | 1 | 55/55 | 100% |
| service | 1 | 260/260 | 100% |
| dto | 3 | 64/64 | 100% |
| exception | 2 | 174/174 | 100% |
| config | 5 | 420/463 | 90.7% |
| atmlocator | 1 | 3/8 | 37.5% |
| model | 4 | - | Lombok generated |
| repository | 1 | - | Interface only |

**Note**: The `atmlocator` package contains only the `AtmLocatorApplication.main()` method which is excluded from standard unit testing. The `model` classes use Lombok-generated code. The `repository` package contains only a Spring Data interface.

## Test Summary

| Test Class | Tests | Passed | Failed |
|------------|-------|--------|--------|
| AtmLocatorApplicationTest | 2 | 2 | 0 |
| CorsConfigTest | 5 | 5 | 0 |
| DataSeederTest | 12 | 12 | 0 |
| MongoConfigTest | 7 | 7 | 0 |
| OpenApiConfigTest | 6 | 6 | 0 |
| AtmControllerTest (all nested) | 19 | 19 | 0 |
| AtmCreateRequestTest | 4 | 4 | 0 |
| AtmDetailResponseTest | 3 | 3 | 0 |
| AtmFilterRequestTest | 3 | 3 | 0 |
| AtmListResponseTest | 3 | 3 | 0 |
| ErrorResponseTest | 6 | 6 | 0 |
| AtmNotFoundExceptionTest | 3 | 3 | 0 |
| GlobalExceptionHandlerTest | 12 | 12 | 0 |
| AddressTest | 4 | 4 | 0 |
| AtmTest | 5 | 5 | 0 |
| CoordinatesTest | 4 | 4 | 0 |
| TimingsTest | 4 | 4 | 0 |
| AtmRepositoryTest | 3 | 3 | 0 |
| AtmServiceTest (all nested) | 22 | 22 | 0 |

**Total Tests: 127**
**Test Success Rate: 100%**
**Test Execution Time: ~3.6 seconds**

## Excluded from Coverage

The following are excluded from coverage calculation:
- `AtmLocatorApplication.main()` - Main class bootstrap method
- Lombok-generated code in model classes (getters/setters/constructors)
- Spring Data repository interface methods

## How to Run

```bash
# Run all tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage meets threshold
./gradlew jacocoTestCoverageVerification

# View HTML report
open build/reports/jacoco/test/html/index.html
```

## Report Location

- HTML Report: `build/reports/jacoco/test/html/index.html`
- XML Report: `build/reports/jacoco/test/jacocoTestReport.xml`
- Test Results: `build/reports/tests/test/index.html`

## Coverage Verification

The project is configured with JaCoCo coverage verification requiring a minimum of 90% instruction coverage. The build will fail if this threshold is not met:

```bash
./gradlew jacocoTestCoverageVerification
```

Current status: **PASSING** (95.3% instruction coverage)

## Test Categories

### Unit Tests
- **Service Layer**: 22 tests covering all business logic scenarios
- **Controller Layer**: 19 tests covering all endpoints and HTTP behaviors
- **Exception Handling**: 12 tests covering error response formats
- **Configuration**: 30 tests covering MongoDB, CORS, OpenAPI, and data seeding
- **DTOs**: 19 tests covering request/response object behavior
- **Models**: 17 tests covering entity objects

### Coverage Highlights
- All 3 REST endpoints are fully tested
- All error response scenarios are covered
- Data seeding logic is thoroughly tested
- Query building and filtering logic is 100% covered
