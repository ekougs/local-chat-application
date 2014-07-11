package me.chat.server.messages;

import junit.framework.TestCase;
import me.chat.common.Message;
import me.chat.common.Messages;
import me.chat.server.InMemoryConfiguration;
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
 * Date: 11/07/2014
 * Time: 18:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class GetUndeliveredMessagesCommandTest {
    @Autowired
    private GetUndeliveredMessagesCommand command;

    @Autowired
    private SendCommand sendCommand;

    @Autowired
    private UsersManager usersManager;

    @Test
    public void testRequestAcceptance() throws Exception {
        TestCase.assertTrue(command.accept("undelivered:Sennen"));
        TestCase.assertFalse(command.accept("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}"));
    }

    @Test
    public void testRequestExecution() throws Exception {
        usersManager.connect(PASCAL);
        usersManager.connect(SENNEN);
        sendCommand.execute("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");
        Messages undeliveredMessages = command.execute("undelivered:Sennen");
        Assertions.assertThat(undeliveredMessages).containsOnly(new Message(PASCAL, SENNEN, "Hey"));
    }
}
