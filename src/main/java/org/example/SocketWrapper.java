package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class SocketWrapper implements Runnable {
    public final Socket SOCKET;
    private final Parser PARSER;
    private final int ID;

    public SocketWrapper(Socket socket, Parser parser, int ID) {
        this.SOCKET = socket;
        this.PARSER = parser;
        this.ID = ID;
    }

    @Override
    public void run() {
        try (Scanner input = new Scanner(SOCKET.getInputStream())){
            while (!SOCKET.isClosed()) {
                if (input.hasNext()) {
                    String message = input.nextLine();
                    PARSER.parse(ID,message);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToClient(String message) throws IOException {
        PrintWriter output = new PrintWriter(SOCKET.getOutputStream(), true);
        output.println(message);
    }

    public InetAddress getInetAddress() {
        return SOCKET.getInetAddress();
    }

    public int getPort() {
        return SOCKET.getPort();
    }

    public int getID() { return ID; }
}