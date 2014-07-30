package me.chat.server.users;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;
import me.chat.server.commands.RequestParsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 12:26
 */
@Component
@Qualifier("childCommand")
public class DisconnectUserCommand implements Command {
    private static final String REQUEST_PREFIX = "disconnect:";

    @Autowired
    private UsersManager usersManager;

    @Override
    public boolean accept(String request) {
        return RequestParsers.accept(REQUEST_PREFIX, request);
    }

    @Override
    public Parsable execute(String request) {
        String user = RequestParsers.extractBody(REQUEST_PREFIX, request);
        usersManager.disconnect(user);
        return Parsable.OK_PARSABLE;
    }
}
