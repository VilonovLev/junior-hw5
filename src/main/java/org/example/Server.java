package org.example;

import java.io.IOException;

public interface Server extends Runnable{
    void unicast(int idSource, int idRecipient, String message);
    void broadcast(int idSource, String mess);

    void drop(int parseInt) throws IOException;
}
