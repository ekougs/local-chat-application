package me.chat.server.server;

import me.chat.common.Parsable;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * User: sennen
 * Date: 31/07/2014
 * Time: 00:02
 */
public class RequestInError implements Parsable {
    private final String requestInError;

    public RequestInError(String requestInError) {
        this.requestInError = requestInError;
    }

    @Override
    public String parse() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            return "{requestInError:" + requestInError + "}";
        }
    }
}
