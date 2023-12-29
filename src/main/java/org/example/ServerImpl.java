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
            System.out.printf("Server start in port: %s", PORT);
            while (true) {
                SocketWrapper socketWrapper = new SocketWrapper(
                        serverSocket.accept(),
                        new MessagesParser(this),
                        ++counter
                );
                EXECUTOR_SERVICE.execute(socketWrapper);
                CONNECTS.add(socketWrapper);
                System.out.printf("connect new user ip#%s port#%s\n", socketWrapper.getInetAddress(),socketWrapper.getPort());
                socketWrapper.sendToClient("Connect to messages server.");
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void broadcast(int idSource, String message) {
        for (Integer idRecipient: CONNECTS.stream().map(SocketWrapper::getID).toList()) {
            if (idRecipient != idSource) {
                unicast(idSource,idRecipient,message);
            }
        }
    }

    @Override
    public void unicast(int idSource, int idRecipient, String message) {
        try {
            String text = String.format("#%d: %s",idSource,message);
            for (SocketWrapper socketWrapper:CONNECTS) {
                if (socketWrapper.getID() == idRecipient) { socketWrapper.sendToClient(text);}
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drop(int id) throws IOException {
        SocketWrapper socketWrapper = CONNECTS.stream()
                .filter(x -> x.getID() == id)
                .findFirst().get();
        CONNECTS.remove(socketWrapper);
        socketWrapper.SOCKET.close();
    }
}
