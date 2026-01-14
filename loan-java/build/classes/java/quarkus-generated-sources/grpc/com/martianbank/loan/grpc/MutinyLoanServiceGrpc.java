package com.martianbank.loan.grpc;

import static com.martianbank.loan.grpc.LoanServiceGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: loan.proto")
public final class MutinyLoanServiceGrpc implements io.quarkus.grpc.MutinyGrpc {

    private MutinyLoanServiceGrpc() {
    }

    public static MutinyLoanServiceStub newMutinyStub(io.grpc.Channel channel) {
        return new MutinyLoanServiceStub(channel);
    }

    public static class MutinyLoanServiceStub extends io.grpc.stub.AbstractStub<MutinyLoanServiceStub> implements io.quarkus.grpc.MutinyStub {

        private LoanServiceGrpc.LoanServiceStub delegateStub;

        private MutinyLoanServiceStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = LoanServiceGrpc.newStub(channel);
        }

        private MutinyLoanServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = LoanServiceGrpc.newStub(channel).build(channel, callOptions);
        }

        @Override
        protected MutinyLoanServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new MutinyLoanServiceStub(channel, callOptions);
        }

        public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoanResponse> processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) {
            return io.quarkus.grpc.stubs.ClientCalls.oneToOne(request, delegateStub::processLoanRequest);
        }

        public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) {
            return io.quarkus.grpc.stubs.ClientCalls.oneToOne(request, delegateStub::getLoanHistory);
        }
    }

    public static abstract class LoanServiceImplBase implements io.grpc.BindableService {

        private String compression;

        /**
         * Set whether the server will try to use a compressed response.
         *
         * @param compression the compression, e.g {@code gzip}
         */
        public LoanServiceImplBase withCompression(String compression) {
            this.compression = compression;
            return this;
        }

        public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoanResponse> processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.smallrye.mutiny.Uni<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(com.martianbank.loan.grpc.LoanServiceGrpc.getProcessLoanRequestMethod(), asyncUnaryCall(new MethodHandlers<com.martianbank.loan.grpc.LoanProto.LoanRequest, com.martianbank.loan.grpc.LoanProto.LoanResponse>(this, METHODID_PROCESS_LOAN_REQUEST, compression))).addMethod(com.martianbank.loan.grpc.LoanServiceGrpc.getGetLoanHistoryMethod(), asyncUnaryCall(new MethodHandlers<com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest, com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse>(this, METHODID_GET_LOAN_HISTORY, compression))).build();
        }
    }

    private static final int METHODID_PROCESS_LOAN_REQUEST = 0;

    private static final int METHODID_GET_LOAN_HISTORY = 1;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final LoanServiceImplBase serviceImpl;

        private final int methodId;

        private final String compression;

        MethodHandlers(LoanServiceImplBase serviceImpl, int methodId, String compression) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
            this.compression = compression;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_PROCESS_LOAN_REQUEST:
                    io.quarkus.grpc.stubs.ServerCalls.oneToOne((com.martianbank.loan.grpc.LoanProto.LoanRequest) request, (io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoanResponse>) responseObserver, compression, serviceImpl::processLoanRequest);
                    break;
                case METHODID_GET_LOAN_HISTORY:
                    io.quarkus.grpc.stubs.ServerCalls.oneToOne((com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest) request, (io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse>) responseObserver, compression, serviceImpl::getLoanHistory);
                    break;
                default:
                    throw new java.lang.AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                default:
                    throw new java.lang.AssertionError();
            }
        }
    }
}
