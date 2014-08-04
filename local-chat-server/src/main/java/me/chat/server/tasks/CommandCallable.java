package me.chat.server.tasks;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;
import me.chat.server.commands.GlobalCommand;

import java.util.concurrent.Callable;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 14:56
 */
public class CommandCallable implements Callable<Parsable> {
    private final String request;
    private final Command command;

    CommandCallable(String request, GlobalCommand command) {
        this.command = command;
        this.request = request;
    }

    @Override
    public Parsable call() throws Exception {
        return command.execute(request);
    }

    public String getRequestingUser() {
        return command.getRequestingUser(request);
    }

    public String getRequest() {
        return request;
    }
}
