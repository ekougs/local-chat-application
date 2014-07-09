package me.chat.common;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 13:41
 */
@Immutable
public class Message implements Parsable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Message.class);
    private final String sender;
    private final String recipient;
    private final String body;

    public Message(String sender, String recipient, String body) {
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;

        if (body != null ? !body.equals(message.body) : message.body != null) {
            return false;
        }
        if (recipient != null ? !recipient.equals(message.recipient) : message.recipient != null) {
            return false;
        }
        return !(sender != null ? !sender.equals(message.sender) : message.sender != null);

    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (recipient != null ? recipient.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", body='" + body + '\'' +
                '}';
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
