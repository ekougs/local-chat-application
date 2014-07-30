package me.chat.server.tasks;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;

import java.net.InetSocketAddress;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 15:02
 */
public class Response {
    private final Parsable parsable;
    private final InetSocketAddress address;

    Response(Parsable parsable, InetSocketAddress address) {
        this.parsable = parsable;
        this.address = address;
    }

    public Parsable getParsable() {
        return parsable;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
