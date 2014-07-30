package me.chat.server.messages;

import me.chat.common.Message;
import me.chat.server.InMemoryConfiguration;
import me.chat.server.users.UserNotConnectedException;
import me.chat.server.users.UsersManager;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.chat.common.UserConstants.*;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 13:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class InMemoryMessageRetrieverTest {
    @Autowired
    private UsersManager usersManager;
    @Autowired
    private MessageHandler messageHandler;

    @Test
    public void testSendMessageToConnectedUser() throws Exception {
        usersManager.connect(SENNEN);
        messageHandler.sendMessage(new Message(PASCAL, SENNEN, "Hey"));
    }

    @Test(expected = UserNotConnectedException.class)
    public void testSendMessageToDisconnectedUser() throws Exception {
        messageHandler.sendMessage(new Message(PASCAL, DISCONNECTED, "Hey"));
    }

    @Test
    public void testGetUndeliveredMessages() throws Exception {
        usersManager.connect(SENNEN);
        messageHandler.sendMessage(new Message(PASCAL, SENNEN, "Hey"));
        messageHandler.sendMessage(new Message(PASCAL, SENNEN, "How are you doing ?"));
        Assertions.assertThat(messageHandler.getUndeliveredMessages(SENNEN))
                  .containsOnly(new Message(PASCAL, SENNEN, "Hey"), new Message(PASCAL, SENNEN, "How are you doing ?"));
        messageHandler.sendMessage(new Message(PASCAL, SENNEN, "Hello ?"));
        Assertions.assertThat(messageHandler.getUndeliveredMessages(SENNEN))
                  .containsOnly(new Message(PASCAL, SENNEN, "Hello ?"));
    }

    @Test(expected = NoMessageException.class)
    public void testNoUndeliveredMessages() throws Exception {
        Assertions.assertThat(messageHandler.getUndeliveredMessages(DISCONNECTED));
    }
}
