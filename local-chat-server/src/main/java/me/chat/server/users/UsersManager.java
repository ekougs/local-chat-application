package me.chat.server.users;

import me.chat.common.exception.UserNotConnectedException;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:27
 */
public interface UsersManager {
    public void connect(@Nonnull UserConnection userConnection);

    public void executeIfConnected(@Nonnull String user,
                                   @Nonnull Runnable taskToExecute) throws UserNotConnectedException;

    public void disconnect(@Nonnull String user);

    public Iterable<String> getOtherUsers(@Nonnull final String user);

    public InetSocketAddress getAddress(@Nonnull String user);
}
