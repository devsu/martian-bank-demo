package com.martianbank.loan.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class LoanServiceGrpc {

    private LoanServiceGrpc() {
    }

    public static final java.lang.String SERVICE_NAME = "loan.LoanService";

    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<com.martianbank.loan.grpc.LoanProto.LoanRequest, com.martianbank.loan.grpc.LoanProto.LoanResponse> getProcessLoanRequestMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "ProcessLoanRequest", requestType = com.martianbank.loan.grpc.LoanProto.LoanRequest.class, responseType = com.martianbank.loan.grpc.LoanProto.LoanResponse.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<com.martianbank.loan.grpc.LoanProto.LoanRequest, com.martianbank.loan.grpc.LoanProto.LoanResponse> getProcessLoanRequestMethod() {
        io.grpc.MethodDescriptor<com.martianbank.loan.grpc.LoanProto.LoanRequest, com.martianbank.loan.grpc.LoanProto.LoanResponse> getProcessLoanRequestMethod;
        if ((getProcessLoanRequestMethod = LoanServiceGrpc.getProcessLoanRequestMethod) == null) {
            synchronized (LoanServiceGrpc.class) {
                if ((getProcessLoanRequestMethod = LoanServiceGrpc.getProcessLoanRequestMethod) == null) {
                    LoanServiceGrpc.getProcessLoanRequestMethod = getProcessLoanRequestMethod = io.grpc.MethodDescriptor.<com.martianbank.loan.grpc.LoanProto.LoanRequest, com.martianbank.loan.grpc.LoanProto.LoanResponse>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "ProcessLoanRequest")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(com.martianbank.loan.grpc.LoanProto.LoanRequest.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(com.martianbank.loan.grpc.LoanProto.LoanResponse.getDefaultInstance())).setSchemaDescriptor(new LoanServiceMethodDescriptorSupplier("ProcessLoanRequest")).build();
                }
            }
        }
        return getProcessLoanRequestMethod;
    }

    private static volatile io.grpc.MethodDescriptor<com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest, com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getGetLoanHistoryMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "getLoanHistory", requestType = com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest.class, responseType = com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest, com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getGetLoanHistoryMethod() {
        io.grpc.MethodDescriptor<com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest, com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getGetLoanHistoryMethod;
        if ((getGetLoanHistoryMethod = LoanServiceGrpc.getGetLoanHistoryMethod) == null) {
            synchronized (LoanServiceGrpc.class) {
                if ((getGetLoanHistoryMethod = LoanServiceGrpc.getGetLoanHistoryMethod) == null) {
                    LoanServiceGrpc.getGetLoanHistoryMethod = getGetLoanHistoryMethod = io.grpc.MethodDescriptor.<com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest, com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "getLoanHistory")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse.getDefaultInstance())).setSchemaDescriptor(new LoanServiceMethodDescriptorSupplier("getLoanHistory")).build();
                }
            }
        }
        return getGetLoanHistoryMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static LoanServiceStub newStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<LoanServiceStub> factory = new io.grpc.stub.AbstractStub.StubFactory<LoanServiceStub>() {

            @java.lang.Override
            public LoanServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new LoanServiceStub(channel, callOptions);
            }
        };
        return LoanServiceStub.newStub(factory, channel);
    }

    /**
     * Creates a new blocking-style stub that supports all types of calls on the service
     */
    public static LoanServiceBlockingV2Stub newBlockingV2Stub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<LoanServiceBlockingV2Stub> factory = new io.grpc.stub.AbstractStub.StubFactory<LoanServiceBlockingV2Stub>() {

            @java.lang.Override
            public LoanServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new LoanServiceBlockingV2Stub(channel, callOptions);
            }
        };
        return LoanServiceBlockingV2Stub.newStub(factory, channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static LoanServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<LoanServiceBlockingStub> factory = new io.grpc.stub.AbstractStub.StubFactory<LoanServiceBlockingStub>() {

            @java.lang.Override
            public LoanServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new LoanServiceBlockingStub(channel, callOptions);
            }
        };
        return LoanServiceBlockingStub.newStub(factory, channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static LoanServiceFutureStub newFutureStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<LoanServiceFutureStub> factory = new io.grpc.stub.AbstractStub.StubFactory<LoanServiceFutureStub>() {

            @java.lang.Override
            public LoanServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new LoanServiceFutureStub(channel, callOptions);
            }
        };
        return LoanServiceFutureStub.newStub(factory, channel);
    }

    /**
     */
    public interface AsyncService {

        /**
         */
        default void processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request, io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoanResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getProcessLoanRequestMethod(), responseObserver);
        }

        /**
         */
        default void getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request, io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetLoanHistoryMethod(), responseObserver);
        }
    }

    /**
     * Base class for the server implementation of the service LoanService.
     */
    public static abstract class LoanServiceImplBase implements io.grpc.BindableService, AsyncService {

        @java.lang.Override
        public io.grpc.ServerServiceDefinition bindService() {
            return LoanServiceGrpc.bindService(this);
        }
    }

    /**
     * A stub to allow clients to do asynchronous rpc calls to service LoanService.
     */
    public static class LoanServiceStub extends io.grpc.stub.AbstractAsyncStub<LoanServiceStub> {

        private LoanServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected LoanServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new LoanServiceStub(channel, callOptions);
        }

        /**
         */
        public void processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request, io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoanResponse> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getProcessLoanRequestMethod(), getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request, io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getGetLoanHistoryMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     * A stub to allow clients to do synchronous rpc calls to service LoanService.
     */
    public static class LoanServiceBlockingV2Stub extends io.grpc.stub.AbstractBlockingStub<LoanServiceBlockingV2Stub> {

        private LoanServiceBlockingV2Stub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected LoanServiceBlockingV2Stub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new LoanServiceBlockingV2Stub(channel, callOptions);
        }

        /**
         */
        public com.martianbank.loan.grpc.LoanProto.LoanResponse processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) throws io.grpc.StatusException {
            return io.grpc.stub.ClientCalls.blockingV2UnaryCall(getChannel(), getProcessLoanRequestMethod(), getCallOptions(), request);
        }

        /**
         */
        public com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) throws io.grpc.StatusException {
            return io.grpc.stub.ClientCalls.blockingV2UnaryCall(getChannel(), getGetLoanHistoryMethod(), getCallOptions(), request);
        }
    }

    /**
     * A stub to allow clients to do limited synchronous rpc calls to service LoanService.
     */
    public static class LoanServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<LoanServiceBlockingStub> {

        private LoanServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected LoanServiceBlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new LoanServiceBlockingStub(channel, callOptions);
        }

        /**
         */
        public com.martianbank.loan.grpc.LoanProto.LoanResponse processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getProcessLoanRequestMethod(), getCallOptions(), request);
        }

        /**
         */
        public com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getGetLoanHistoryMethod(), getCallOptions(), request);
        }
    }

    /**
     * A stub to allow clients to do ListenableFuture-style rpc calls to service LoanService.
     */
    public static class LoanServiceFutureStub extends io.grpc.stub.AbstractFutureStub<LoanServiceFutureStub> {

        private LoanServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected LoanServiceFutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new LoanServiceFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<com.martianbank.loan.grpc.LoanProto.LoanResponse> processLoanRequest(com.martianbank.loan.grpc.LoanProto.LoanRequest request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getProcessLoanRequestMethod(), getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse> getLoanHistory(com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getGetLoanHistoryMethod(), getCallOptions()), request);
        }
    }

    private static final int METHODID_PROCESS_LOAN_REQUEST = 0;

    private static final int METHODID_GET_LOAN_HISTORY = 1;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final AsyncService serviceImpl;

        private final int methodId;

        MethodHandlers(AsyncService serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_PROCESS_LOAN_REQUEST:
                    serviceImpl.processLoanRequest((com.martianbank.loan.grpc.LoanProto.LoanRequest) request, (io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoanResponse>) responseObserver);
                    break;
                case METHODID_GET_LOAN_HISTORY:
                    serviceImpl.getLoanHistory((com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest) request, (io.grpc.stub.StreamObserver<com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    public static io.grpc.ServerServiceDefinition bindService(AsyncService service) {
        return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(getProcessLoanRequestMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<com.martianbank.loan.grpc.LoanProto.LoanRequest, com.martianbank.loan.grpc.LoanProto.LoanResponse>(service, METHODID_PROCESS_LOAN_REQUEST))).addMethod(getGetLoanHistoryMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<com.martianbank.loan.grpc.LoanProto.LoansHistoryRequest, com.martianbank.loan.grpc.LoanProto.LoansHistoryResponse>(service, METHODID_GET_LOAN_HISTORY))).build();
    }

    private static abstract class LoanServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {

        LoanServiceBaseDescriptorSupplier() {
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return com.martianbank.loan.grpc.LoanProto.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("LoanService");
        }
    }

    private static final class LoanServiceFileDescriptorSupplier extends LoanServiceBaseDescriptorSupplier {

        LoanServiceFileDescriptorSupplier() {
        }
    }

    private static final class LoanServiceMethodDescriptorSupplier extends LoanServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {

        private final java.lang.String methodName;

        LoanServiceMethodDescriptorSupplier(java.lang.String methodName) {
            this.methodName = methodName;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (LoanServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME).setSchemaDescriptor(new LoanServiceFileDescriptorSupplier()).addMethod(getProcessLoanRequestMethod()).addMethod(getGetLoanHistoryMethod()).build();
                }
            }
        }
        return result;
    }
}
