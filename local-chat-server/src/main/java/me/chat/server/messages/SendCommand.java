package me.chat.server.messages;

import me.chat.common.Message;
import me.chat.common.Parsable;
import me.chat.common.TranslationException;
import me.chat.server.command.Command;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * User: sennen
 * Date: 10/07/2014
 * Time: 12:38
 */
@Component
@Qualifier("childCommand")
public class SendCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendCommand.class);
    private static final String REQUEST_PREFIX = "send:";

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public boolean accept(String request) {
        return request.startsWith(REQUEST_PREFIX);
    }

    @Override
    public Parsable get(String request) {
        String formattedMessage = request.substring(REQUEST_PREFIX.length());
        try {
            Message messageToSend = new ObjectMapper().readValue(formattedMessage, Message.class);
            messageHandler.sendMessage(messageToSend);
            return Parsable.OK_PARSABLE;
        } catch (IOException e) {
            LOGGER.error("Error during object command", e);
            throw new TranslationException();
        }
    }
}
