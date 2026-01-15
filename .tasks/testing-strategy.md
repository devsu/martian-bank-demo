# Testing Strategy: ATM-Locator Java Migration

## Overview

This document outlines the testing strategy for the ATM-Locator migration to Java 25 with Spring Boot. The focus is on **unit testing with mocked external dependencies** to achieve 90% code coverage.

## Testing Philosophy

- **Unit tests over integration tests**: All external dependencies (MongoDB) are mocked
- **Fast feedback**: Tests run quickly without external services
- **Isolation**: Each test is independent and repeatable
- **Coverage target**: 90% line coverage minimum

## Technology Stack

| Tool | Purpose | Version |
|------|---------|---------|
| JUnit 5 | Test framework | 5.10.x |
| Mockito | Mocking framework | 5.x |
| MockMvc | Controller testing | Spring Boot Test |
| JaCoCo | Code coverage | 0.8.x |
| AssertJ | Fluent assertions | 3.x |

## Test Categories

### 1. Unit Tests (Primary Focus)

#### 1.1 Service Layer Tests (`AtmServiceTest.java`)

**Purpose**: Test business logic with mocked repository

**What to test**:
- `getATMs()` - Query building logic
  - Default query returns `interPlanetary: false`
  - `isOpenNow=true` adds `isOpen: true` to query
  - `isInterPlanetary=true` sets `interPlanetary: true`
  - Results are shuffled and limited to 4
  - Empty results return 404
- `addATM()` - Create logic
  - Valid input creates and returns ATM
  - Invalid input throws appropriate exception
- `getSpecificATM()` - Find by ID logic
  - Valid ID returns ATM details
  - Invalid/missing ID throws `AtmNotFoundException`

**Mocking approach**:
```java
@ExtendWith(MockitoExtension.class)
class AtmServiceTest {
    @Mock
    private AtmRepository atmRepository;

    @InjectMocks
    private AtmService atmService;

    @Test
    void getATMs_withDefaultFilters_returnsNonInterplanetaryATMs() {
        // Given
        when(atmRepository.findByInterPlanetary(false))
            .thenReturn(List.of(testAtm1, testAtm2, testAtm3, testAtm4, testAtm5));

        // When
        List<AtmListResponse> result = atmService.getATMs(new AtmFilterRequest());

        // Then
        assertThat(result).hasSize(4);
        verify(atmRepository).findByInterPlanetary(false);
    }
}
```

#### 1.2 Controller Layer Tests (`AtmControllerTest.java`)

**Purpose**: Test HTTP layer with mocked service

**What to test**:
- Request mapping (correct HTTP methods and paths)
- Request body parsing
- Response status codes
- Response JSON structure
- Error handling

**Mocking approach**:
```java
@WebMvcTest(AtmController.class)
class AtmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtmService atmService;

    @Test
    void getATMs_validRequest_returns200WithJsonArray() throws Exception {
        // Given
        when(atmService.getATMs(any())).thenReturn(testAtmList);

        // When/Then
        mockMvc.perform(post("/api/atm/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isOpenNow\": true}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].name").exists())
            .andExpect(jsonPath("$[0].coordinates.latitude").exists());
    }
}
```

#### 1.3 Repository Layer Tests (`AtmRepositoryTest.java`)

**Purpose**: Test custom query methods (if any)

**Note**: Since we're using Spring Data MongoDB with standard methods and custom queries, we'll test the query generation logic at the service level. Repository tests will be minimal, focusing on interface correctness.

```java
@ExtendWith(MockitoExtension.class)
class AtmRepositoryTest {
    // Test any custom @Query methods if defined
    // For standard CRUD, Spring Data handles the implementation
}
```

#### 1.4 DTO Validation Tests

**Purpose**: Test request/response DTO structure

```java
class AtmDtoTest {
    @Test
    void atmCreateRequest_serializesCorrectly() {
        AtmCreateRequest request = new AtmCreateRequest();
        request.setName("Test ATM");
        // ... set other fields

        String json = objectMapper.writeValueAsString(request);
        assertThat(json).contains("\"name\":\"Test ATM\"");
    }
}
```

#### 1.5 Exception Handler Tests

**Purpose**: Test error response format matches legacy

```java
@WebMvcTest(AtmController.class)
class ExceptionHandlerTest {
    @Test
    void atmNotFound_returns404WithCorrectFormat() throws Exception {
        when(atmService.getSpecificATM(any()))
            .thenThrow(new AtmNotFoundException("ATM not found"));

        mockMvc.perform(get("/api/atm/invalid-id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("ATM information not found"))
            .andExpect(jsonPath("$.stack").isEmpty());
    }
}
```

### 2. Configuration Tests

**Purpose**: Verify Spring configuration loads correctly

```java
@SpringBootTest
class AtmLocatorApplicationTest {
    @Test
    void contextLoads() {
        // Verifies application context starts without errors
    }
}
```

## Test Data Strategy

### Test Fixtures

Create a `TestDataFactory` class with reusable test data:

```java
public class TestDataFactory {

    public static Atm createTestAtm() {
        Atm atm = new Atm();
        atm.setId("64a6f1cc8c1899820dbdf25a");
        atm.setName("Martian ATM (Highway)");
        atm.setAddress(createTestAddress());
        atm.setCoordinates(createTestCoordinates());
        atm.setTimings(createTestTimings());
        atm.setAtmHours("24 hours");
        atm.setNumberOfATMs(2);
        atm.setIsOpen(true);
        atm.setInterPlanetary(false);
        return atm;
    }

    public static Address createTestAddress() {
        Address address = new Address();
        address.setStreet("14th Street, Martian Way");
        address.setCity("Musk City");
        address.setState("Mars");
        address.setZip("40411");
        return address;
    }

    // ... additional factory methods
}
```

