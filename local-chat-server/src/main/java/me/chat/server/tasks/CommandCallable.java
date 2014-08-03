package me.chat.server.tasks;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;
import me.chat.server.commands.GlobalCommand;
import me.chat.server.server.Request;

import java.util.concurrent.Callable;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 14:56
 */
public class CommandCallable implements Callable<Parsable> {
    private final Request request;
    private final Command command;

    CommandCallable(Request request, GlobalCommand command) {
        this.command = command;
        this.request = request;
    }

    @Override
    public Parsable call() throws Exception {
        return command.execute(request.getCommand());
    }

    public String getRequestingUser() {
        return command.getRequestingUser(request.getCommand());
    }

    public String getRequest() {
        return request.getCommand();
    }
}
