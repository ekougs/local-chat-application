package me.chat.server.server;

import java.net.InetSocketAddress;

/**
 * User: sennen
 * Date: 01/08/2014
 * Time: 23:35
 */
public class Request {
    private final String command;

    public Request(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Request{" +
               "command='" + command + '\'' +
               '}';
    }
}
