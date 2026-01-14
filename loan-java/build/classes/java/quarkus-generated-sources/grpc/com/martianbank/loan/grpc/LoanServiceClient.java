package com.martianbank.loan.grpc;

import java.util.function.BiFunction;
import io.quarkus.grpc.MutinyClient;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: loan.proto")
public class LoanServiceClient implements LoanService, MutinyClient<MutinyLoanServiceGrpc.MutinyLoanServiceStub> {

    private final MutinyLoanServiceGrpc.MutinyLoanServiceStub stub;

    public LoanServiceClient(String name, io.grpc.Channel channel, BiFunction<String, MutinyLoanServiceGrpc.MutinyLoanServiceStub, MutinyLoanServiceGrpc.MutinyLoanServiceStub> stubConfigurator) {
        this.stub = stubConfigurator.apply(name, MutinyLoanServiceGrpc.newMutinyStub(channel));
    }

    private LoanServiceClient(MutinyLoanServiceGrpc.MutinyLoanServiceStub stub) {
        this.stub = stub;
    }

    public LoanServiceClient newInstanceWithStub(MutinyLoanServiceGrpc.MutinyLoanServiceStub stub) {
        return new LoanServiceClient(stub);
    }

    @Override
    public MutinyLoanServiceGrpc.MutinyLoanServiceStub getStub() {
        return stub;
    }

    @Override
    public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoanResponse> processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) {
        return stub.processLoanRequest(request);
    }

    @Override
    public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) {
        return stub.getLoanHistory(request);
    }
}
