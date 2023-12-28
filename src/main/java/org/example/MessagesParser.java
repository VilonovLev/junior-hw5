package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesParser implements Parser {
    Server server;
    Pattern pattern = Pattern.compile("^@[\\d?]+");

    private final List<Integer> ADMINS;
    private final String PASSWORD;

    public MessagesParser(Server server) {
        this.server = server;
        ADMINS = new ArrayList<>();
        PASSWORD = "123";
    }

    public void parse(int idSource, String message) {
        message = message.trim();
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            for (var addressee: extractedAddresseesFrom(message)) {
                int idRecipient = Integer.parseInt(addressee);
                server.unicast(idSource,idRecipient,message);
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

    //            if (idRecipient == 0) {
//                technicalRequest(idSource,message.replaceAll("@0","").trim());
//            }

    //    private void addAdmin(int idSource, String message) {
//        if(message.equals(PASSWORD)) {
//            ADMINS.add(idSource);
//            unicast(0,idSource,"access approved.");
//        } else {
//            unicast(0,idSource,"access denied.");
//        }
//    }

    //    private void technicalRequest(int idSource, String message) throws IOException {
//        if (!ADMINS.contains(idSource)) {
//            addAdmin(idSource,message);
//        } else {
//            checkCommand(message);
//        }
//    }

//    private void checkCommand(String message) throws IOException {
//        Pattern pattern = Pattern.compile("drop\\d+");
//        Matcher matcher = pattern.matcher(message);
//        if (matcher.find()) {
//            message = message.replaceAll("drop", "");
//            drop(Integer.parseInt(message));
//        }
//    }
}