### Test Data Sets

| Set | Description | Use Case |
|-----|-------------|----------|
| Single ATM | One complete ATM record | Basic CRUD tests |
| Multiple ATMs (5+) | List exceeding limit | Pagination/limit tests |
| Mixed ATMs | Open/closed, planetary/interplanetary | Filter tests |
| Edge Cases | Empty fields, boundary values | Validation tests |

## Coverage Requirements

### Target: 90% Line Coverage

| Package | Minimum Coverage |
|---------|------------------|
| `controller` | 95% |
| `service` | 95% |
| `dto` | 85% |
| `exception` | 90% |
| `config` | 80% |
| `model` | 80% |

### JaCoCo Configuration

```groovy
// build.gradle
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.11"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/AtmLocatorApplication.class',  // Main class
                '**/config/**'  // Configuration classes (lower coverage OK)
            ])
        }))
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.90
            }
        }
    }
}

test {
    finalizedBy jacocoTestReport
}

check {
    dependsOn jacocoTestCoverageVerification
}
```

## Test Organization

### Directory Structure

```
src/test/java/com/martianbank/atmlocator/
├── controller/
│   └── AtmControllerTest.java
├── service/
│   └── AtmServiceTest.java
├── repository/
│   └── AtmRepositoryTest.java
├── dto/
│   └── AtmDtoTest.java
├── exception/
│   └── GlobalExceptionHandlerTest.java
├── config/
│   └── DataSeederTest.java
└── testutil/
    └── TestDataFactory.java
```

### Naming Conventions

- Test class: `{ClassName}Test.java`
- Test method: `{methodName}_{scenario}_{expectedResult}()`

Examples:
- `getATMs_withIsOpenNowTrue_returnsOnlyOpenATMs()`
- `getSpecificATM_withInvalidId_throws404()`
- `addATM_withValidData_returns201WithCreatedAtm()`

## Test Execution

### Run All Tests
```bash
./gradlew test
```

### Run with Coverage Report
```bash
./gradlew test jacocoTestReport
```

### View Coverage Report
```bash
open build/reports/jacoco/test/html/index.html
```

### Run Specific Test Class
```bash
./gradlew test --tests "AtmServiceTest"
```

### Run Specific Test Method
```bash
./gradlew test --tests "AtmServiceTest.getATMs_withDefaultFilters_returnsNonInterplanetaryATMs"
```

## Critical Test Scenarios

### Must-Have Tests (Parity Verification)

| # | Scenario | Expected Behavior |
|---|----------|-------------------|
| 1 | POST `/api/atm/` with empty body | Returns max 4 non-interplanetary ATMs |
| 2 | POST `/api/atm/` with `isOpenNow: true` | Returns only open, non-interplanetary ATMs |
| 3 | POST `/api/atm/` with `isInterPlanetary: true` | Returns only interplanetary ATMs |
| 4 | POST `/api/atm/` with both filters | Returns open, interplanetary ATMs |
| 5 | GET `/api/atm/{valid-id}` | Returns ATM details (specific fields only) |
| 6 | GET `/api/atm/{invalid-id}` | Returns 404 with `{"message": "ATM information not found"}` |
| 7 | POST `/api/atm/add` with valid data | Returns 201 with created ATM |
| 8 | Results limited to 4 | Even with more matches, only 4 returned |
| 9 | Results randomized | Multiple calls may return different order |
| 10 | Response field projection | `getATMs` returns only: `_id`, `name`, `coordinates`, `address`, `isOpen` |

### Edge Case Tests

| # | Scenario | Expected Behavior |
|---|----------|-------------------|
| 1 | No ATMs match filter | Returns 404 with "No ATMs found" |
| 2 | Exactly 4 ATMs match | Returns all 4 |
| 3 | Less than 4 ATMs match | Returns all matches |
| 4 | Invalid ObjectId format | Returns 404 "Resource not found" |
| 5 | Malformed JSON request | Returns 400 Bad Request |

## Mocking Best Practices

### Do Mock
- Repository layer (all database calls)
- External services (if any added in future)
- System clock (for timestamp testing)

### Don't Mock
- DTOs and model classes
- Utility methods
- The class under test

### Verify Interactions
```java
@Test
void getATMs_callsRepositoryWithCorrectQuery() {
    // Given
    AtmFilterRequest request = new AtmFilterRequest();
    request.setIsOpenNow(true);

    // When
    atmService.getATMs(request);

    // Then
    verify(atmRepository).findByInterPlanetaryAndIsOpen(false, true);
}
```

## Continuous Integration

Tests should be integrated into CI pipeline:

```yaml
# Example GitHub Actions step
- name: Run Tests
  run: ./gradlew test

- name: Check Coverage
  run: ./gradlew jacocoTestCoverageVerification

- name: Upload Coverage Report
  uses: actions/upload-artifact@v3
  with:
    name: coverage-report
    path: build/reports/jacoco/test/html/
```

## Success Criteria

1. All tests pass (`./gradlew test` exits with 0)
2. Coverage meets 90% threshold (`./gradlew jacocoTestCoverageVerification` passes)
3. All critical test scenarios from the table above are covered
4. No flaky tests (tests pass consistently across runs)
5. Test execution time < 30 seconds for full suite
