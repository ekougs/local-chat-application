package me.chat.common;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 13:41
 */
public class Message {
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
}
