package me.chat.server.messages;

import junit.framework.TestCase;
import me.chat.common.Message;
import me.chat.common.Messages;
import me.chat.common.Parsable;
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
 * Date: 10/07/2014
 * Time: 12:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class SendCommandTest extends ConnectionTestCase {
    @Autowired
    private SendCommand sendCommand;

    @Autowired
    private MessageHandler messageHandler;

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
        TestCase.assertTrue(sendCommand.accept("send:"));
        TestCase.assertFalse(sendCommand.accept("undelivered:"));
    }

    @Test
    public void testCommandResult() throws Exception {
        connect(PASCAL, localHost1);
        connect(SENNEN, localHost2);
        Parsable response = sendCommand.execute("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");
        TestCase.assertEquals(response.parse(), "OK");
        Messages sennenUndeliveredMessages = messageHandler.getUndeliveredMessages(SENNEN);
        Assertions.assertThat(sennenUndeliveredMessages)
                  .containsOnly(new Message(PASCAL, SENNEN, "Hey"));
    }
}
