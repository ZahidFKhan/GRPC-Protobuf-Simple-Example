package com.zahid.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.zahid.models.StudentRequest;
import org.zahid.models.StudentResponse;
import org.zahid.models.StudentServiceGrpc;

@Slf4j
public class Client {
  public static void main(String[] args) {
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

    final StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub =
        StudentServiceGrpc.newBlockingStub(managedChannel);

    final StudentResponse studentResponse =
        studentServiceStub.getStudent(StudentRequest.newBuilder().setId(2).build());

    log.info("Response {}", studentResponse);
  }
}
