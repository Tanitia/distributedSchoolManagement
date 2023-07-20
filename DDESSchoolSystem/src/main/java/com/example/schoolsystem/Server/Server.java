package com.example.schoolsystem.Server;

import com.example.schoolsystem.Testing.Mocker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int port = 8080;
    private final ServerSocket serverSocket;

    private ExecutorService pool = Executors.newFixedThreadPool(1000);

    public Server() throws IOException {
        //sets up socket and initialises mocker
        this.serverSocket = new ServerSocket(port);
        Mocker.createUsers();
        Mocker.initialiseMessages();
        Mocker.createClasses();
    }

    public void startServer() {
        try {
            while (true) {
                //accept new connections
                System.out.println("SERVER: waiting for connection");
                Socket client = this.serverSocket.accept();
                System.out.println("SERVER: A client has connected");
                ClientHandler clientHandler = new ClientHandler(client);
                //multi threading
                this.pool.execute(clientHandler);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
