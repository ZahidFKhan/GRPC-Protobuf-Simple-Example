package com.zahid.grpc.server;

import com.github.javafaker.Faker;
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
  public void serverSideStreaming(
      StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
    log.info("Received input from Client: {}", request);
    for (int loop = 0; loop < 50; loop++) {
      final var fakerInstance = Faker.instance();
      final StudentResponse nextStudent =
          StudentResponse.newBuilder()
              .setName(fakerInstance.name().firstName())
              .setAge(fakerInstance.number().numberBetween(18, 60))
              .build();

      log.info("Sent to client : {}", nextStudent);
      responseObserver.onNext(nextStudent);
    }
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<StudentRequest> clientSideStreaming(
      StreamObserver<StudentResponse> responseObserver) {
    return new StreamObserver<>() {
      @Override
      public void onNext(StudentRequest value) {
        log.info("Request received from client: {}", value);
      }

      @Override
      public void onError(Throwable t) {
        log.error("Request received : {}", t.getMessage());
      }

      @Override
      public void onCompleted() {
        responseObserver.onNext(StudentResponse.getDefaultInstance());
        responseObserver.onCompleted();
        log.info("Request completed");
      }
    };
  }

  @Override
  public StreamObserver<StudentRequest> biDirectionalStream(
      StreamObserver<StudentResponse> responseObserver) {
    var observer =
        new StreamObserver<StudentRequest>() {
          @Override
          public void onNext(StudentRequest studentRequest) {
            log.info("received from client {}", studentRequest);
            final var instance = Faker.instance();
            final var instanceOfStudentObject =
                StudentResponse.newBuilder()
                    .setName(instance.name().firstName())
                    .setAge(instance.number().numberBetween(18, 65))
                    .build();

            responseObserver.onNext(instanceOfStudentObject);
          }

          @Override
          public void onError(Throwable t) {
            log.error("Error occurred while processing the record");
          }

          @Override
          public void onCompleted() {
            responseObserver.onNext(StudentResponse.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("Completed all the requests.");
          }
        };
    return observer;
  }

  @Override
  public void getStudent(StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
    final var builder = StudentResponse.newBuilder();
    final var zahid = builder.setName("Zahid").setAge(20).build();

    responseObserver.onNext(zahid);
    responseObserver.onCompleted();
    log.info("Response sent");
  }
}
