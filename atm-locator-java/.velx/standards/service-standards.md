# Service Rules and standards

When implementing a new or modifying a service follow these rules:

**Naming:** Interface `{Entity}Service` + Implementation `{Entity}ServiceImpl` in same package
**Annotation:** Apply `@Service` only on implementation class, never on interface
**DI:** Constructor injection with `final` fields (no `@Autowired` needed for single constructor)
**Null safety:** Use `Boolean.TRUE.equals()` for boolean checks; null-check nested objects before access
**Organization:** Public entry method delegates to private helper methods (mapping, filtering)
**Constants:** Declare business rules as `static final` (e.g., `static final int MAX_RESULTS = 4`)

Always use these best practices:

**Transactions:** Apply `@Transactional(readOnly = true)` at class level, override with `@Transactional` for write operations
**Exception handling:** Create custom exceptions extending `RuntimeException` for automatic rollback; never swallow exceptions silently
**Logging:** Use SLF4J with `@Slf4j` (Lombok) or static final Logger; use MDC for request tracing; never log passwords, tokens, or PII
**Caching:** Cache DTOs (not JPA entities); use `@Cacheable` with `sync=true` for expensive operations; use `@CacheEvict` on writes
**Async:** Configure custom `ThreadPoolTaskExecutor` (never use default in production); `@Async` methods must be public and called externally
**SOLID:** One service per domain aggregate; depend on abstractions (interfaces), not implementations

Service Structure Template:
```java
@Service
@Transactional(readOnly = true)
public class {Entity}ServiceImpl implements {Entity}Service {

    static final int MAX_RESULTS = 10;

    private final {Entity}Repository repository;

    public {Entity}ServiceImpl({Entity}Repository repository) {
        this.repository = repository;
    }

    @Override
    public List<{Entity}Response> findAll({Entity}Request request) {
        // 1. Fetch data from repository
        // 2. Map entities to DTOs
        // 3. Apply filters
        // 4. Apply business rules
        // 5. Return result
    }

    @Override
    @Transactional
    public {Entity}Response create({Entity}Request request) {
        // Write operation - overrides class-level readOnly
    }

    private {Entity}Response mapToResponse({Entity} entity) {
        // Null-safe mapping logic
    }
}
```
