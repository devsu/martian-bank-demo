# Phase 4: HTTP REST Endpoints

## Overview
Implement Flask-equivalent REST endpoints using Quarkus RESTEasy that match the exact paths, methods, and response formats.

## Changes Required:

### 1. Loan REST Resource
**File**: `loan-java/src/main/java/com/martianbank/loan/resource/LoanResource.java`

```java
package com.martianbank.loan.resource;

import com.martianbank.loan.model.*;
import com.martianbank.loan.service.LoanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * REST endpoints for loan operations
 * Exact replica of Python Flask routes
 * Reference: loan/loan.py:185-201
 */
@Path("/loan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    private static final Logger LOG = Logger.getLogger(LoanResource.class);

    @Inject
    LoanService loanService;

    /**
     * Process loan request endpoint
     * Matches Python: @app.route("/loan/request", methods=["POST"])
     * Reference: loan/loan.py:187-192
     */
    @POST
    @Path("/request")
    public LoanResponseDto processLoanRequest(LoanRequestDto request) {
        LOG.debugf("Request: %s", request);
        return loanService.processLoanRequest(request);
    }

    /**
     * Get loan history endpoint
     * Matches Python: @app.route("/loan/history", methods=["POST"])
     * Reference: loan/loan.py:195-201
     */
    @POST
    @Path("/history")
    public List<LoanDocument> getLoanHistory(LoanHistoryRequestDto request) {
        LOG.debug("----------------> Request: /loan/history");
        LOG.debugf("Request: %s", request);
        return loanService.getLoanHistory(request.getEmail());
    }
}
```

## Success Criteria:

### Automated Verification:
- [ ] Build succeeds: `cd loan-java && ./gradlew compileJava`
- [ ] Application starts: `cd loan-java && ./gradlew quarkusDev`
- [ ] Endpoint responds: `curl -X POST http://localhost:50053/loan/request -H "Content-Type: application/json" -d '{"name":"test","email":"test@test.com","account_type":"savings","account_number":"123","govt_id_type":"passport","govt_id_number":"ABC123","loan_type":"personal","loan_amount":100,"interest_rate":5.5,"time_period":"12 months"}'`

**Implementation Note**: After completing this phase and all automated verification passes, proceed to Phase 4.5 for testing.
