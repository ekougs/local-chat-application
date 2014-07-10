package me.chat.server.command;

import junit.framework.TestCase;
import me.chat.common.Message;
import me.chat.common.Messages;
import me.chat.common.Parsable;
import me.chat.server.InMemoryConfiguration;
import me.chat.server.messages.MessageHandler;
import me.chat.server.users.UsersManager;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.chat.common.UserConstants.PASCAL;
import static me.chat.common.UserConstants.SENNEN;

/**
 * User: sennen
 * Date: 10/07/2014
 * Time: 23:29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class GlobalCommandTest {
    @Autowired
    private UsersManager usersManager;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private GlobalCommand globalCommand;

    @Test
    public void testAcceptSendCommand() throws Exception {
        TestCase.assertTrue(
                globalCommand.accept("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}"));
    }

    @Test
    public void testSendCommandExecuted() throws Exception {
        usersManager.connect(PASCAL);
        usersManager.connect(SENNEN);
        Parsable response = globalCommand.execute(
                "send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");
        TestCase.assertEquals(response.parse(), "OK");
        Messages sennenUndeliveredMessages = messageHandler.getUndeliveredMessages(SENNEN);
        Assertions.assertThat(sennenUndeliveredMessages)
                  .containsOnly(new Message(PASCAL, SENNEN, "Hey"));
    }
}
