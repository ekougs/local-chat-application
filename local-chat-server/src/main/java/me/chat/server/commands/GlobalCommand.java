package me.chat.server.commands;

import me.chat.common.Parsable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 12:38
 */
@Component
@Qualifier("global")
public class GlobalCommand implements Command {
    @Autowired
    @Qualifier("childCommand")
    private Command[] commands;

    @Override
    public boolean accept(String request) {
        for (Command command : commands) {
            if (command.accept(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Parsable execute(String request) {
        Command appropriateCommand = getCommand(request);
        return appropriateCommand.execute(request);
    }

    @Override
    public String getRequestingUser(String request) {
        return getCommand(request).getRequestingUser(request);
    }

    private Command getCommand(String request) {
        for (Command command : commands) {
            if (command.accept(request)) {
                return command;
            }
        }
        throw new CommandNotFoundException();
    }
}
