# Exception Handler Tasks

## Overview

Implement centralized exception handling with @ControllerAdvice to provide consistent error responses across all endpoints with proper HTTP status codes and detailed validation messages.

## Prerequisites

- DTOs created including ErrorResponse ([TASK-007])
- Controllers implemented ([TASK-021], [TASK-022], [TASK-023])

## Tasks

### [TASK-019] - [AI] Implement global exception handler with @ControllerAdvice

**Why**: Centralized exception handling ensures consistent error responses and eliminates duplicate error handling logic across controllers.

**What**:
- Create GlobalExceptionHandler class with @ControllerAdvice annotation
- Add @ExceptionHandler for MethodArgumentNotValidException (JSR-380 validation failures)
- Add @ExceptionHandler for IllegalArgumentException (business logic errors)
- Add @ExceptionHandler for EntityNotFoundException (resource not found)
- Add @ExceptionHandler for Exception (catch-all for unexpected errors)
- Map exceptions to appropriate HTTP status codes (400, 404, 500)
- Extract field-level validation errors from MethodArgumentNotValidException
- Suppress stack traces in production using environment-based configuration
- Include timestamp in all error responses

**Testing** (TDD - write tests first):
- Unit test: MethodArgumentNotValidException maps to HTTP 400
- Unit test: Field validation errors extracted and included in details
- Unit test: IllegalArgumentException maps to HTTP 400
- Unit test: EntityNotFoundException maps to HTTP 404
- Unit test: Generic Exception maps to HTTP 500
- Unit test: Error response includes code, message, timestamp, details
- Unit test: Stack trace suppressed in production profile

**Dependencies**: [TASK-007] ErrorResponse DTO

---

### [TASK-020] - [AI] Create standardized error response DTOs

**Why**: Consistent error format enables clients to handle errors predictably across all endpoints.

**What**:
- Create ErrorResponse DTO with code (String), message (String), timestamp (LocalDateTime), details (Map<String, String>) fields
- Use Lombok @Data and @Builder for construction
- Add factory methods for common error types: validationError(), notFound(), internalError()
- Ensure timestamp formatted as ISO 8601 in JSON responses
- Create FieldError DTO for validation error details with field name and error message
- Support multiple field errors in single response
- Ensure error codes follow consistent naming (VALIDATION_ERROR, NOT_FOUND, INTERNAL_ERROR)

**Testing** (TDD - write tests first):
- Unit test: ErrorResponse serializes to expected JSON structure
- Unit test: Factory methods create appropriate error codes and messages
- Unit test: Timestamp formats correctly in ISO 8601
- Unit test: Multiple field errors included in details map
- Unit test: Null details handled gracefully (empty map or omitted)

**Dependencies**: [TASK-003] Jackson dependencies
