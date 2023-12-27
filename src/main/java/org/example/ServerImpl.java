package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements Server {
    public final int PORT;
    private final ExecutorService EXECUTOR_SERVICE;
    private final List<SocketWrapper> CONNECTS;
    private int counter;

    public ServerImpl() {
        PORT = 5555;
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(16);
        CONNECTS = new ArrayList<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server start in port: " + PORT);
            while (true) {
                SocketWrapper socketWrapper = new SocketWrapper(
                        serverSocket.accept(),
                        ++counter,
                        new SocketListener(this)
                );
                EXECUTOR_SERVICE.execute(socketWrapper);
                CONNECTS.add(socketWrapper);
                System.out.println("connect new user ip#" + socketWrapper.getInetAddress() + " port#" + socketWrapper.getPort());
                socketWrapper.sendToClient("Connect to messages server.");
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcast(int idSource, String mess) {
        for (Integer idRecipient: CONNECTS.stream().map(SocketWrapper::getID).toList()) {
            if (idRecipient != idSource) {
                send(idSource,idRecipient,mess);
            }
        }
    }


    @Override
    public void send(int idSource, int idRecipient, String mess) {
        String text = String.format("#%d: %s",idSource,mess);
        try {
            for (SocketWrapper socketWrapper:CONNECTS) {
                if (socketWrapper.getID() == idRecipient) { socketWrapper.sendToClient(text);}
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
