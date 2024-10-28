package com.zahid.grpc.client;

import com.github.javafaker.Faker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zahid.models.StudentRequest;
import org.zahid.models.StudentResponse;
import org.zahid.models.StudentServiceGrpc;

@Slf4j
public class Client {
  @SneakyThrows
  public static void main(String[] args) {
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

    final StudentServiceGrpc.StudentServiceStub nonBlockingStub =
        StudentServiceGrpc.newStub(managedChannel);

    final var decisionMaking = 3;
    switch (decisionMaking) {
      case 0:
        blockingCall(managedChannel);
        break;
      case 1:
        serverSideStreaming(nonBlockingStub);
        Thread.sleep(10000);
        break;
      case 2:
        clientSideStreaming(nonBlockingStub);
        Thread.sleep(10000);
        break;
      case 3:
        biDirectionalStreaming(nonBlockingStub);
        Thread.sleep(10000);
        break;
      default:
        throw new RuntimeException();
    }
  }

  private static void biDirectionalStreaming(
      StudentServiceGrpc.StudentServiceStub nonBlockingStub) {

    final var biDirectionalStream =
        nonBlockingStub.biDirectionalStream(
            new StreamObserver<>() {
              @Override
              public void onNext(StudentResponse value) {
                log.info("received response from server : {}", value);
              }

              @Override
              public void onError(Throwable t) {}

              @Override
              public void onCompleted() {}
            });

    for (int loop = 0; loop < 50; loop++) {
      final var instance = Faker.instance();
      biDirectionalStream.onNext(
          StudentRequest.newBuilder().setId(instance.number().numberBetween(0, 50)).build());
    }
    biDirectionalStream.onCompleted();
  }

  private static void clientSideStreaming(StudentServiceGrpc.StudentServiceStub nonBlockingStub) {
    final var studentRequestObserver =
        nonBlockingStub.clientSideStreaming(
            new StreamObserver<>() {
              @Override
              public void onNext(StudentResponse value) {
                log.info("received response from server : {}", value);
              }

              @Override
              public void onError(Throwable t) {}

              @Override
              public void onCompleted() {}
            });

    for (int loop = 0; loop < 50; loop++) {
      studentRequestObserver.onNext(
          StudentRequest.newBuilder()
              .setId(Faker.instance().number().numberBetween(0, 100))
              .build());
    }
    studentRequestObserver.onCompleted();
  }

  private static StudentServiceGrpc.StudentServiceStub serverSideStreaming(
      StudentServiceGrpc.StudentServiceStub nonBlockingStub) {
    final var studentRequest = StudentRequest.newBuilder().setId(2).build();

    nonBlockingStub.serverSideStreaming(
        studentRequest,
        new StreamObserver<>() {
          @Override
          public void onNext(StudentResponse value) {
            log.info("Received response from Server : {}", value);
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {
            log.info("Completed receiving from the server.");
          }
        });
    return nonBlockingStub;
  }

  private static void blockingCall(ManagedChannel managedChannel) {
    final StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub =
        StudentServiceGrpc.newBlockingStub(managedChannel);

    final StudentResponse studentResponse =
        studentServiceStub.getStudent(StudentRequest.newBuilder().setId(2).build());

    log.info("Response {}", studentResponse);
  }
}
