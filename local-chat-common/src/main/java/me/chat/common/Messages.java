package me.chat.common;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:13
 */
public class Messages implements Iterable<Message>, Parsable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Messages.class);
    private final List<Message> messages;

    public Messages(List<Message> messages) {
        this.messages = new ArrayList<>();
        for (Message message : messages) {
            this.messages.add(message);
        }
    }

    @Override
    public Iterator<Message> iterator() {
        return messages.iterator();
    }

    @Override
    public String parse() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            LOGGER.error("Error during object translation", e);
            throw new TranslationException();
        }
    }
}
