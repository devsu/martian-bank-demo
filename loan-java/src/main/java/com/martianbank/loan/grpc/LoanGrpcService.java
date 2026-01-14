package com.martianbank.loan.grpc;

import com.martianbank.loan.model.*;
import com.martianbank.loan.service.LoanService;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * gRPC service implementation
 * Exact replica of Python LoanService class
 * Reference: loan/loan.py:145-180
 */
@GrpcService
public class LoanGrpcService extends LoanServiceGrpc.LoanServiceImplBase {

    private static final Logger LOG = Logger.getLogger(LoanGrpcService.class);

    @Inject
    LoanService loanService;

    /**
     * Process loan request via gRPC
     * Exact replica of Python: LoanService.ProcessLoanRequest()
     * Reference: loan/loan.py:151-167
     */
    @Override
    public void processLoanRequest(LoanProto.LoanRequest request, StreamObserver<LoanProto.LoanResponse> responseObserver) {
        // Extract fields from gRPC request (matches Python: loan.py:152-161)
        LoanRequestDto dto = new LoanRequestDto();
        dto.setName(request.getName());
        dto.setEmail(request.getEmail());
        dto.setAccountType(request.getAccountType());
        dto.setAccountNumber(request.getAccountNumber());
        dto.setGovtIdType(request.getGovtIdType());
        dto.setGovtIdNumber(request.getGovtIdNumber());
        dto.setLoanType(request.getLoanType());
        dto.setLoanAmount(request.getLoanAmount());
        dto.setInterestRate(request.getInterestRate());
        dto.setTimePeriod(request.getTimePeriod());

        // Process loan request using shared business logic
        // Matches Python: loan.py:164
        LoanResponseDto result = loanService.processLoanRequest(dto);

        // Build gRPC response (matches Python: loan.py:166)
        LoanProto.LoanResponse response = LoanProto.LoanResponse.newBuilder()
            .setApproved(result.isApproved())
            .setMessage(result.getMessage())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Get loan history via gRPC
     * Exact replica of Python: LoanService.getLoanHistory()
     * Reference: loan/loan.py:169-180
     */
    @Override
    public void getLoanHistory(LoanProto.LoansHistoryRequest request, StreamObserver<LoanProto.LoansHistoryResponse> responseObserver) {
        // Extract email from request (matches Python: loan.py:171)
        String email = request.getEmail();

        // Get loan history using shared business logic
        // Matches Python: loan.py:175
        List<LoanDocument> loans = loanService.getLoanHistory(email);

        // Build gRPC response (matches Python: loan.py:177-180)
        LoanProto.LoansHistoryResponse.Builder responseBuilder = LoanProto.LoansHistoryResponse.newBuilder();

        for (LoanDocument loan : loans) {
            LoanProto.Loan grpcLoan = LoanProto.Loan.newBuilder()
                .setName(loan.getName())
                .setEmail(loan.getEmail())
                .setAccountType(loan.getAccountType())
                .setAccountNumber(loan.getAccountNumber())
                .setGovtIdType(loan.getGovtIdType())
                .setGovtIdNumber(loan.getGovtIdNumber())
                .setLoanType(loan.getLoanType())
                .setLoanAmount(loan.getLoanAmount())
                .setInterestRate(loan.getInterestRate())
                .setTimePeriod(loan.getTimePeriod())
                .setStatus(loan.getStatus())
                .setTimestamp(loan.getTimestamp())
                .build();
            responseBuilder.addLoans(grpcLoan);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
