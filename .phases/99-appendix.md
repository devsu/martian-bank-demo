# Appendix

## Testing Strategy Summary

### Unit Tests
Unit tests are included in each coding phase (Phase 1.5, 2.5, 3.5, 4.5, 5.5) with:
- JUnit 5 as the test framework
- Mockito for mocking MongoDB and external dependencies
- REST Assured for HTTP endpoint testing
- JaCoCo for coverage measurement (minimum 90% threshold)

### Integration and Manual Tests
All integration tests and manual verification steps are consolidated in **Phase 7: Final Manual Verification**.

## Performance Considerations

The Java implementation maintains the same performance characteristics as Python:
- Full collection scan in `getAccountByAccountNumber()` (intentional - matching Python behavior)
- No connection pooling optimizations
- No query index requirements

## Migration Notes

### Switching Between Python and Java Services

**To use Python (default):**
```bash
docker-compose up --build
```

**To use Java:**
```bash
docker-compose -f docker-compose.yaml -f docker-compose.java.yaml up --build
```

### Rollback Procedure

If issues occur with Java service:
1. Stop Docker Compose: `docker-compose down`
2. Start with default (Python): `docker-compose up --build`

## References

- Original Python implementation: `loan/loan.py`
- Proto definition: `protobufs/loan.proto`
- Docker Compose: `docker-compose.yaml`
- Python Dockerfile: `loan/Dockerfile`
