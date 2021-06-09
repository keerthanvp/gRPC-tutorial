package com.vpk.tutorial.greet.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello, I'm gRPC Client");
        GreetingClient client = new GreetingClient();
        client.run();
    }

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

//        unaryAPI(channel);
//        serverStreamingAPI(channel);
//        clientStreamingAPI(channel);
        biDiStreamingAPI(channel);

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void unaryAPI(ManagedChannel channel){
        System.out.println("Creating a stub");

        //Create a client (sync client)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //Create protocol buffer message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bobby")
                .setLastName("Firmino")
                .build();

        //Create request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //RPC call
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println("GreetResponse: "+greetResponse.getResult());
    }

    private void serverStreamingAPI(ManagedChannel channel){
        System.out.println("Creating a stub");

        //Create a client (sync client)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //Create protocol buffer message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bobby")
                .setLastName("Firmino")
                .build();

        //Create request
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //RPC call
        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void clientStreamingAPI(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceStub asyncGreetClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncGreetClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //We get a response from the server
                System.out.println("Response from the server: "+ value.getResult() + "\n");
            }

            @Override
            public void onError(Throwable t) {
                //We get an error from the server
            }

            @Override
            public void onCompleted() {
                //The server is done sending us data
                System.out.println("Server has completed sending data");
                latch.countDown();
            }
        });

        System.out.println("Sending Message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Alisson")
                        .setLastName("Becker")
                        .build())
                .build());

        System.out.println("Sending Message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Trent")
                        .setLastName("Alexander-Arnold")
                        .build())
                .build());

        System.out.println("Sending Message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Andrew")
                        .setLastName("Robertson")
                        .build())
                .build());

        //Informing server that client has completed sending data
        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void biDiStreamingAPI(ManagedChannel channel){
        GreetServiceGrpc.GreetServiceStub asyncGreetClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncGreetClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: "+value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending data");
                latch.countDown();
            }
        });

        Arrays.asList("Steven","Mark","David","Joseph","Mitchel","Enda","Charles").forEach(name->{
            System.out.println("Sending: "+name);
            requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder()
                            .setFirstName(name)
                            .setLastName("VP")
                            .build())
                    .build());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        requestObserver.onCompleted();

        try {
            latch.await(3,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
