# Controller rules and standands

**Naming:** {Entity}Controller with @RestController + @RequestMapping("/api/{resource}")
**DI:** Constructor injection with final fields (no @Autowired)
**DTOs:** Java Records for request/response objects
**Response:** Wrap in ResponseEntity<T> using ResponseEntity.ok(body)
**Null safety:** Use @RequestBody(required = false) for optional requests

Always use these best practices:
**Global exception handling:** Use @RestControllerAdvice with structured error responses (RFC 7807 ProblemDetail)
**Input validation:** Use @Valid with Bean Validation annotations; @Validated for validation groups
**Status codes:**: Always use the `atm-locator-java/.velx/specs/openapi.yaml` file for reference