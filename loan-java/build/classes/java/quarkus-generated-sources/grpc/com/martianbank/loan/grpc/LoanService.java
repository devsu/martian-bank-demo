package com.martianbank.loan.grpc;

import io.quarkus.grpc.MutinyService;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: loan.proto")
public interface LoanService extends MutinyService {

    io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoanResponse> processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request);

    io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request);
}
