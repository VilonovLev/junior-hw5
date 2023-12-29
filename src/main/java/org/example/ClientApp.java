package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static final int SERVER_PORT = 5555;
    public static final String URI = "localhost";

    public static void main(String[] args) {
        try(Socket socket = new Socket(URI, SERVER_PORT)) {
            new Thread(getOutputChannel(socket)).start();
            listenInputChannel(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void listenInputChannel(Socket socket) throws IOException{
        Scanner input = new Scanner(socket.getInputStream());
        while (!socket.isClosed()) {
            if (input.hasNext()) {
                System.out.println(input.nextLine());
            }
        }
    }

    private static Runnable getOutputChannel(Socket socket) throws IOException {
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        Scanner consoleScanner = new Scanner(System.in);
        return () -> {
            while (!socket.isClosed()) {
                output.println(consoleScanner.nextLine());
            }
        };
    }
}
