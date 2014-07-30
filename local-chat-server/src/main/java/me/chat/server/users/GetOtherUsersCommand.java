package me.chat.server.users;

import me.chat.common.Parsable;
import me.chat.server.commands.Command;
import me.chat.server.commands.RequestParsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * User: sennen
 * Date: 15/07/2014
 * Time: 02:18
 */
@Component
@Qualifier("childCommand")
public class GetOtherUsersCommand implements Command {
    private static final String REQUEST_PREFIX = "others:";

    @Autowired
    private UsersManager usersManager;

    @Override
    public boolean accept(String request) {
        return RequestParsers.accept(REQUEST_PREFIX, request);
    }

    @Override
    public Parsable execute(String request) {
        String user = RequestParsers.extractBody(REQUEST_PREFIX, request);
        return new StringIterableParsable(usersManager.getOtherUsers(user));
    }

    private static class StringIterableParsable implements Parsable {
        private final Iterable<String> strings;

        private StringIterableParsable(Iterable<String> strings) {
            this.strings = strings;
        }

        @Override
        public String parse() {
            StringBuilder parsedStrings = new StringBuilder();
            for (String string : strings) {
                parsedStrings.append('"').append(string).append('"').append(";");
            }
            return parsedStrings.substring(0, parsedStrings.length() - 1);
        }
    }
}
