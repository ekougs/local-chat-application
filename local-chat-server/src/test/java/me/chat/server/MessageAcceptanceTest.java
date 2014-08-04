package me.chat.server;

import me.chat.common.Message;
import me.chat.server.messages.MessageHandler;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.Future;

import static me.chat.common.UserConstants.PASCAL;
import static me.chat.common.UserConstants.SENNEN;

/**
 * User: sennen
 * Date: 02/08/2014
 * Time: 00:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class MessageAcceptanceTest extends AcceptanceTestCase {
    @Autowired
    private MessageHandler messageHandler;

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testSendMessage() throws Exception {
        connect(PASCAL, localHost1);
        connect(SENNEN, localHost1);

        Future<String> sendAnswerFuture =
                whenCommandAsyncSent("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");

        String sendAnswer = sendAnswerFuture.get();
        Assertions.assertThat(sendAnswer).isEqualTo("OK");
        Assertions.assertThat(messageHandler.getUndeliveredMessages(SENNEN))
                  .containsOnly(new Message(PASCAL, SENNEN, "Hey"));
    }

    @Test
    public void testSendMessageRecipientNotConnected() throws Exception {
        connect(PASCAL, localHost1);

        Future<String> sendAnswerFuture =
                whenCommandAsyncSent("send:{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}");

        String sendAnswer = sendAnswerFuture.get();
        Assertions.assertThat(sendAnswer)
                  .isEqualTo(
                          "{\"requestInError\":\"send:{\\\"sender\\\":\\\"Pascal\\\",\\\"recipient\\\":\\\"Sennen\\\",\\\"body\\\":\\\"Hey\\\"}\",\"error\":\"UserNotConnectedException\"}");
    }

    @Test
    public void testGetUndeliveredMessages() throws Exception {
        connect(PASCAL, localHost2);
        connect(SENNEN, localHost1);
        messageHandler.sendMessage(new Message(PASCAL, SENNEN, "Hey"));

        Future<String> userUndeliveredMessagesFuture = whenCommandAsyncSent("undelivered:Sennen");

        String undeliveredMessages = userUndeliveredMessagesFuture.get();

        Assertions.assertThat(undeliveredMessages)
                  .isEqualTo("[{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}]");
    }

    @Test
    public void testGetUndeliveredNoMessage() throws Exception {
        connect(SENNEN, localHost1);

        Future<String> userUndeliveredMessagesFuture = whenCommandAsyncSent("undelivered:Sennen");

        String undeliveredMessages = userUndeliveredMessagesFuture.get();

        Assertions.assertThat(undeliveredMessages)
                  .isEqualTo("{\"requestInError\":\"undelivered:Sennen\",\"error\":\"NoMessageException\"}");
    }
}
