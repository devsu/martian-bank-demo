# Test rules and standards

When implementing new or modifying tests follow these rules:

**Class Naming:** `{ClassName}Test` in mirrored package structure under `src/test/java`
**Method Naming:** `should{ExpectedBehavior}When{Condition}` (e.g., `shouldReturnEmptyListWhenNoItemsExist`)
**Framework:** JUnit 5 + Mockito + AssertJ (all included via `spring-boot-starter-test`)
**Annotations:** Use `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks` for unit tests
**Organization:** Group related tests with `@Nested` inner classes and `@DisplayName`
**Structure:** Follow Arrange-Act-Assert (or Given-When-Then) pattern

Always use these best practices:

**Test Slices:** Prefer `@WebMvcTest`, `@DataMongoTest` over `@SpringBootTest` (faster, smaller context)
**Always mock database:** Database connection must be always mocked
**Mocking:** Use `@MockitoBean` (Spring Boot 3.x) over `@MockBean`; use `BDDMockito.given()` for readable stubs
**Assertions:** Use AssertJ fluent assertions (`assertThat()`); use `allSatisfy()` for collections; use soft assertions for multiple checks
**Test Data:** Create builder helper methods; use `@BeforeEach` for shared setup; leverage Lombok `@Builder` from domain objects
**Cleanup:** Clean data BEFORE each test with `@BeforeEach`, not after; avoid `@DirtiesContext` (slow)
**Parameterized:** Use `@ParameterizedTest` with `@CsvSource` for input variations and edge cases

Unit Test Structure Template:
```java
@ExtendWith(MockitoExtension.class)
class {ClassName}Test {

    @Mock
    private {Dependency}Repository repository;

    @InjectMocks
    private {ClassName}Impl classUnderTest;

    private List<{Entity}> testData;

    @BeforeEach
    void setUp() {
        testData = createTestData();
    }

    @Nested
    @DisplayName("findAll scenarios")
    class FindAllTests {

        @Test
        @DisplayName("should return all items when no filters applied")
        void shouldReturnAllItemsWhenNoFiltersApplied() {
            // Arrange (Given)
            when(repository.findAll()).thenReturn(testData);

            // Act (When)
            List<{Response}> result = classUnderTest.findAll(null);

            // Assert (Then)
            assertThat(result).isNotNull();
            assertThat(result).hasSize(testData.size());
        }

        @Test
        void shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
            // Arrange
            when(repository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<{Response}> result = classUnderTest.findAll(null);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    // Helper methods
    private List<{Entity}> createTestData() {
        return List.of(
            build{Entity}("1", "Name1", true),
            build{Entity}("2", "Name2", false)
        );
    }

    private {Entity} build{Entity}(String id, String name, boolean active) {
        return {Entity}.builder()
                .id(id)
                .name(name)
                .active(active)
                .build();
    }
}
```

Integration Test Structure Template:
```java
@DataMongoTest
@Testcontainers
class {Entity}RepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private {Entity}Repository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveEntity() {
        // Arrange
        {Entity} entity = build{Entity}("test-id", "Test Name");

        // Act
        repository.save(entity);
        Optional<{Entity}> result = repository.findById("test-id");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Name");
    }
}
```
