package me.chat.server.users;

import org.codehaus.jackson.annotate.JsonProperty;

import java.net.InetSocketAddress;

/**
 * User: sennen
 * Date: 02/08/2014
 * Time: 14:21
 */
public class UserConnection {
    private final String user;
    private final String address;
    private final int port;

    public UserConnection(@JsonProperty("user") String user,
                          @JsonProperty("address") String address,
                          @JsonProperty("port") int port) {
        this.user = user;
        this.address = address;
        this.port = port;
    }

    public InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(address, port);
    }

    public String getUser() {
        return user;
    }
}
