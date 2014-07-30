package me.chat.server.tasks;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;
import me.chat.server.commands.GlobalCommand;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 14:56
 */
public class CommandCallable implements Callable<Parsable> {
    private final InetSocketAddress address;
    private final Command command;
    private final String request;

    CommandCallable(InetSocketAddress address, GlobalCommand command, String request) {
        this.address = address;
        this.command = command;
        this.request = request;
    }

    @Override
    public Parsable call() throws Exception {
        return command.execute(request);
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getRequest() {
        return request;
    }
}
