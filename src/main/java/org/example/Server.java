package org.example;

public interface Server extends Runnable{
    void send(int idSource, int idRecipient, String mess);
    void broadcast(int idSource, String mess);

}
