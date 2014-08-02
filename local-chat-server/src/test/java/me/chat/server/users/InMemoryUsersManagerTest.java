package me.chat.server.users;

import me.chat.server.InMemoryConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static me.chat.common.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 14:47
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class InMemoryUsersManagerTest extends ConnectionTestCase {
    @Autowired
    private UsersManager usersManager;

    @Autowired
    private ExecutorService executor;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testConcurrentAdditions() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        connect(SENNEN, localHost1);
        executor.submit(getConnectionRunnable(PASCAL, localHost2, latch));
        executor.submit(getConnectionRunnable(NGUEMA, localHost1, latch));
        latch.await();
        assertThat(usersManager.getOtherUsers(SENNEN)).containsOnly(PASCAL, NGUEMA);
        assertThat(usersManager.getOtherUsers(PASCAL)).containsOnly(SENNEN, NGUEMA);
        assertThat(usersManager.getAddress(SENNEN)).isEqualTo(localHost1);
        assertThat(usersManager.getAddress(PASCAL)).isEqualTo(localHost2);
    }
    @Test
    public void testRemoval() throws Exception {
        connect(SENNEN, localHost1);
        connect(PASCAL, localHost1);
        connect(NGUEMA, localHost2);
        connect(EKOUGS, localHost2);
        assertThat(usersManager.getOtherUsers(SENNEN)).containsOnly(PASCAL, NGUEMA, EKOUGS);
        usersManager.disconnect(SENNEN);
        usersManager.disconnect(EKOUGS);
        assertThat(usersManager.getOtherUsers(SENNEN)).containsOnly(PASCAL, NGUEMA);
        assertThat(usersManager.getOtherUsers(PASCAL)).containsOnly(NGUEMA);
    }

    @Test
    public void testExecutionIfConnected() throws Exception {
        connect(SENNEN, localHost2);
        usersManager.executeIfConnected(SENNEN, () -> {
        });
    }

    @Test(expected = UserNotConnectedException.class)
    public void testExecutionIfNotConnected() throws Exception {
        usersManager.executeIfConnected(DISCONNECTED, () -> {
        });
    }

    @Test(expected = UserNameAlreadyUsedException.class)
    public void testTryingToConnectUserAlreadyConnected() throws Exception {
        connect(SENNEN, localHost1);
        connect(SENNEN, localHost2);
    }

    private Runnable getConnectionRunnable(String user, InetSocketAddress address, CountDownLatch latch) {
        return () -> {
            connect(user, address);
            latch.countDown();
        };
    }
}
