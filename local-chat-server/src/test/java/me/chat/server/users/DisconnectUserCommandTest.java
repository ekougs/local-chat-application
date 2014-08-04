package me.chat.server.users;

import junit.framework.TestCase;
import me.chat.common.Parsable;
import me.chat.common.exception.UserNotConnectedException;
import me.chat.server.InMemoryConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
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
public class DisconnectUserCommandTest extends ConnectionTestCase {
    @Autowired
    private DisconnectUserCommand command;

    @Autowired
    private UsersManager usersManager;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testAcceptDisconnectUserCommand() throws Exception {
        TestCase.assertFalse(command.accept("connect:Sennen"));
        TestCase.assertTrue(command.accept("disconnect:Sennen"));
    }

    @Test(expected = UserNotConnectedException.class)
    public void testDisconnectUserCommandExecution() throws Exception {
        connect("Sennen", localHost1);
        Parsable response = command.execute("disconnect:Sennen");
        TestCase.assertEquals("OK", response.parse());
        usersManager.executeIfConnected(SENNEN, () -> {
        });
    }

    @Test
    public void testRequestingUser() throws Exception {
        String requestingUser = command.getRequestingUser("disconnect:Sennen");
        Assertions.assertThat(requestingUser).isEqualTo("Sennen");
    }
}
