package me.chat.server.users;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;
import me.chat.server.commands.RequestParsers;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 12:26
 */
@Component
@Qualifier("childCommand")
public class ConnectUserCommand implements Command {
    private static final String REQUEST_PREFIX = "connect:";

    @Autowired
    private UsersManager usersManager;

    @Override
    public boolean accept(String request) {
        return RequestParsers.accept(REQUEST_PREFIX, request);
    }

    @Override
    public Parsable execute(String request) {
        String formattedUserConnection = RequestParsers.extractBody(REQUEST_PREFIX, request);
        UserConnection userConnection;
        try {
            userConnection = new ObjectMapper().readValue(formattedUserConnection, UserConnection.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        usersManager.connect(userConnection);
        return Parsable.OK_PARSABLE;
    }
}
