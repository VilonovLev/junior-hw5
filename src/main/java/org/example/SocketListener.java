package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketListener {
    Server server;
    Pattern pattern = Pattern.compile("^@[\\d?]+");

    public SocketListener(Server server) {
        this.server = server;
    }

    public void send(int idSource, String message) {
        message = message.trim();
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            for (var addressee: extractedAddresseesFrom(message)) {
                int idRecipient = Integer.parseInt(addressee);
                server.send(idSource,idRecipient,message);
            }
        } else {
            server.broadcast(idSource, message);
        }


    }

    private Queue<String> extractedAddresseesFrom(String message) {
        Queue<String> addressees = new LinkedList<>();
        while (true) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                addressees.add(matcher.group(0).replaceAll("@", ""));
                message = message.substring(addressees.peek().length() + 1);
            } else {
                return addressees;
            }
        }

    }
}
