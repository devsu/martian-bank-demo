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
