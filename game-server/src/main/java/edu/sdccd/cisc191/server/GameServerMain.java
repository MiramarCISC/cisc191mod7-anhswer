package edu.sdccd.cisc191.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GameServerMain {

    private static final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(PORT)
                .addService((BindableService) new GameServiceImpl())
                .build();

        server.start();

        System.out.println("1v1 gRPC Game Server started on port " + PORT);
        System.out.println("Press Ctrl+C to stop.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping gRPC Game Server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
