package com.vpk.tutorial.greet.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //Fetch the fields needed
        Greeting greeting = request.getGreeting();
        String first_name = greeting.getFirstName();

        //Create the response
        String result = "Hello " + first_name + " " + greeting.getLastName();
        GreetResponse greetResponse = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        //Send the reponse
        responseObserver.onNext(greetResponse);

        //Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String result = request.getGreeting().getFirstName() + " " + request.getGreeting().getLastName();
        try {
            for (int i = 1; i <= 10; i++) {
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                        .setResult(result+", response number: "+i)
                        .build();

                responseObserver.onNext(response);
                Thread.sleep(1000);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
            String result = "";
            @Override
            public void onNext(LongGreetRequest value) {
                result+="Hello "+value.getGreeting().getFirstName() + " "+value.getGreeting().getLastName() + "\n";
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build());
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello, " + value.getGreeting().getFirstName() + " "+value.getGreeting().getLastName();
                responseObserver.onNext(GreetEveryoneResponse.newBuilder()
                        .setResult(result)
                        .build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
