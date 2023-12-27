package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class SocketWrapper implements Runnable {
    public final Socket SOCKET;
    private final int ID;
    private final SocketListener LISTENER;

    public SocketWrapper(Socket socket, int ID, SocketListener LISTENER) {

        this.SOCKET = socket;
        this.ID = ID;
        this.LISTENER = LISTENER;
    }

    @Override
    public void run() {
        try (Scanner input = new Scanner(SOCKET.getInputStream())){
            while (!SOCKET.isClosed()) {
                if (input.hasNext()) {
                    String mes = input.nextLine();
                    LISTENER.send(ID,mes);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToClient(String mess) throws IOException {
        PrintWriter output = new PrintWriter(SOCKET.getOutputStream(), true);
        output.println(mess);
    }

    public InetAddress getInetAddress() {
        return SOCKET.getInetAddress();
    }

    public int getPort() {
        return SOCKET.getPort();
    }

    public int getID() { return ID; }
}