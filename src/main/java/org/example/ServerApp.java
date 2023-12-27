package org.example;

public class ServerApp {
    public static void main(String[] args) {
        Server server = new ServerImpl();
        server.run();
    }
}