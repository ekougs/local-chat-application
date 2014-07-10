package me.chat.server.messages;

import me.chat.common.Message;
import me.chat.common.Messages;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:11
 */
public interface MessageHandler {
    void sendMessage(Message message);

    Messages getUndeliveredMessages(String user);
}
