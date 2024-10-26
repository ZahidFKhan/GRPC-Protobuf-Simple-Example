package com.zahid.grpc.server;


import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.zahid.models.StudentRequest;
import org.zahid.models.StudentResponse;
import org.zahid.models.StudentServiceGrpc;

@GRpcService
@Slf4j
public class Server extends StudentServiceGrpc.StudentServiceImplBase {
    @Override
    public void getStudent(StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
        final var builder = StudentResponse.newBuilder();
        final var zahid = builder.setName("Zahid").setAge(20).build();

        responseObserver.onNext(zahid);
        responseObserver.onCompleted();
        log.info("Response sent");
    }
}
