package me.chat.server.messages;

import me.chat.common.Messages;
import me.chat.server.commands.Command;
import me.chat.server.commands.RequestParsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * User: sennen
 * Date: 11/07/2014
 * Time: 18:17
 */
@Component
@Qualifier("childCommand")
public class GetUndeliveredMessagesCommand implements Command<Messages> {
    private static final String REQUEST_PREFIX = "undelivered:";

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public boolean accept(String request) {
        return RequestParsers.accept(REQUEST_PREFIX, request);
    }

    @Override
    public Messages execute(String request) {
        String user = getUser(request);
        return messageHandler.getUndeliveredMessages(user);
    }

    @Override
    public String getRequestingUser(String request) {
        return getUser(request);
    }

    private String getUser(String request) {
        return RequestParsers.extractBody(REQUEST_PREFIX, request);
    }
}
