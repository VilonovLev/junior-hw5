package org.example;

public interface Server extends Runnable{
    void send(int idSource, int idRecipient, String message);
    void broadcast(int idSource, String mess);

}
