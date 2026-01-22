# Service Tasks

## Overview

Implement business logic for ATM operations including geospatial distance calculations using Haversine formula, radius-based filtering, and dynamic isOpenNow calculation based on server time and operating hours.

## Prerequisites

- ATM entity and DTOs created ([TASK-004], [TASK-007])
- Repository interface created ([TASK-010])
- Test infrastructure set up ([TASK-008], [TASK-009])

## Tasks

### [TASK-016] - [AI] Implement ATM Service with geospatial distance calculations

**Why**: Service layer encapsulates business logic for ATM queries while maintaining testability through repository abstraction.

**What**:
- Create ATMService class with @Service annotation
- Inject ATMRepository via constructor using @RequiredArgsConstructor (Lombok)
- Implement findNearbyATMs(latitude, longitude, radius) method
- Use Haversine formula to calculate distance between coordinates in kilometers
- Filter ATMs within specified radius
- Return filtered list of ATMs
- Implement getATMById(id) method with Optional return type
- Implement getAllATMs(Pageable) method for paginated listing
- Implement createATM(atmRequest) method with DTO to entity mapping

**Testing** (TDD - write tests first):
- Unit test: Verify Haversine formula calculates correct distances (test with known coordinate pairs)
- Unit test: Test radius filtering returns only ATMs within range
- Unit test: Validate edge cases (equator, poles, prime meridian crossings)
- Unit test: Test empty results when no ATMs within radius
- Unit test: Mock repository and verify service calls repository methods
- Unit test: Test getATMById returns Optional.empty() for non-existent ID

**Dependencies**: [TASK-010] repository, [TASK-009] test fixtures

---

### [TASK-017] - [AI] Implement dynamic isOpenNow calculation logic

**Why**: Dynamic calculation ensures real-time accuracy of ATM availability status without storing stale data.

**What**:
- Add calculateIsOpenNow(ATM atm) private method to ATMService
- Parse operating hours from timings object (monFri, satSun, holidays)
- Get current server time and day of week using Clock (injectable for testing)
- Compare current time against parsed operating hours ranges
- Handle special cases: 24/7 ATMs (always open), closed ATMs, null timings
- Handle time ranges crossing midnight (e.g., "22:00-02:00")
- Set isOpenNow field on ATM entities before returning from service methods
- Support multiple time ranges per day (e.g., "09:00-12:00,14:00-17:00")

**Testing** (TDD - write tests first):
- Unit test: Mock Clock to test specific times of day
- Unit test: Verify 24/7 ATMs always return isOpenNow=true
- Unit test: Test business hours (e.g., 09:00-17:00 on weekday)
- Unit test: Test closed ATMs always return isOpenNow=false
- Unit test: Test weekend hours differ from weekday hours
- Unit test: Test holiday hours (simulate holiday detection)
- Unit test: Handle null timings gracefully (default to closed)
- Unit test: Test midnight crossing time ranges
- Unit test: Validate time parsing with invalid format returns closed

**Dependencies**: [TASK-016] service structure

---

### [TASK-018] - [AI] Create comprehensive unit tests for service layer

**Why**: Service layer contains critical business logic requiring >80% test coverage for quality assurance.

**What**:
- Create ATMServiceTest class with @ExtendWith(MockitoExtension.class)
- Mock ATMRepository using @Mock and Clock using @Mock
- Use @InjectMocks for ATMService to auto-inject mocks
- Write tests for all public service methods
- Test distance calculation accuracy with known coordinate pairs
- Test radius filtering with various distances
- Test isOpenNow calculation for all time scenarios
- Test pagination support in getAllATMs
- Test createATM with valid and invalid inputs
- Use ArgumentCaptor to verify repository save calls
- Test exception handling for invalid ObjectIds

**Testing** (TDD - write tests first):
- Achieve >80% code coverage measured by JaCoCo
- Test all public methods with happy path and edge cases
- Verify all repository method calls with Mockito.verify()
- Test concurrency safety for stateless service methods
- Validate null handling for optional parameters

**Dependencies**: [TASK-016] service implementation, [TASK-017] isOpenNow logic, [TASK-009] test fixtures
