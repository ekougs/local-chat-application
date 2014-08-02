package me.chat.server.messages;

import junit.framework.TestCase;
import me.chat.common.Message;
import me.chat.common.Messages;
import me.chat.server.InMemoryConfiguration;
import me.chat.server.users.ConnectionTestCase;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
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
public class GetUndeliveredMessagesCommandTest extends ConnectionTestCase {
    @Autowired
    private GetUndeliveredMessagesCommand command;

    @Autowired
    private SendCommand sendCommand;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testRequestAcceptance() throws Exception {
        TestCase.assertTrue(command.accept("undelivered:Sennen"));
        TestCase.assertFalse(command.accept("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}"));
    }

    @Test
    public void testRequestExecution() throws Exception {
        connect(PASCAL, localHost1);
        connect(SENNEN, localHost2);
        sendCommand.execute("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");
        Messages undeliveredMessages = command.execute("undelivered:Sennen");
        Assertions.assertThat(undeliveredMessages).containsOnly(new Message(PASCAL, SENNEN, "Hey"));
    }
}
