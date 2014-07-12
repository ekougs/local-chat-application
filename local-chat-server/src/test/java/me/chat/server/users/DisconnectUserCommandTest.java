package me.chat.server.users;

import junit.framework.TestCase;
import me.chat.common.Parsable;
import me.chat.server.InMemoryConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.chat.common.UserConstants.SENNEN;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 13:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class DisconnectUserCommandTest {
    @Autowired
    private DisconnectUserCommand command;

    @Autowired
    private UsersManager usersManager;

    @Test
    public void testAcceptConnectUserCommand() throws Exception {
        TestCase.assertFalse(command.accept("connect:Sennen"));
        TestCase.assertTrue(command.accept("disconnect:Sennen"));
    }

    @Test(expected = UserNotConnectedException.class)
    public void testConnectUserCommandExecution() throws Exception {
        usersManager.connect("Sennen");
        Parsable response = command.execute("disconnect:Sennen");
        TestCase.assertEquals("OK", response.parse());
        usersManager.executeIfConnected(SENNEN, () -> {
        });
    }
}
