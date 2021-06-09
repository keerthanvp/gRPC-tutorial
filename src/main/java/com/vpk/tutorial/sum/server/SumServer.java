package com.vpk.tutorial.sum.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SumServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC - Sum Server");

        Server server = ServerBuilder.forPort(50051)
                .addService(new SumServiceImpl())
                .build();
        server.start();
        System.out.println("gRPC server started");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped server");
        }));

        server.awaitTermination();
    }
}
