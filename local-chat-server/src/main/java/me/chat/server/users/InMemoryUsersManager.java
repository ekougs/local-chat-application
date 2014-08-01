package me.chat.server.users;

import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private final List<String> users = new ArrayList<>();

    @Override
    public void connect(@Nonnull String user) {
        usersLock.lock();
        if (!isConnected(user)) {
            users.add(user);
        }
        usersLock.unlock();
    }

    @Override
    public void executeIfConnected(@Nonnull String user,
                                   @Nonnull Runnable taskToExecute) throws UserNotConnectedException {
        usersLock.lock();
        if (isConnected(user)) {
            taskToExecute.run();
        } else {
            usersLock.unlock();
            throw new UserNotConnectedException();
        }
        usersLock.unlock();
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
                users.stream().filter((currentUser) -> !user.equals(currentUser)).iterator();
        usersLock.unlock();
        return () -> otherUsersIterator;
    }

    private boolean isConnected(@Nonnull String user) {
        usersLock.lock();
        boolean isConnected = users.contains(user);
        usersLock.unlock();
        return isConnected;
    }
}
