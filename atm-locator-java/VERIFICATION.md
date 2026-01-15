# ATM Locator Java - Migration Verification Checklist

## API Endpoint Parity

### POST /api/atm/
- [ ] Returns JSON array
- [ ] Maximum 4 items returned
- [ ] Items contain: _id, name, coordinates, address, isOpen
- [ ] Items exclude: timings, atmHours, numberOfATMs, interPlanetary
- [ ] Results are randomized
- [ ] Empty filter returns non-interplanetary ATMs
- [ ] isOpenNow=true returns only open ATMs
- [ ] isInterPlanetary=true returns interplanetary ATMs
- [ ] No results returns 404 with "No ATMs found"

### POST /api/atm/add
- [ ] Returns 201 status code
- [ ] Returns full created ATM document
- [ ] Generated _id included
- [ ] Timestamps included (createdAt, updatedAt)
- [ ] Nested objects created correctly (address, coordinates, timings)

### GET /api/atm/{id}
- [ ] Returns 200 for valid ID
- [ ] Returns: coordinates, timings, atmHours, numberOfATMs, isOpen
- [ ] Excludes: _id, name, address, interPlanetary
- [ ] Invalid ID returns 404
- [ ] Error response has message and stack fields

## Error Response Format

### 404 Errors
- [ ] Response body: `{"message": "...", "stack": null}`
- [ ] Stack is null in production mode
- [ ] Stack populated in development mode
- [ ] "ATM not found" mapped to "ATM information not found"

### Invalid ObjectId
- [ ] Returns 404 (not 400)
- [ ] Message: "Resource not found"

## Database Behavior

- [ ] Connects to MongoDB on startup
- [ ] Seeds 13 ATM records from atm_data.json
- [ ] Drops existing collection before seeding
- [ ] Supports DATABASE_HOST environment variable
- [ ] Supports DB_URL environment variable

## Docker Integration

- [ ] Container builds successfully
- [ ] Container starts without errors
- [ ] Health check passes
- [ ] Port 8001 exposed
- [ ] Works with docker-compose
- [ ] NGINX routing works

## OpenAPI Documentation

- [ ] Swagger UI available at /docs
- [ ] OpenAPI JSON at /docs.json
- [ ] All endpoints documented
- [ ] Request/response schemas shown

## CORS Configuration

- [ ] All origins allowed
- [ ] Credentials allowed
- [ ] Preflight requests handled

## Performance

- [ ] Startup time < 30 seconds
- [ ] Response time < 500ms
- [ ] Memory usage < 512MB
