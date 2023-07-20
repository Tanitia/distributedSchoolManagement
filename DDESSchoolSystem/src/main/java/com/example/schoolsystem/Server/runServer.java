package com.example.schoolsystem.Server;

import java.io.IOException;

public class runServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }
}
