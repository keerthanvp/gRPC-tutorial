package com.vpk.tutorial.sum.client;

import com.proto.sum.Sum;
import com.proto.sum.SumRequest;
import com.proto.sum.SumResponse;
import com.proto.sum.SumServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SumClient {
    public static void main(String[] args) {
        System.out.println("Hello, I'm gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

        System.out.println("Creating a stub");
        //Create a client (sync client)
        SumServiceGrpc.SumServiceBlockingStub sumClient = SumServiceGrpc.newBlockingStub(channel);

        //Create request
        SumRequest sumRequest = SumRequest.newBuilder()
                .setSum(Sum.newBuilder()
                        .setFirstNumber(7)
                        .setSecondNumber(9)
                        .build())
                .build();

        //RPC call
        SumResponse sumResponse = sumClient.sum(sumRequest);


        System.out.println("Sum: "+sumResponse.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();

    }
}
