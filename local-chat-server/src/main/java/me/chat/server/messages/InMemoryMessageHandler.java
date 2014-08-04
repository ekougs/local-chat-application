package me.chat.server.messages;

import com.google.common.collect.ArrayListMultimap;
import me.chat.common.Message;
import me.chat.common.Messages;
import me.chat.common.exception.NoMessageException;
import me.chat.common.exception.UserNotConnectedException;
import me.chat.server.users.UsersManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.GuardedBy;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:15
 */
@Component
public class InMemoryMessageHandler implements MessageHandler {
    private final Lock messagesLock = new ReentrantLock();
    @GuardedBy("messagesLock")
    private final ArrayListMultimap<String, Message> undeliveredMessages = ArrayListMultimap.create();
    @Autowired
    private UsersManager usersManager;

    @Override
    public void sendMessage(Message message) throws UserNotConnectedException {
        String recipient = message.getRecipient();
        usersManager.executeIfConnected(recipient, () -> {
            messagesLock.lock();
            undeliveredMessages.put(recipient, message);
            messagesLock.unlock();
        });
    }

    @Override
    public Messages getUndeliveredMessages(String user) throws NoMessageException {
        messagesLock.lock();
        Messages messages;
        try {
            List<Message> messageList = undeliveredMessages.get(user);
            if (messageList.isEmpty()) {
                throw new NoMessageException();
            }
            messages = new Messages(messageList);
            undeliveredMessages.removeAll(user);
        } finally {
            messagesLock.unlock();
        }
        return messages;
    }
}
