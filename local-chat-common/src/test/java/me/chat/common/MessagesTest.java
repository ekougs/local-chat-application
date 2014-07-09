package me.chat.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static me.chat.common.UserConstants.PASCAL;
import static me.chat.common.UserConstants.SENNEN;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 13:34
 */
public class MessagesTest {
    @Test
    public void testMessagesToJSON() throws Exception {
        Messages messages =
                new Messages(Arrays.asList(new Message(PASCAL, SENNEN, "Hey"),
                                           new Message(PASCAL, SENNEN, "How are you doing ?")));
        Assert.assertEquals("[{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"Hey\"}," +
                            "{\"sender\":\"Pascal\",\"recipient\":\"Sennen\",\"body\":\"How are you doing ?\"}]",
                            messages.parse());
    }
}
