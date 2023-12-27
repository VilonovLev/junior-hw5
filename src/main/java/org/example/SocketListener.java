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

    public void send(int idSource, String mes) {
        mes = mes.trim();
        Matcher matcher = pattern.matcher(mes);

        if (matcher.find()) {
            for (var str:extracted(mes)) {
                server.send(idSource,Integer.parseInt(str),mes.replaceAll("@", ""));
            }
        } else {
            server.broadcast(idSource, mes);
        }


    }

    private Queue<String> extracted(String mes) {
        Queue<String> strings = new LinkedList<>();
        while (true) {
            Matcher matcher = pattern.matcher(mes);
            if (matcher.find()) {
                strings.add(matcher.group(0).replaceAll("@", ""));
                mes = mes.substring(strings.peek().length() + 1);
            } else {
                return strings;
            }
        }

    }
}
