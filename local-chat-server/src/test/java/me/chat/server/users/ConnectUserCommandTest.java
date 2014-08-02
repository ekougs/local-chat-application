package me.chat.server.users;

import junit.framework.TestCase;
import me.chat.common.Parsable;
import me.chat.server.InMemoryConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetSocketAddress;

import static me.chat.common.UserConstants.SENNEN;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 12:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class ConnectUserCommandTest extends ConnectionTestCase {
    @Autowired
    private ConnectUserCommand command;

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
    public void testAcceptConnectUserCommand() throws Exception {
        TestCase.assertTrue(command.accept("connect:Sennen"));
        TestCase.assertFalse(command.accept("disconnect:Sennen"));
    }

    @Test
    public void testConnectUserCommandExecution() throws Exception {
        Parsable response = command.execute("connect:{\"user\":\"Sennen\",\"address\":\"127.0.0.1\",\"port\":5555}");
        TestCase.assertEquals("OK", response.parse());
        try {
            usersManager.executeIfConnected(SENNEN, () -> {
            });
        } catch (UserNotConnectedException e) {
            TestCase.fail("User should be connected!");
        }
        Assertions.assertThat(usersManager.getAddress(SENNEN)).isEqualTo(new InetSocketAddress("127.0.0.1", 5555));
    }
}
