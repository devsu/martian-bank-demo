package com.martianbank.loan.grpc;

import io.grpc.BindableService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.grpc.MutinyBean;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: loan.proto")
public class LoanServiceBean extends MutinyLoanServiceGrpc.LoanServiceImplBase implements BindableService, MutinyBean {

    private final LoanService delegate;

    LoanServiceBean(@GrpcService LoanService delegate) {
        this.delegate = delegate;
    }

    @Override
    public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoanResponse> processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) {
        try {
            return delegate.processLoanRequest(request);
        } catch (UnsupportedOperationException e) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }
    }

    @Override
    public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) {
        try {
            return delegate.getLoanHistory(request);
        } catch (UnsupportedOperationException e) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }
    }
}
