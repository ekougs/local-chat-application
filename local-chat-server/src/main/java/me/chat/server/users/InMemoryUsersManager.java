package me.chat.server.users;

import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 14:11
 */
@Component
public class InMemoryUsersManager implements UsersManager {
    private final Lock usersLock = new ReentrantLock();
    @GuardedBy("usersLock")
    private final Map<String, InetSocketAddress> users = new HashMap<>();

    @Override
    public void connect(@Nonnull UserConnection userConnection) {
        usersLock.lock();
        String user = userConnection.getUser();
        try {
            if (!isConnected(user)) {
                users.put(user, userConnection.getInetSocketAddress());
            } else {
                throw new UserNameAlreadyUsedException();
            }
        } finally {
            usersLock.unlock();
        }
    }

    @Override
    public void executeIfConnected(@Nonnull String user,
                                   @Nonnull Runnable taskToExecute) throws UserNotConnectedException {
        usersLock.lock();
        try {
            if (isConnected(user)) {
                taskToExecute.run();
            } else {
                throw new UserNotConnectedException();
            }
        } finally {
            usersLock.unlock();
        }
    }

    @Override
    public void disconnect(@Nonnull String user) {
        usersLock.lock();
        users.remove(user);
        usersLock.unlock();
    }

    @Override
    public Iterable<String> getOtherUsers(@Nonnull final String user) {
        usersLock.lock();
        Iterator<String> otherUsersIterator =
                users.keySet().stream().filter((currentUser) -> !user.equals(currentUser)).iterator();
        usersLock.unlock();
        return () -> otherUsersIterator;
    }

    @Override
    public InetSocketAddress getAddress(@Nonnull String user) {
        usersLock.lock();
        InetSocketAddress address = users.get(user);
        usersLock.unlock();
        return address;
    }

    private boolean isConnected(@Nonnull String user) {
        usersLock.lock();
        boolean isConnected = users.keySet().contains(user);
        usersLock.unlock();
        return isConnected;
    }
}
