package me.chat.server.commands;

import junit.framework.TestCase;
import me.chat.common.Message;
import me.chat.common.Messages;
import me.chat.common.Parsable;
import me.chat.server.InMemoryConfiguration;
import me.chat.server.messages.MessageHandler;
import me.chat.server.users.UserNotConnectedException;
import me.chat.server.users.UsersManager;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.chat.common.UserConstants.*;

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

    @Before
    public void setUp() {
        usersManager.connect(PASCAL);
        usersManager.connect(SENNEN);
    }

    @Test
    public void testAcceptSendCommand() throws Exception {
        TestCase.assertTrue(
                globalCommand.accept("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}"));
    }

    @Test
    public void testAcceptUndeliveredCommand() throws Exception {
        TestCase.assertTrue(
                globalCommand.accept("undelivered:Sennen"));
    }

    @Test
    public void testAcceptConnectUserCommand() throws Exception {
        TestCase.assertTrue(globalCommand.accept("connect:Nguema"));
    }

    @Test
    public void testAcceptDisconnectUserCommand() throws Exception {
        TestCase.assertTrue(globalCommand.accept("disconnect:Sennen"));
    }

    @Test
    public void testCommandAcceptance() throws Exception {
        TestCase.assertTrue(globalCommand.accept("others:Sennen"));
    }

    @Test
    public void testSendCommandExecuted() throws Exception {
        Parsable response = globalCommand.execute(
                "send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");
        TestCase.assertEquals(response.parse(), "OK");
        Messages sennenUndeliveredMessages = messageHandler.getUndeliveredMessages(SENNEN);
        Assertions.assertThat(sennenUndeliveredMessages)
                  .containsOnly(new Message(PASCAL, SENNEN, "Hey"));
    }

    @Test
    public void testUndeliveredCommandExecuted() throws Exception {
        globalCommand.execute(
                "send:{\"sender\":\"Sennen\",\"recipient\":\"Pascal\",\"body\":\"Hey\"}");
        Parsable undeliveredMessages = globalCommand.execute("undelivered:Pascal");
        Assertions.assertThat(undeliveredMessages.parse())
                  .isEqualTo("[{\"sender\":\"Sennen\",\"recipient\":\"Pascal\",\"body\":\"Hey\"}]");
    }

    @Test
    public void testConnectUserCommandExecution() throws Exception {
        Parsable response = globalCommand.execute("connect:Nguema");
        TestCase.assertEquals("OK", response.parse());
        try {
            usersManager.executeIfConnected(NGUEMA, () -> {
            });
        } catch (UserNotConnectedException e) {
            TestCase.fail("User should be connected!");
        }
    }

    @Test(expected = UserNotConnectedException.class)
    public void testDisconnectUserCommandExecution() throws Exception {
        globalCommand.execute("connect:Nguema");
        Parsable response = globalCommand.execute("disconnect:Nguema");
        TestCase.assertEquals("OK", response.parse());
        usersManager.executeIfConnected(NGUEMA, () -> {
        });
    }

    @Test
    public void testCommandExecution() throws Exception {
        usersManager.connect(SENNEN);
        usersManager.connect(PASCAL);
        usersManager.connect(NGUEMA);
        Parsable otherUsers = globalCommand.execute("others:Sennen");
        TestCase.assertEquals(otherUsers.parse(), "\"Pascal\";\"Nguema\"");
        usersManager.disconnect(SENNEN);
        usersManager.disconnect(PASCAL);
        usersManager.disconnect(NGUEMA);
    }
}
