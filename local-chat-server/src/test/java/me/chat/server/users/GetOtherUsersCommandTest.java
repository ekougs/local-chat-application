package me.chat.server.users;

import junit.framework.TestCase;
import me.chat.common.Parsable;
import me.chat.server.InMemoryConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.chat.common.UserConstants.*;

/**
 * User: sennen
 * Date: 15/07/2014
 * Time: 02:18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class GetOtherUsersCommandTest extends ConnectionTestCase {
    @Autowired
    private GetOtherUsersCommand command;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        connect(SENNEN, localHost1);
        connect(PASCAL, localHost2);
        connect(NGUEMA, localHost1);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testCommandAcceptance() throws Exception {
        TestCase.assertTrue(command.accept("others:Sennen"));
        TestCase.assertFalse(command.accept("connect:Sennen"));
    }

    @Test
    public void testCommandExecution() throws Exception {
        Parsable otherUsers = command.execute("others:Sennen");
        TestCase.assertEquals(otherUsers.parse(), "\"Nguema\";\"Pascal\"");
    }
}
