package me.chat.server.server;

import java.net.InetSocketAddress;

/**
 * User: sennen
 * Date: 01/08/2014
 * Time: 23:35
 */
public class Request {
    private final InetSocketAddress clientAdress;
    private final String command;

    public Request(InetSocketAddress clientAdress, String command) {
        this.clientAdress = clientAdress;
        this.command = command;
    }

    public InetSocketAddress getClientAdress() {
        return clientAdress;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Request{" +
               "clientAdress=" + clientAdress +
               ", command='" + command + '\'' +
               '}';
    }
}
