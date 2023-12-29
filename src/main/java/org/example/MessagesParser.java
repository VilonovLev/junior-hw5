package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesParser implements Parser {
    private final Server SERVER;
    private final List<Integer> ADMINS;
    private final String PASSWORD;
    private final Pattern PRIVATE_MESSAGE;

    public MessagesParser(Server server) {
        this.SERVER = server;
        this.ADMINS = new ArrayList<>();
        this.PASSWORD = "password";
        this.PRIVATE_MESSAGE = Pattern.compile("^@[\\d?]+");

    }

    public void parse(int idSource, String message) {
        message = message.trim();
        Matcher matcher = PRIVATE_MESSAGE.matcher(message);
        try {
            if (matcher.find()) {
                parsePrivateMessage(idSource,message);
            } else {
                SERVER.broadcast(idSource, message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parsePrivateMessage(int idSource, String message) throws IOException {
        List<Integer> addressees = new ArrayList<>();
        extractedAddressFrom(message,addressees);

        for (int idRecipient: addressees) {
            if (idRecipient == 0) {
                technicalRequest(idSource,message);
            } else {
                SERVER.unicast(idSource, idRecipient, message);
            }
        }
    }

    private void extractedAddressFrom(String message, List<Integer> addressList) {
        Matcher matcher = PRIVATE_MESSAGE.matcher(message);
        if (matcher.find()) {
            String res = matcher.group(0);
            message = message.substring(res.length());
            res = res.replaceAll("@", "");
            addressList.add(Integer.parseInt(res));
            extractedAddressFrom(message.trim(),addressList);
        }
    }

    private void technicalRequest(int idSource, String message) throws IOException {
        if (!ADMINS.contains(idSource)) {
            addAdmin(idSource,message);
        } else {
            checkCommand(message);
        }
    }

    private void addAdmin(int idSource, String message) {
        if(message.equals(PASSWORD)) {
            ADMINS.add(idSource);
            SERVER.unicast(0,idSource,"access approved.");
        } else {
            SERVER.unicast(0,idSource,"access denied.");
        }
    }

    private void checkCommand(String message) throws IOException {
        Pattern pattern = Pattern.compile("drop\\d+");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            message = message.replaceAll("drop", "");
            SERVER.drop(Integer.parseInt(message));
        }
    }
}
