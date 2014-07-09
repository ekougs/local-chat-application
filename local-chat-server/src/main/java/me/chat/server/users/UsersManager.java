package me.chat.server.users;

import javax.annotation.Nonnull;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:27
 */
public interface UsersManager {
    public void connect(@Nonnull String user);

    public void executeIfConnected(@Nonnull String user, @Nonnull Runnable taskToExecute) throws UserNotConnectedException;

    public void disconnect(@Nonnull String user);

    public Iterable<String> getOtherUsers(@Nonnull final String user);

}
